package com.example.uni.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DatingStyleRequest {
    @NotNull @Pattern(regexp="^[ab]$") private String q1;
    @NotNull @Pattern(regexp="^[ab]$") private String q2;
    @NotNull @Pattern(regexp="^[ab]$") private String q3;
    @NotNull @Pattern(regexp="^[ab]$") private String q4;
    @NotNull @Pattern(regexp="^[ab]$") private String q5;
    @NotNull @Pattern(regexp="^[ab]$") private String q6;
    @NotNull @Pattern(regexp="^[ab]$") private String q7;
    @NotNull @Pattern(regexp="^[ab]$") private String q8;
    @NotNull @Pattern(regexp="^[ab]$") private String q9;
    @NotNull @Pattern(regexp="^[ab]$") private String q10;
}
