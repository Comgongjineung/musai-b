package com.musai.musai.service.recog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VuforiaService {

    @Value("${vuforia.access.key:}")
    private String accessKey;
    
    @Value("${vuforia.secret.key:}")
    private String secretKey;
    
    private static final String VUFORIA_URL = "https://vws.vuforia.com";

    public String registerTarget(String imageName, byte[] imageBytes, String metadata) throws Exception {
        // API 키 검증
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Vuforia API 키가 설정되지 않았습니다. application.properties에서 vuforia.access.key와 vuforia.secret.key를 설정해주세요.");
        }

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String base64Metadata = Base64.getEncoder().encodeToString(metadata.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", imageName);
        requestBody.put("width", 0.3); // 기준 폭
        requestBody.put("image", base64Image);
        requestBody.put("application_metadata", base64Metadata);

        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

        // JSON 바이트 배열로 직접 MD5 계산 (UTF-8 인코딩) - 16진수로 변환
        byte[] jsonBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(jsonBytes);
        String contentMD5 = bytesToHex(md.digest()); // 16진수 MD5 해시

        String contentType = "application/json";
        String date = getRFC1123Date();

        // 로그 출력
        System.out.println("JSON Body: " + jsonBody);
        System.out.println("JSON Bytes Length: " + jsonBytes.length);
        System.out.println("Content-MD5 (hex): " + contentMD5);
        System.out.println("Date header value: " + date);

        // String to Sign 구성 (POST 요청의 경우)
        String stringToSign = "POST\n" +
                contentMD5 + "\n" +
                contentType + "\n" +
                date + "\n" +
                "/targets";

        System.out.println("stringToSign:\n" + stringToSign);

        String signature = getHmacSHA1(stringToSign, secretKey);
        System.out.println("Signature: " + signature);
        String authHeader = "VWS " + accessKey + ":" + signature;

        HttpPost post = new HttpPost(VUFORIA_URL + "/targets");
        post.setHeader("Authorization", authHeader);
        post.setHeader("Content-Type", contentType);
        post.setHeader("Date", date);
        post.setHeader("Content-MD5", contentMD5);
        
        // Entity를 한 번만 생성
        StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
        post.setEntity(entity);

        // 헤더 정보 출력
        System.out.println("=== Request Headers ===");
        System.out.println("Access Key: " + accessKey);
        System.out.println("Secret Key: " + secretKey.substring(0, 8) + "...");
        System.out.println("Authorization: " + authHeader);
        System.out.println("Content-Type: " + contentType);
        System.out.println("Date: " + date);
        System.out.println("Content-MD5: " + contentMD5);
        System.out.println("URL: " + VUFORIA_URL + "/targets");
        System.out.println("======================");

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            
            System.out.println("Response Status: " + response.getStatusLine());
            System.out.println("Vuforia response: " + responseBody);
            
            if (statusCode >= 400) {
                throw new RuntimeException("Vuforia API 오류: " + statusCode + " - " + responseBody);
            }
            
            return responseBody;
        } catch (Exception e) {
            System.err.println("Vuforia API 호출 중 오류 발생: " + e.getMessage());
            throw e;
        }
    }

    private String getRFC1123Date() {
        // 현재 시간을 GMT로 가져오기
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        
        // RFC1123 형식으로 정확하게 포맷팅 (GMT 대신 z 사용)
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateString = formatter.format(calendar.getTime());
        
        System.out.println("Calendar time: " + calendar.getTime());
        System.out.println("Formatted date: " + dateString);
        
        return dateString;
    }

    private String getHmacSHA1(String data, String key) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    // API 키 유효성 테스트 메서드
    public String testConnection() throws Exception {
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Vuforia API 키가 설정되지 않았습니다.");
        }

        String date = getRFC1123Date();
        
        // GET 요청의 경우 빈 문자열의 MD5 해시 사용
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update("".getBytes(StandardCharsets.UTF_8));
        String contentMD5 = bytesToHex(md.digest());
        
        String stringToSign = "GET\n" +
                contentMD5 + "\n" +
                "\n" + // 빈 Content-Type
                date + "\n" +
                "/targets";
        
        String signature = getHmacSHA1(stringToSign, secretKey);
        String authHeader = "VWS " + accessKey + ":" + signature;

        HttpGet get = new HttpGet(VUFORIA_URL + "/targets");
        get.setHeader("Authorization", authHeader);
        get.setHeader("Date", date);

        System.out.println("=== Test Connection ===");
        System.out.println("Access Key: " + accessKey);
        System.out.println("Secret Key: " + secretKey.substring(0, 8) + "...");
        System.out.println("Date: " + date);
        System.out.println("Content-MD5 (empty string): " + contentMD5);
        System.out.println("String to Sign: " + stringToSign);
        System.out.println("Signature: " + signature);
        System.out.println("Authorization: " + authHeader);
        System.out.println("======================");

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {

            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            
            System.out.println("Test Response Status: " + response.getStatusLine());
            System.out.println("Test Response: " + responseBody);
            
            return "Status: " + statusCode + ", Response: " + responseBody;
        }
    }

    // 더 간단한 API 키 테스트
    public String simpleTest() throws Exception {
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Vuforia API 키가 설정되지 않았습니다.");
        }

        System.out.println("=== Simple API Key Test ===");
        System.out.println("Access Key Length: " + accessKey.length());
        System.out.println("Secret Key Length: " + secretKey.length());
        System.out.println("Access Key: " + accessKey);
        System.out.println("Secret Key (first 8 chars): " + secretKey.substring(0, 8) + "...");
        System.out.println("==========================");

        return "API 키 길이 - Access: " + accessKey.length() + ", Secret: " + secretKey.length();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
