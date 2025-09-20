// com/example/uni/user/service/UserService.java
package com.example.uni.user.service;

import com.example.uni.chat.ChatRoomService;
import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.match.Signal;
import com.example.uni.match.SignalRepository;
import com.example.uni.user.ai.TextGenClient;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.DatingStyleSummary;
import com.example.uni.user.dto.PeerDetailResponse;
import com.example.uni.user.dto.UserOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.repo.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TextGenClient textGenClient;
    private final ObjectMapper om;
    private final Environment env;

    private final ChatRoomService chatRoomService;
    private final SignalRepository signalRepository;

    @Value("${app.unknown-user.name:알 수 없는 유저}")
    private String unknownUserName;
    @Value("${app.unknown-user.image:}")
    private String unknownUserImage;

    @Value("${app.public-base-url:}")
    private String publicBaseUrl;
    @Value("${app.api-prefix:/api}")
    private String apiPrefix;
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private String imageUrlByType(int typeId){
        String u1 = env.getProperty("app.type-image.1", "");
        String u2 = env.getProperty("app.type-image.2", "");
        String u3 = env.getProperty("app.type-image.3", "");
        String u4 = env.getProperty("app.type-image.4", "");
        return switch (typeId) {
            case 1 -> u1;
            case 2 -> u2;
            case 3 -> u3;
            default -> u4;
        };
    }
    private String imageUrlByType2(int typeId){
        String u1 = env.getProperty("app.type-image2.1", "");
        String u2 = env.getProperty("app.type-image2.2", "");
        String u3 = env.getProperty("app.type-image2.3", "");
        String u4 = env.getProperty("app.type-image2.4", "");
        return switch (typeId) {
            case 1 -> u1;
            case 2 -> u2;
            case 3 -> u3;
            default -> u4;
        };
    }
    private String imageUrlByType3(int typeId){
        String u1 = env.getProperty("app.type-image3.1", "");
        String u2 = env.getProperty("app.type-image3.2", "");
        String u3 = env.getProperty("app.type-image3.3", "");
        String u4 = env.getProperty("app.type-image3.4", "");
        return switch (typeId) {
            case 1 -> u1;
            case 2 -> u2;
            case 3 -> u3;
            default -> u4;
        };
    }

    public String resolveTypeImage(int typeId)  { return imageUrlByType(typeId); }
    public String resolveTypeImage2(int typeId) { return imageUrlByType2(typeId); }
    public String resolveTypeImage3(int typeId) { return imageUrlByType3(typeId); }
    public String resolveTypeImage4(int idx) {
        String url = env.getProperty("app.type-image4." + idx, "");
        if (url == null || url.isBlank()) url = env.getProperty("app.type-image4.default", "");
        return url;
    }

    public User get(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
    public User getActive(Long id){
        User u = get(id);
        if (u.getDeactivatedAt() != null) throw new ApiException(ErrorCode.FORBIDDEN);
        return u;
    }
    public boolean isNameAvailable(String name) {
        return !userRepository.existsByNameIgnoreCase(name);
    }

    private static int determineTypeId(Map<String,String> answers) {
        int aCnt = 0;
        for (int i = 1; i <= 10; i++) {
            String v = Optional.ofNullable(answers.get("q"+i)).orElse("").trim().toLowerCase();
            if ("a".equals(v)) aCnt++;
        }
        int bCnt = 10 - aCnt;
        if (aCnt >= 7) return 1;
        if (bCnt >= 7) return 2;
        if (aCnt == 5) return 4;
        if (aCnt <= 4) return 3;
        return 4;
    }
    private static String determineEgenType(Map<String,String> answers) {
        int bCnt = 0;
        for (int i = 1; i <= 10; i++) {
            String sel = Optional.ofNullable(answers.get("q"+i)).orElse("").trim().toLowerCase();
            if ("b".equals(sel)) bCnt++;
        }
        return bCnt >= 6 ? "EGEN" : "TETO";
    }
    private static String toKoEgen(String v){
        if (v == null) return null;
        String s = v.trim().toUpperCase();
        if (s.equals("EGEN")) return "에겐";
        if (s.equals("TETO")) return "테토";
        return null;
    }
    private static TypeText toTypeText(int typeId){
        return switch (typeId) {
            case 1 -> new TypeText("활발한 에너지형", "관계의 즐거움과 생기를 붙여넣는 매력의 소유자!");
            case 2 -> new TypeText("따뜻한 통찰력형", "깊은 교감의 매력 소유자!");
            case 3 -> new TypeText("든든한 신뢰형", "관계의 일정을 주는 든든한 매력의 소유자!");
            default -> new TypeText("세련된 감각형", "만남을 빛내는 개성 넘치는 매력의 소유자!");
        };
    }
    private record TypeText(String title, String content) {}

    @Transactional
    public User completeProfile(Long userId, UserOnboardingRequest req) {
        User u = getActive(userId);

        String prevName = u.getName();
        Integer prevTypeId = u.getTypeId();

        if (req.getName() != null) {
            userRepository.findByNameIgnoreCase(req.getName()).ifPresent(other -> {
                if (!Objects.equals(other.getId(), userId)) throw new ApiException(ErrorCode.CONFLICT);
            });
            u.setName(req.getName());
        }
        u.setDepartment(req.getDepartment());
        u.setStudentNo(req.getStudentNo());
        u.setBirthYear(req.getBirthYear());
        if (u.getGender() != null && u.getGender() != req.getGender()) {
            throw new ApiException(ErrorCode.CONFLICT);
        }
        u.setGender(req.getGender());
        u.setMbti(req.getMbti());

        Map<String,String> answers = new LinkedHashMap<>();
        answers.put("q1", req.getQ1()); answers.put("q2", req.getQ2());
        answers.put("q3", req.getQ3()); answers.put("q4", req.getQ4());
        answers.put("q5", req.getQ5()); answers.put("q6", req.getQ6());
        answers.put("q7", req.getQ7()); answers.put("q8", req.getQ8());
        answers.put("q9", req.getQ9()); answers.put("q10", req.getQ10());
        u.setTypeId(determineTypeId(answers));

        try {
            String newJson = om.writeValueAsString(answers);
            String oldJson = Optional.ofNullable(u.getDatingStyleAnswersJson()).orElse("");
            if (!newJson.equals(oldJson)) {
                DatingStyleSummary ds = textGenClient.summarizeDatingStyle(answers);
                u.setStyleSummary(ds.getFeature());
                u.setStyleRecommendedPartner(ds.getRecommendedPartner());
                u.setStyleTagsJson(om.writeValueAsString(ds.getTags()));
                String egen = Optional.ofNullable(ds.getEgenType()).orElse("").trim().toUpperCase(java.util.Locale.ROOT);
                if (!egen.equals("EGEN") && !egen.equals("TETO")) egen = determineEgenType(answers);
                u.setEgenType(egen);
            }
            u.setDatingStyleAnswersJson(newJson);
        } catch (Exception e) {
            u.setDatingStyleAnswersJson("{}");
            u.setEgenType(determineEgenType(answers));
        }

        u.setProfileComplete(true);
        if (u.getMatchCredits() <= 0) u.setMatchCredits(3);
        if (u.getSignalCredits() <= 0) u.setSignalCredits(3);

        User saved = userRepository.save(u);

        // 이름 변경 → Firestore 동기화
        if (req.getName() != null && !Objects.equals(prevName, req.getName())) {
            try { chatRoomService.updatePeerName(saved.getId(), saved.getName()); } catch (RuntimeException ignore) {}
        }
        // 타입 변경 + 수동 프사 없음 → 기본 이미지 변경 반영
        if (!Objects.equals(prevTypeId, saved.getTypeId())) {
            String profile = validProfile(saved);
            if (profile == null || profile.isBlank()) {
                String fallback = imageUrlByType2(Optional.ofNullable(saved.getTypeId()).orElse(4));
                try { chatRoomService.updatePeerAvatar(saved.getId(), fallback); } catch (RuntimeException ignore) {}
            }
        }

        return saved;
    }

    @Transactional
    public User updateIntroduce(Long userId, String introduce) {
        User u = getActive(userId);
        u.setIntroduce(introduce);
        return userRepository.save(u);
    }

    @Transactional
    public User updateInstagram(Long userId, String raw) {
        User u = getActive(userId);
        u.setInstagramUrl(toInstagramUrlOrNull(raw));
        return userRepository.save(u);
    }

    // 상대/절대 URL 모두 받아 절대 URL로 저장 + Firestore 캐시 즉시 반영
    @Transactional
    public User updateProfileImageUrl(Long userId, String url) {
        User u = getActive(userId);
        String v = normalizeAbsoluteUrl(url);
        u.setProfileImageUrl(v);
        User saved = userRepository.save(u);

        String avatar = (v != null && !v.isBlank())
                ? v
                : imageUrlByType2(Optional.ofNullable(saved.getTypeId()).orElse(4));
        try { chatRoomService.updatePeerAvatar(saved.getId(), avatar); } catch (RuntimeException ignore) {}

        return saved;
    }

    // 회원탈퇴: 대기중 신호 일괄 정리 + Firestore 마스킹 + 이전 매칭 캐시 초기화
    @Transactional
    public void deactivateAndCleanup(Long meId) {
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (me.getDeactivatedAt() != null) return;

        me.setDeactivatedAt(LocalDateTime.now());

        // ✅ 이전 매칭 캐시 초기화
        me.setLastMatchJson("[]");
        me.setLastMatchAt(null);

        userRepository.save(me);

        // 파라미터화된 bulk update 메서드 사용 (레포지토리 구현 필요)
        signalRepository.declineAllIncomingFor(me, Signal.Status.DECLINED, Signal.Status.SENT);
        signalRepository.declineAllOutgoingFrom(me, Signal.Status.DECLINED, Signal.Status.SENT);

        chatRoomService.markUserLeft(me.getId(), unknownUserName, unknownUserImage);
    }

    // ---- helpers ----

    private String toInstagramUrlOrNull(String raw) {
        if (raw == null) return null;
        String v = raw.trim();
        if (v.isEmpty()) return null;
        if ("null".equalsIgnoreCase(v) || "undefined".equalsIgnoreCase(v)) return null;
        v = v.replaceAll("^https?://(www\\.)?instagram\\.com/", "");
        if (v.startsWith("@")) v = v.substring(1);
        v = v.replaceAll("[/?].*$", "");
        if (v.isEmpty()) return null;
        if (!v.matches("^[A-Za-z0-9._]{1,30}$")) return null;
        return "https://www.instagram.com/" + v;
    }

    private List<String> parseTags(String json){
        try {
            if (json == null || json.isBlank()) return List.of();
            return om.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
    private String validProfile(User u) {
        String p = u.getProfileImageUrl();
        if (p == null) return null;
        String t = p.trim();
        return t.isEmpty() ? null : t;
    }

    public UserProfileResponse toResponse(User u) {
        boolean deactivated = (u.getDeactivatedAt()!=null);
        int typeId = Optional.ofNullable(u.getTypeId()).orElse(4);
        TypeText tt = toTypeText(typeId);
        List<String> tags = deactivated ? List.of() : parseTags(u.getStyleTagsJson());
        String profile = deactivated ? null : validProfile(u);
        String img1 = deactivated ? unknownUserImage : (profile != null ? profile : imageUrlByType(typeId));
        String img2 = deactivated ? unknownUserImage : (profile != null ? profile : imageUrlByType2(typeId));
        String egenOut = deactivated ? null : toKoEgen(u.getEgenType());
        return UserProfileResponse.builder()
                .userId(u.getId())
                .kakaoId(deactivated ? null : u.getKakaoId())
                .email(deactivated ? null : u.getEmail())
                .nickname(deactivated ? null : u.getNickname())
                .name(deactivated ? unknownUserName : u.getName())
                .department(deactivated ? null : u.getDepartment())
                .studentNo(deactivated ? null : u.getStudentNo())
                .birthYear(deactivated ? null : u.getBirthYear())
                .gender(deactivated ? null : u.getGender())
                .profileComplete(!deactivated && u.isProfileComplete())
                .matchCredits(deactivated ? 0 : u.getMatchCredits())
                .signalCredits(deactivated ? 0 : u.getSignalCredits())
                .version(u.getVersion())
                .typeTitle(tt.title())
                .typeContent(tt.content())
                .typeImageUrl(img1)
                .typeImageUrl2(img2)
                .styleSummary(deactivated ? null : u.getStyleSummary())
                .recommendedPartner(deactivated ? null : u.getStyleRecommendedPartner())
                .tags(tags)
                .introduce(deactivated ? null : u.getIntroduce())
                .instagramUrl(deactivated ? null : u.getInstagramUrl())
                .mbti(deactivated ? null : u.getMbti())
                .egenType(egenOut)
                .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().toString() : null)
                .updatedAt(u.getUpdatedAt() != null ? u.getUpdatedAt().toString() : null)
                .build();
    }

    public PeerDetailResponse toPeerResponse(User u){
        boolean deactivated = (u.getDeactivatedAt()!=null);
        int typeId = Optional.ofNullable(u.getTypeId()).orElse(4);
        TypeText tt = toTypeText(typeId);
        List<String> tags = deactivated ? List.of() : parseTags(u.getStyleTagsJson());
        String profile = deactivated ? null : validProfile(u);
        String img1 = deactivated ? unknownUserImage : (profile != null ? profile : imageUrlByType(typeId));
        String img2 = deactivated ? unknownUserImage : (profile != null ? profile : imageUrlByType2(typeId));
        String egenOut = deactivated ? null : toKoEgen(u.getEgenType());
        return PeerDetailResponse.builder()
                .userId(u.getId())
                .name(deactivated ? unknownUserName : u.getName())
                .department(deactivated ? null : u.getDepartment())
                .studentNo(deactivated ? null : u.getStudentNo())
                .birthYear(deactivated ? null : u.getBirthYear())
                .gender(deactivated ? null : u.getGender())
                .typeTitle(tt.title())
                .typeContent(tt.content())
                .typeImageUrl(img1)
                .typeImageUrl2(img2)
                .styleSummary(deactivated ? null : u.getStyleSummary())
                .recommendedPartner(deactivated ? null : u.getStyleRecommendedPartner())
                .tags(tags)
                .introduce(deactivated ? null : u.getIntroduce())
                .instagramUrl(deactivated ? null : u.getInstagramUrl())
                .mbti(deactivated ? null : u.getMbti())
                .egenType(egenOut)
                .build();
    }

    // === URL 정규화 ===
    private String normalizeAbsoluteUrl(String raw) {
        if (raw == null) return null;
        String v = raw.trim();
        if (v.isEmpty()) return null;

        if (v.startsWith("http://") || v.startsWith("https://")) return v;
        if (v.startsWith("//")) return "https:" + v;

        String base = (publicBaseUrl == null) ? "" : publicBaseUrl.trim().replaceAll("/+$", "");
        String prefix = (apiPrefix == null ? "" : apiPrefix.trim());
        if (!prefix.isEmpty() && !prefix.startsWith("/")) prefix = "/" + prefix;

        String ctx = (contextPath == null) ? "" : contextPath.trim();
        if (!ctx.isEmpty() && !ctx.startsWith("/")) ctx = "/" + ctx;

        if (v.startsWith("/")) return base.isEmpty() ? v : base + v;

        if (v.startsWith("files/")) {
            String rel = (prefix + "/" + v).replaceAll("//+", "/");
            return base.isEmpty() ? rel : base + rel;
        }
        if (v.startsWith("api/")) {
            String rel = ("/" + v).replaceAll("//+", "/");
            return base.isEmpty() ? rel : base + rel;
        }

        String rel = (prefix + "/" + v).replaceAll("//+", "/");
        return base.isEmpty() ? rel : base + rel;
    }
}
