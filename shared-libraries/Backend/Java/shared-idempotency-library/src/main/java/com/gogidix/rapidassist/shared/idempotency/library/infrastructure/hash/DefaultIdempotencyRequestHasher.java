package com.gogidix.rapidassist.shared.idempotency.library.infrastructure.hash;

import com.gogidix.rapidassist.shared.idempotency.library.domain.policy.IdempotencyRequestHasher;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DefaultIdempotencyRequestHasher implements IdempotencyRequestHasher {

    @Override
    public String hash(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();

        String canonical = method + " " + path + (query == null ? "" : ("?" + query));
        return sha256Hex(canonical);
    }

    private static String sha256Hex(String input) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }

        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
