package com.example.uni.user.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class DatingStyleSummary {
    private final String feature;
    private final String recommendedPartner;
    private final List<String> tags;
}
