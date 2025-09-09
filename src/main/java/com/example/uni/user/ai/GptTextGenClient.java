package com.example.uni.user.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@org.springframework.context.annotation.Primary
public class GptTextGenClient implements TextGenClient {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.base-url:https://api.openai.com}")
    private String baseUrl;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    private WebClient client(){
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public String summarizeDatingStyle(Map<String, String> answers) {
        String prompt = buildPrompt(answers);

        var body = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role","system","content",
                                "너는 데이팅 성향을 간결하게 요약하는 한국어 어시스턴트다. 사용자의 A/B 응답을 근거로, 성향/데이트 선호/관계 가치관을 2~3문장으로 자연스럽게 요약해. 과장·클리셰·목차·점수·헤더 금지, 친근한 존댓말."),
                        Map.of("role","user","content", prompt)
                },
                "temperature", 0.7
        );

        try {
            ChatCompletionResponse resp = client().post()
                    .uri("/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();

            String content = Optional.ofNullable(resp)
                    .map(ChatCompletionResponse::choices)
                    .filter(list -> !list.isEmpty())
                    .map(java.util.List::getFirst)   // JDK 21
                    .map(Choice::message)
                    .map(Message::content)
                    .map(String::trim)
                    .orElse(null);

            if (content != null && !content.isBlank()) return content;
        } catch (Exception ignore) {
            // fall through to fallback
        }

        return "응답을 바탕으로 편안한 소통과 상호 배려를 중시하는 데이팅 성향으로 보입니다.";
    }

    private String buildPrompt(Map<String,String> a){
        String[][] qs = new String[][]{
                {"새로운 사람 만남 선호","친구들과 함께하는 시끌벅적한 모임(a)","조용한 1:1 대화(b)"},
                {"상대 선택 기준","유머러스/즐거움(a)","진지한 대화(b)"},
                {"선호 데이트","야외 활동(a)","실내 활동(b)"},
                {"갈등 태도","직접 소통/해결(a)","경청/부드럽게 대처(b)"},
                {"외모/스타일","힙/트렌디/강렬(a)","깔끔/단정/부드러움(b)"},
                {"계획 성향","즉흥적(a)","계획적(b)"},
                {"애정 표현","솔직/직접(a)","은근/섬세(b)"},
                {"관계 가치","흥미/활력(a)","안정/신뢰(b)"},
                {"첫 대화","가볍고 친근한 질문(a)","가치관 등 깊은 질문(b)"},
                {"상호작용","분위기 주도/리드(a)","경청/맞춤(b)"}
        };
        StringBuilder sb = new StringBuilder("아래 A/B 선택 결과를 요약해줘.\n\n");
        for (int i = 0; i < 10; i++) {
            String sel = a.get("q" + (i + 1));
            sb.append(i + 1).append(". ").append(qs[i][0]).append(" = ")
                    .append("a".equalsIgnoreCase(sel) ? qs[i][1] : qs[i][2]).append("\n");
        }
        sb.append("\n출력: 2~3문장 한국어 요약(불필요한 머리말/목차/이모지 금지).");
        return sb.toString();
    }

    /** ---- 최소 DTO (응답에서 쓰는 부분만) ---- */
    private record ChatCompletionResponse(java.util.List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String role, String content) {}
}