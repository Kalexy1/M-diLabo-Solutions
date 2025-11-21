package com.medilabo.patientservice.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public final class TestJwtUtil {

    private TestJwtUtil() {}

    public static String createHs256(String secret, Map<String, Object> claims) {
        try {
            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payloadJson = toJson(claims);

            String header = b64(headerJson.getBytes(StandardCharsets.UTF_8));
            String payload = b64(payloadJson.getBytes(StandardCharsets.UTF_8));

            String unsigned = header + "." + payload;

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] sig = mac.doFinal(unsigned.getBytes(StandardCharsets.UTF_8));
            String signature = b64(sig);

            return unsigned + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String b64(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (var e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(e.getKey()).append('"').append(':');
            Object v = e.getValue();
            if (v == null) {
                sb.append("null");
            } else if (v instanceof Number || v instanceof Boolean) {
                sb.append(v.toString());
            } else if (v instanceof Iterable<?> it) {
                sb.append('[');
                boolean f2 = true;
                for (Object o : it) {
                    if (!f2) sb.append(',');
                    f2 = false;
                    if (o instanceof Number || o instanceof Boolean) {
                        sb.append(String.valueOf(o));
                    } else {
                        sb.append('"').append(String.valueOf(o)).append('"');
                    }
                }
                sb.append(']');
            } else {
                sb.append('"').append(String.valueOf(v)).append('"');
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
