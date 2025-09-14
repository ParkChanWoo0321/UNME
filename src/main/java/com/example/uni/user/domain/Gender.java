package com.example.uni.user.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("남자"),
    FEMALE("여자");

    private final String label;

    // 응답(JSON)에는 한글로 나감
    @JsonValue
    public String json() {
        return label;
    }

    // 요청(JSON)으로 한글/영문 모두 허용하고 싶을 때
    @JsonCreator
    public static Gender fromJson(String v) {
        if (v == null) return null;
        String s = v.trim();
        if ("남자".equals(s) || "male".equalsIgnoreCase(s) || "MALE".equals(s)) return MALE;
        if ("여자".equals(s) || "female".equalsIgnoreCase(s) || "FEMALE".equals(s)) return FEMALE;
        throw new IllegalArgumentException("Invalid gender: " + v);
    }
}
