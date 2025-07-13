package com.veganfood.veganfoodbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    @Value("${api.payosapi.key}")
    private String PAYOS_API;

    @Value("${api.clientid.key}")
    private String CLIENT_ID;

    @Value("${api.apikey.key}")
    private String API_KEY;

    @Value("${api.checksumkey.key}")
    private String CHECKSUM_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createPaymentLink(Integer orderId, BigDecimal totalAmount, String username) {
        int amount = totalAmount.intValue(); // Đơn vị VND
        String description = "Thanh toán VeganFood #" + orderId;
        String returnUrl = "http://localhost:3000/billing/" + orderId;
        String cancelUrl = "http://localhost:3000";

        // ✅ Tạo chữ ký
        String signature = generateSignature(amount, cancelUrl, description, orderId, returnUrl);

        // ✅ Tạo request body
        Map<String, Object> body = new HashMap<>();
        body.put("orderCode", orderId);
        body.put("amount", amount);
        body.put("description", description);
        body.put("returnUrl", returnUrl);
        body.put("cancelUrl", cancelUrl);
        body.put("signature", signature);
        body.put("buyerName", username);

        // ✅ Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", CLIENT_ID);
        headers.set("x-api-key", API_KEY);

        // ✅ Gửi request
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(PAYOS_API, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map data = (Map) response.getBody().get("data");
            if (data != null && data.containsKey("checkoutUrl")) {
                return (String) data.get("checkoutUrl");
            }
        }

        throw new RuntimeException("Không nhận được dữ liệu từ PayOS");
    }

    private String generateSignature(int amount, String cancelUrl, String description, int orderCode, String returnUrl) {
        try {
            String rawData = "amount=" + amount +
                    "&cancelUrl=" + cancelUrl +
                    "&description=" + description +
                    "&orderCode=" + orderCode +
                    "&returnUrl=" + returnUrl;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(CHECKSUM_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] hash = sha256Hmac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo chữ ký thanh toán", e);
        }
    }
}
