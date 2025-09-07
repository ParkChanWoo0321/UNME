package com.example.uni.user.service;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.DatingStyleRequest;
import com.example.uni.user.repo.UserRepository;
import com.example.uni.user.ai.TextGenClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DatingStyleService {

    private final UserRepository userRepository;
    private final ObjectMapper om;
    private final TextGenClient textGenClient;

    private User get(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
    }

    @Transactional
    public Map<String,Object> complete(UUID meId, DatingStyleRequest req){
        User u = get(meId);
        if (u.isDatingStyleCompleted()) throw new ApiException(ErrorCode.CONFLICT);

        Map<String,String> answers = new LinkedHashMap<>();
        answers.put("q1", req.getQ1()); answers.put("q2", req.getQ2());
        answers.put("q3", req.getQ3()); answers.put("q4", req.getQ4());
        answers.put("q5", req.getQ5()); answers.put("q6", req.getQ6());
        answers.put("q7", req.getQ7()); answers.put("q8", req.getQ8());
        answers.put("q9", req.getQ9()); answers.put("q10", req.getQ10());

        String summary = textGenClient.summarizeDatingStyle(answers);

        try { u.setDatingStyleAnswersJson(om.writeValueAsString(answers)); }
        catch (Exception ignored) { u.setDatingStyleAnswersJson("{}"); }

        u.setDatingStyleSummary(summary);
        u.setDatingStyleCompleted(true);
        userRepository.save(u);

        return Map.of("summary", summary, "completed", true);
    }
}