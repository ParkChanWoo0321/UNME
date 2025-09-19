// com/example/uni/user/dto/PeerDetailResponse.java
package com.example.uni.user.dto;

import com.example.uni.user.domain.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PeerDetailResponse {
    private Long userId;
    private String name;
    private String department;
    private String studentNo;
    private String birthYear;
    private Gender gender;
    private String typeTitle;
    private String typeContent;
    private String typeImageUrl;
    private String typeImageUrl2;
    private String styleSummary;
    private String recommendedPartner;
    private List<String> tags;
    private String introduce;
    private String instagramUrl;
    private String mbti;
    private String egenType;
}
