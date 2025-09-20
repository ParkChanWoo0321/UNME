// com/example/uni/user/ai/GptTextGenClient.java
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

    @PostConstruct
    public void checkKey() {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("[GPT] API Key 로드 실패 (null/blank)");
        } else {
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
                                "너는 데이팅 성향 결과를 한국어로 생성한다. 반드시 JSON만 출력해. 코드블록/설명/개행/이모지 금지. " +
                                        "A는 활동적/외향/리드/즉흥/직접, B는 조용/내향/경청/계획/섬세 의미로 본다. " +
                                        "B 경향이 많으면 egenType=EGEN, A 경향이 많으면 egenType=TETO. 애매하면 q1,q4,q8,q10을 우선 고려한다. " +
                                        "JSON schema: {\"feature\":\"문장 1~2개, 180~220자\",\"recommendedPartner\":\"문장 1개, 110~150자\",\"tags\":[\"단어\",\"단어\",\"단어\"],\"egenType\":\"EGEN|TETO\"} " +
                                        "tags는 해시 없이 3개의 핵심 단어, 중복 금지. egenType은 대문자 EGEN 또는 TETO 중 하나."),
                        Map.of("role","user","content", prompt)
                },
                "temperature", 0.4,
                "max_tokens", 420
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
            String egen = clean((String) json.getOrDefault("egenType", "")).toUpperCase(java.util.Locale.ROOT);
            if (!egen.equals("EGEN") && !egen.equals("TETO")) egen = "";

            if (feature.isBlank() || partner.isBlank()) {
                log.warn("[GPT] JSON 필드 누락/비어있음 → 기본값 보강. content={}", abbreviate(content));
                if (feature.isBlank()) feature = defaultFeature();
                if (partner.isBlank()) partner = defaultPartner();
            }

            return DatingStyleSummary.builder()
                    .feature(feature)
                    .recommendedPartner(partner)
                    .tags(tags)
                    .egenType(egen)
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
                .egenType(null)
                .build();
    }

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
                {"첫 만남에서의 당신, 주로 어떤 상황을 선호하시나요?","사람들과 함께하는 시끌벅적한 모임(a)","조용하고 분위기 있는 카페에서 일대일 대화(b)"},
                {"데이트 상대를 볼 때, 가장 중요하게 생각하는 것은?","유머러스하고 즐거운 시간을 보내 줄 수 있는 사람(a)","진지하고 깊이 있는 대화를 나눌 수 있는 사람(b)"},
                {"첫 인사할 때, 어떤 모습일까요?","먼저 다가가서 밝게 인사하기(a)","작게 웃으면서 고개 숙이기(b)"},
                {"갈등상황에서 당신은?","회피하기보단 직접적으로 소통하고 해결하려한다.(a)","상대의 의견을 먼저 듣고 배려하며 부드럽게 대처한다.(b)"},
                {"당신의 외적 이상형에 더 가까운 것은?","힙하고 트렌디한 스타일(a)","깔끔하고 단정한 스타일(b)"},
                {"어떤 데이트를 선호하나요?","즉흥적이고 계획되지 않은 데이트(a)","미리 준비한 안정적인 데이트(b)"},
                {"애정 표현 방식은 주로 어떤가요?","솔직하고 직접적(a)","은은하고 섬세함(b)"},
                {"관계에서 당신이 추구하는 가치는?","서로에게 새로운 활력을 불어주는 흥미로움(a)","편안한 신뢰를 바탕으로 한 안정감(b)"},
                {"호감 있는 사람과 데이트 중 손이 닿았을 때, 어떻게 할 건가요?","자연스럽게 손 잡기(a)","작게 웃으며 눈치만 보기(b)"},
                {"데이트 상대와 있을 때, 당신의 모습은?","재미있는 분위기를 주도하고 리드하는 편(a)","상대방의 이야기에 귀 기울이고 맞춰주는 편(b)"}
        };
        StringBuilder sb = new StringBuilder("아래 A/B 선택 결과를 근거로 결과를 만들어라.\n\n");
        for (int i = 0; i < 10; i++) {
            String sel = a.getOrDefault("q" + (i + 1), "");
            sb.append(i + 1).append(". ").append(qs[i][0]).append(" = ")
                    .append("a".equalsIgnoreCase(sel) ? qs[i][1] : qs[i][2]).append("\n");
        }
        sb.append("\n반드시 JSON만 출력: {\"feature\":\"...\",\"recommendedPartner\":\"...\",\"tags\":[\"단어\",\"단어\",\"단어\"],\"egenType\":\"EGEN|TETO\"}");
        return sb.toString();
    }

    private static String abbreviate(String s) {
        if (s == null) return "";
        return s.length() > 300 ? s.substring(0, 300) + "..." : s;
    }

    private record ChatCompletionResponse(List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String role, String content) {}
}
