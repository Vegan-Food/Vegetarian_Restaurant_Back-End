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
        try {
            int amount = totalAmount.intValue();

            // ✅ Tạo orderCode unique bằng cách thêm timestamp
            long timestamp = System.currentTimeMillis();
            String uniqueOrderCode = orderId + "" + (timestamp % 100000); // Lấy 5 số cuối của timestamp

            String description = "Thanh toán VeganFood #" + orderId;
            String returnUrl = "https://veganfood-five.vercel.app/billing/" + orderId;
            String cancelUrl = "https://veganfood-five.vercel.app";

            // ✅ Tạo chữ ký với orderCode unique
            String signature = generateSignature(amount, cancelUrl, description, uniqueOrderCode, returnUrl);

            // ✅ Tạo request body
            Map<String, Object> body = new HashMap<>();
            body.put("orderCode", Long.parseLong(uniqueOrderCode)); // PayOS cần số nguyên
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

            // ✅ Log để debug
            System.out.println("🔍 Unique OrderCode: " + uniqueOrderCode);
            System.out.println("🔍 Amount: " + amount);
            System.out.println("🔍 PayOS URL: " + PAYOS_API);

            // ✅ Gửi request
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(PAYOS_API, request, Map.class);

            System.out.println("📤 PayOS Response: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map responseBody = response.getBody();

                // ✅ Check error code trong response
                if (responseBody.containsKey("code") && !"00".equals(responseBody.get("code"))) {
                    throw new RuntimeException("PayOS Error: " + responseBody.get("desc"));
                }

                Map data = (Map) responseBody.get("data");
                if (data != null && data.containsKey("checkoutUrl")) {
                    return (String) data.get("checkoutUrl");
                }
            }

            throw new RuntimeException("Không nhận được dữ liệu từ PayOS");

        } catch (Exception e) {
            System.err.println("❌ PayOS Error: " + e.getMessage());
            throw new RuntimeException("Lỗi tạo thanh toán: " + e.getMessage());
        }
    }

    private String generateSignature(int amount, String cancelUrl, String description, String orderCode, String returnUrl) {
        try {
            // ✅ Đảm bảo thứ tự alphabetical
            String rawData = "amount=" + amount +
                    "&cancelUrl=" + cancelUrl +
                    "&description=" + description +
                    "&orderCode=" + orderCode +
                    "&returnUrl=" + returnUrl;

            System.out.println("🔐 Signature Data: " + rawData);

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(CHECKSUM_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] hash = sha256Hmac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            String signature = hexString.toString();
            System.out.println("🔐 Generated Signature: " + signature);

            return signature;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo chữ ký thanh toán", e);
        }
    }
}
