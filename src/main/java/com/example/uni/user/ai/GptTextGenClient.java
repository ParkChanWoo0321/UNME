package com.example.uni.user.ai;

import com.example.uni.user.dto.DatingStyleSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Component
@RequiredArgsConstructor
@org.springframework.context.annotation.Primary
public class GptTextGenClient implements TextGenClient {

    private static final Logger log = LoggerFactory.getLogger(GptTextGenClient.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.base-url:https://api.openai.com}")
    private String baseUrl;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    /** ✅ 서버 시작 시 API 키 확인 */
    @PostConstruct
    public void checkKey() {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("[GPT] API Key 로드 실패 (null/blank)");
        } else {
            // 앞 10자리만 찍고 나머지는 마스킹
            String masked = apiKey.length() > 13
                    ? apiKey.substring(0, 10) + "..." + apiKey.substring(apiKey.length() - 3)
                    : apiKey;
            log.info("[GPT] Loaded API Key = {}", masked);
        }
    }

    private WebClient client(){
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public DatingStyleSummary summarizeDatingStyle(Map<String, String> answers) {
        String prompt = buildPrompt(answers);

        var body = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role","system","content",
                                "너는 데이팅 성향 결과를 한국어로 생성한다. " +
                                        "반드시 JSON만 출력해. 코드블록/설명/개행/이모지 금지. " +
                                        "JSON schema: {\"feature\":\"문장 1~2개, 180~220자\", " +
                                        "\"recommendedPartner\":\"문장 1개, 110~150자\", " +
                                        "\"tags\":[\"단어\",\"단어\",\"단어\"]} " +
                                        "tags는 해시 없이 3개의 핵심 단어, 중복 금지."),
                        Map.of("role","user","content", prompt)
                },
                "temperature", 0.4,
                "max_tokens", 380
        );

        try {
            if (apiKey == null || apiKey.isBlank()) {
                log.error("[GPT] openai.api-key 비어있음 → fallback 반환");
                return fallback();
            }

            ChatCompletionResponse resp = client().post()
                    .uri("/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(s -> !s.is2xxSuccessful(), r ->
                            r.bodyToMono(String.class).flatMap(b -> {
                                log.error("[GPT] HTTP {} 에러 바디: {}", r.statusCode(), b);
                                return Mono.error(new RuntimeException("OpenAI error: " + r.statusCode()));
                            })
                    )
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();

            String content = Optional.ofNullable(resp)
                    .map(ChatCompletionResponse::choices)
                    .filter(list -> !list.isEmpty())
                    .map(List::getFirst)
                    .map(Choice::message)
                    .map(Message::content)
                    .orElse(null);

            if (content == null || content.isBlank()) {
                log.error("[GPT] 빈 content 수신 → fallback. prompt={}", abbreviate(prompt));
                return fallback();
            }

            Map<String, Object> json = parseJson(content);
            String feature = clean((String) json.getOrDefault("feature", ""));
            String partner = clean((String) json.getOrDefault("recommendedPartner", ""));
            List<String> raw = toStringList(json.get("tags"));
            if (raw.isEmpty()) raw = List.of("안정감","소통","배려");
            List<String> tags = normalizeTags(raw);

            if (feature.isBlank() || partner.isBlank()) {
                log.warn("[GPT] JSON 필드 누락/비어있음 → 기본값 보강. content={}", abbreviate(content));
                if (feature.isBlank()) feature = defaultFeature();
                if (partner.isBlank()) partner = defaultPartner();
            }

            return DatingStyleSummary.builder()
                    .feature(feature)
                    .recommendedPartner(partner)
                    .tags(tags)
                    .build();

        } catch (WebClientResponseException e) {
            log.error("[GPT] HTTP 예외 status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            return fallback();
        } catch (Exception e) {
            log.error("[GPT] 호출/파싱 실패: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            return fallback();
        }
    }

    private DatingStyleSummary fallback() {
        return DatingStyleSummary.builder()
                .feature(defaultFeature())
                .recommendedPartner(defaultPartner())
                .tags(List.of("안정감","소통","배려"))
                .build();
    }

    /** Object → List<String> 안전 변환 */
    private static List<String> toStringList(Object v) {
        if (v == null) return List.of();
        if (v instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object o : list) if (o != null) out.add(String.valueOf(o));
            return out;
        }
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) return List.of();
        try {
            return MAPPER.readValue(s, new TypeReference<List<String>>() {});
        } catch (Exception ignore) {
            String[] parts = s.split("[,\\s]+");
            List<String> out = new ArrayList<>();
            for (String p : parts) if (!p.isBlank()) out.add(p);
            return out;
        }
    }

    private static List<String> normalizeTags(List<String> in){
        if (in == null) return List.of("안정감","소통","배려");
        List<String> out = new ArrayList<>();
        for (Object o : in) {
            if (o == null) continue;
            String s = clean(String.valueOf(o)).replace("#","").trim();
            if (s.isBlank()) continue;
            if (!out.contains(s)) out.add(s);
            if (out.size() == 3) break;
        }
        List<String> fill = List.of("안정감","소통","배려","재미","설렘","신뢰");
        for (String f : fill) {
            if (out.size() == 3) break;
            if (!out.contains(f)) out.add(f);
        }
        return out.subList(0, 3);
    }

    private static String clean(String s){
        if (s == null) return "";
        return s.replaceAll("[\\r\\n]+", " ").replaceAll("\\s{2,}", " ").trim();
    }

    private static Map<String,Object> parseJson(String content){
        if (content == null) return Map.of();
        String c = content.trim();
        int start = c.indexOf('{');
        int end = c.lastIndexOf('}');
        if (start >= 0 && end > start) c = c.substring(start, end + 1);
        try {
            return MAPPER.readValue(c, new TypeReference<Map<String,Object>>() {});
        } catch (Exception ignore) {
            return Map.of();
        }
    }

    private String defaultFeature(){
        return "응답을 보면 편안한 소통과 상호 배려를 중시하며, 상황에 맞게 분위기를 살리고 상대의 감정을 세심하게 살피는 편입니다. 관계에서는 신뢰를 바탕으로 안정감을 주고, 상대가 편하게 표현할 수 있도록 배려하는 강점이 돋보입니다.";
    }

    private String defaultPartner(){
        return "진솔한 대화를 통해 서로의 속도를 맞추고, 꾸준함과 신뢰를 소중히 여기는 분과 잘 어울립니다. 함께 안정적인 관계를 만들어가며 편안함을 나눌 수 있는 사람이 이상적입니다.";
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
        StringBuilder sb = new StringBuilder("아래 A/B 선택 결과를 근거로 결과를 만들어라.\n\n");
        for (int i = 0; i < 10; i++) {
            String sel = a.getOrDefault("q" + (i + 1), "");
            sb.append(i + 1).append(". ").append(qs[i][0]).append(" = ")
                    .append("a".equalsIgnoreCase(sel) ? qs[i][1] : qs[i][2]).append("\n");
        }
        sb.append("\n반드시 JSON만 출력: {\"feature\":\"...\",\"recommendedPartner\":\"...\",\"tags\":[\"단어\",\"단어\",\"단어\"]}");
        return sb.toString();
    }

    private static String abbreviate(String s) {
        if (s == null) return "";
        return s.length() > 300 ? s.substring(0, 300) + "..." : s;
    }

    /** ---- 최소 DTO (응답에서 쓰는 부분만) ---- */
    private record ChatCompletionResponse(List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String role, String content) {}
}
