// user/dto/ProfileOnboardingRequest.java
package com.example.uni.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class ProfileOnboardingRequest {
    @NotBlank private String name;        // 이름
    @NotBlank private String department;  // 학과
    @NotBlank private String studentNo;   // 학번
    @NotNull  private Integer age;        // 나이
}
