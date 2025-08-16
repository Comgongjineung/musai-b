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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VuforiaService {

    private static final Logger log = LoggerFactory.getLogger(VuforiaService.class);

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
        String base64Metadata = Base64.getEncoder().encodeToString(
            (metadata != null ? metadata : "").getBytes(StandardCharsets.UTF_8)
        );

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", imageName);
        requestBody.put("width", 0.3); // 기준 폭
        requestBody.put("image", base64Image);
        requestBody.put("application_metadata", base64Metadata);

        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

        byte[] jsonBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(jsonBytes);
        String contentMD5 = bytesToHex(md.digest()); // 16진수 MD5 해시

        String contentType = "application/json";
        String date = getRFC1123Date();

        System.out.println("JSON Body: " + jsonBody);
        System.out.println("JSON Bytes Length: " + jsonBytes.length);
        System.out.println("Content-MD5 (hex): " + contentMD5);
        System.out.println("Date header value: " + date);

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

        StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
        post.setEntity(entity);

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

    public String testConnection() throws Exception {
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Vuforia API 키가 설정되지 않았습니다.");
        }

        String date = getRFC1123Date();

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

    public String findTargetIdByTitleAccurate(String title) throws Exception {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("작품 제목이 비어있습니다.");
        }

        List<String> targetIds = listTargetIds();
        for (String id : targetIds) {
            String name = getTargetName(id);
            if (name != null && name.equalsIgnoreCase(title)) {
                log.info("뷰포리아에서 target_id 찾음(정확 조회): {} -> {}", title, id);
                return id;
            }
        }
        log.warn("뷰포리아에서 작품 제목 '{}'에 해당하는 target_id를 찾을 수 없습니다.", title);
        return null;
    }

    public String ensureTargetByTitle(String title, byte[] imageBytes, String metadata) throws Exception {
        String existingId = findTargetIdByTitleAccurate(title);
        if (existingId != null) {
            return existingId;
        }

        String response = registerTarget(title, imageBytes, metadata != null ? metadata : title);

        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(response);
        String createdId = node.path("target_id").asText(null);
        if (createdId != null && !createdId.isEmpty()) {
            return createdId;
        }

        String fallbackId = findTargetIdByTitleAccurate(title);
        if (fallbackId != null) {
            return fallbackId;
        }
        throw new RuntimeException("Vuforia target 생성 또는 조회 실패: title=" + title);
    }

    private List<String> listTargetIds() throws Exception {
        String date = getRFC1123Date();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update("".getBytes(StandardCharsets.UTF_8));
        String contentMD5 = bytesToHex(md.digest());

        String stringToSign = "GET\n" +
                contentMD5 + "\n" +
                "\n" +
                date + "\n" +
                "/targets";

        String signature = getHmacSHA1(stringToSign, secretKey);
        String authHeader = "VWS " + accessKey + ":" + signature;

        HttpGet get = new HttpGet(VUFORIA_URL + "/targets");
        get.setHeader("Authorization", authHeader);
        get.setHeader("Date", date);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                log.warn("/targets 호출 실패: {} - {}", statusCode, responseBody);
                return Collections.emptyList();
            }
            ObjectMapper mapper = new ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(responseBody);
            com.fasterxml.jackson.databind.JsonNode results = root.path("results");
            if (!results.isArray()) {
                return Collections.emptyList();
            }
            List<String> ids = new ArrayList<>();
            for (com.fasterxml.jackson.databind.JsonNode item : results) {
                String id = item.asText();
                if (id != null && !id.isEmpty()) ids.add(id);
            }
            return ids;
        }
    }

    private String getTargetName(String targetId) throws Exception {
        String date = getRFC1123Date();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update("".getBytes(StandardCharsets.UTF_8));
        String contentMD5 = bytesToHex(md.digest());

        String path = "/targets/" + targetId;
        String stringToSign = "GET\n" +
                contentMD5 + "\n" +
                "\n" +
                date + "\n" +
                path;

        String signature = getHmacSHA1(stringToSign, secretKey);
        String authHeader = "VWS " + accessKey + ":" + signature;

        HttpGet get = new HttpGet(VUFORIA_URL + path);
        get.setHeader("Authorization", authHeader);
        get.setHeader("Date", date);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                log.warn("/targets/{} 호출 실패: {} - {}", targetId, statusCode, responseBody);
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(responseBody);
            // 일반적으로 target_record.name 에 위치
            String name = root.path("target_record").path("name").asText(null);
            if (name == null) {
                name = root.path("name").asText(null);
            }
            return name;
        }
    }

    public String findTargetIdByTitle(String title) throws Exception {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("작품 제목이 비어있습니다.");
        }
        
        try {
            String date = getRFC1123Date();

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

            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(get)) {

                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();
                
                if (statusCode == 200) {
                    return findTargetIdFromResponse(responseBody, title);
                } else {
                    log.warn("뷰포리아 API 호출 실패: {} - {}", statusCode, responseBody);
                    return generateTempTargetId(title);
                }
            }
            
        } catch (Exception e) {
            log.error("뷰포리아 target_id 조회 실패: {}", e.getMessage(), e);
            return generateTempTargetId(title);
        }
    }

    private String findTargetIdFromResponse(String responseBody, String title) {
        try {
            if (responseBody.contains("\"name\":\"" + title + "\"") || 
                responseBody.contains("\"name\": \"" + title + "\"")) {

                int targetIdIndex = responseBody.indexOf("\"target_id\":");
                if (targetIdIndex != -1) {
                    int startIndex = responseBody.indexOf("\"", targetIdIndex + 13) + 1;
                    int endIndex = responseBody.indexOf("\"", startIndex);
                    if (startIndex > 0 && endIndex > startIndex) {
                        String targetId = responseBody.substring(startIndex, endIndex);
                        log.info("뷰포리아에서 target_id 찾음: {} -> {}", title, targetId);
                        return targetId;
                    }
                }
            }
            
            log.warn("뷰포리아에서 작품 제목 '{}'에 해당하는 target_id를 찾을 수 없습니다.", title);
            return generateTempTargetId(title);
            
        } catch (Exception e) {
            log.error("뷰포리아 응답 파싱 실패: {}", e.getMessage(), e);
            return generateTempTargetId(title);
        }
    }

    private String generateTempTargetId(String title) {
        String tempId = "temp_" + title.hashCode() + "_" + System.currentTimeMillis();
        log.info("임시 target_id 생성: {} -> {}", title, tempId);
        return tempId;
    }
}
