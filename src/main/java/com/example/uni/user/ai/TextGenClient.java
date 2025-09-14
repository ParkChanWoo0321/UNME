package com.example.uni.user.ai;

import com.example.uni.user.dto.DatingStyleSummary;
import java.util.Map;

public interface TextGenClient {
    DatingStyleSummary summarizeDatingStyle(Map<String, String> answers);
}