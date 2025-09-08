package com.util;

import java.io.UncheckedIOException;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonCodec {

    private final ObjectMapper om;

    public JsonCodec(ObjectMapper om) {
        this.om = om; // 用 Spring Boot 提供/客製的全域 ObjectMapper
    }

    public String write(Object value) {
        try {
            return om.writeValueAsString(value);
        } catch (Exception e) {
            // 轉成 unchecked，DAO 不必 try/catch
            if (e instanceof java.io.IOException io) throw new UncheckedIOException(io);
            throw new JsonCodecException("JSON write failed", e);
        }
    }

    // CartItemRedis item = om.readValue(json, CartItemRedis.class);
    public <T> T read(String json, Class<T> type) {
        try {
            return om.readValue(json, type);
        } catch (Exception e) {
            if (e instanceof java.io.IOException io) throw new UncheckedIOException(io);
            throw new JsonCodecException("JSON read failed: " + type.getSimpleName(), e);
        }
    }

    // overloadding for dealing with generic type erasure
    // List<CartItemRedis> list = jsonCodec.read(json, new TypeReference<List<CartItemRedis>>() {});
    public <T> T read(String json, TypeReference<T> typeRef) {
        try {
            return om.readValue(json, typeRef);
        } catch (Exception e) {
            if (e instanceof java.io.IOException io) throw new UncheckedIOException(io);
            throw new JsonCodecException("JSON read failed (TypeReference)", e);
        }
    }

    public static class JsonCodecException extends RuntimeException {
        public JsonCodecException(String msg, Throwable cause) { super(msg, cause); }
    }
}
