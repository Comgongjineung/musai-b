package com.musai.musai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VuforiaService {

    private static final String ACCESS_KEY = "f34823e09154ac53170f35648846e6f4d904807b";
    private static final String SECRET_KEY = "726ba842e49dee97ed2de7892833c2a6a9cf71a4";
    private static final String VUFORIA_URL = "https://vws.vuforia.com";

    public String registerTarget(String imageName, byte[] imageBytes, String metadata) throws Exception {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String base64Metadata = Base64.getEncoder().encodeToString(metadata.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", imageName);
        requestBody.put("width", 0.3); // 기준 폭
        requestBody.put("image", base64Image);
        requestBody.put("application_metadata", base64Metadata);

        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

        // JSON 바이트 배열로 직접 MD5 계산 (UTF-8 인코딩)
        byte[] jsonBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(jsonBytes);
        String contentMD5 = Base64.getEncoder().encodeToString(md.digest());

        String contentType = "application/json";
        String date = getRFC1123Date();

        // 로그 출력
        System.out.println("JSON Body: " + jsonBody);
        System.out.println("JSON Bytes Length: " + jsonBytes.length);
        System.out.println("Content-MD5: " + contentMD5);
        System.out.println("Date header value: " + date);

        String stringToSign = "POST\n" +
                contentMD5 + "\n" +
                contentType + "\n" +
                date + "\n" +
                "/targets";

        System.out.println("stringToSign:\n" + stringToSign);

        String signature = getHmacSHA1(stringToSign, SECRET_KEY);
        System.out.println("Signature: " + signature);
        String authHeader = "VWS " + ACCESS_KEY + ":" + signature;

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
        System.out.println("Access Key: " + ACCESS_KEY);
        System.out.println("Secret Key: " + SECRET_KEY.substring(0, 8) + "...");
        System.out.println("Authorization: " + authHeader);
        System.out.println("Content-Type: " + contentType);
        System.out.println("Date: " + date);
        System.out.println("Content-MD5: " + contentMD5);
        System.out.println("URL: " + VUFORIA_URL + "/targets");
        System.out.println("======================");

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("Response Status: " + response.getStatusLine());
            System.out.println("Vuforia response: " + responseBody);
            return responseBody;
        }
    }

    private String getRFC1123Date() {
        // 현재 시간을 GMT로 가져오기 (시스템 시간과 관계없이)
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        
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
}
