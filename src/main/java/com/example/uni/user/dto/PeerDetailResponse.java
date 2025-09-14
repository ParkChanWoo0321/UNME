package com.example.uni.user.dto;

import com.example.uni.user.domain.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PeerDetailResponse {
    private UUID userId;
    private String name;
    private String department;
    private String studentNo;
    private String birthYear;
    private Gender gender;
    private String typeTitle;
    private String typeContent;
    private String typeImageUrl;
    private String styleSummary;
    private String recommendedPartner;
    private List<String> tags;
    private String introduce;
    private String instagramUrl;
}
