# 너랑나랑 (UNME) 💘

<img width="267" height="70" alt="Image" src="https://github.com/user-attachments/assets/edacbf7d-5504-4d25-ac1f-32938bb6f758" />

## 개발자 및 개발 기간 👤

| 이름 | 역할 | 기간 |
|---|---|---|
| 박찬우 | Backend Developer | 25.09.08 ~ 26.09.28 |

## 1. 프로젝트 소개 🚀

너랑나랑(UNME)은 한서대 중심의 대학생 소개팅/매칭 서비스입니다. 사용자는 카카오 로그인을 통해 가입하고, 프로필과 10문항 성향 검사를 입력한 뒤 매칭 후보를 확인할 수 있습니다. 이후 관심 표현인 시그널을 보내고, 상대가 수락하면 1:1 채팅방이 생성되는 흐름으로 서비스가 이어집니다.

백엔드는 카카오 OAuth 인증, JWT 기반 인증/인가, 프로필 및 성향 데이터 관리, 매칭 후보 필터링, 시그널 상태 관리, Firestore 기반 채팅방 생성, 실시간 WebSocket 알림, 파일 업로드, 랭킹 통계, 이벤트 코드 사용까지 서비스 핵심 흐름을 담당합니다.

## 2. 프로젝트 기획 배경 📌

대학생 소개팅 서비스는 단순히 사용자를 무작위로 보여주는 것보다, 중복 노출을 줄이고 서로 다른 학과의 사용자를 연결하며, 관심 표현 이후 실제 대화까지 자연스럽게 이어지는 구조가 중요합니다.

이 프로젝트는 매칭 후보를 매번 새롭게 관리하고, 이미 본 사용자나 이미 시그널을 보낸 사용자를 다시 노출하지 않도록 설계했습니다. 또한 매칭과 시그널에 크레딧을 두어 무분별한 사용을 제한하고, 시그널이 성사되면 별도 채팅방을 자동 생성하여 사용자가 서비스 안에서 바로 대화를 시작할 수 있도록 구현했습니다.

## 3. 성과 및 회고 🏆

사진 넣을 곳

## 4. 주요 기능 ✨

### 4.1 카카오 로그인 및 사용자 생성

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 카카오 로그인 | 카카오 OAuth 인가 코드를 받아 사용자 정보를 조회하고 로그인 처리를 수행합니다. |
| 신규 사용자 생성 | 카카오 ID와 이메일 기준으로 기존 사용자를 찾고, 없으면 `users` 테이블에 신규 사용자를 생성합니다. |
| JWT 발급 | 로그인 성공 시 Access Token과 Refresh Token을 발급합니다. |
| Refresh Cookie 저장 | Refresh Token은 HttpOnly Cookie로 저장해 재발급에 사용합니다. |
| 탈퇴 계정 차단 | `deactivatedAt`이 존재하는 사용자는 재로그인을 차단합니다. |

### 4.2 프로필 등록 및 내 정보 관리

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 닉네임 중복 확인 | 입력한 닉네임이 이미 사용 중인지 확인합니다. |
| 프로필 등록/수정 | 이름, 학과, 학번, 출생 연도, 성별, MBTI, 10문항 답변을 저장합니다. |
| 입력값 검증 | 닉네임 길이, 출생 연도 범위, 성별, 성향 검사 답변 형식을 검증합니다. |
| 내 프로필 조회 | 로그인한 사용자의 프로필과 크레딧, 성향 결과를 조회합니다. |
| 자기소개 수정 | 사용자의 자기소개를 수정합니다. |
| 인스타그램 수정 | 입력값을 인스타그램 URL 형식으로 정규화해 저장합니다. |
| 프로필 이미지 URL 수정 | 프로필 이미지 URL을 저장하고 기존 채팅방의 표시 이미지에도 반영합니다. |

### 4.3 성향 분석 및 유형 이미지 매핑

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 10문항 성향 검사 | `q1`부터 `q10`까지의 A/B 답변을 저장합니다. |
| `typeId` 산출 | 답변의 A/B 개수를 기준으로 사용자 성향 유형 ID를 계산합니다. |
| EGEN/TETO 산출 | 답변 결과 또는 OpenAI 응답을 기반으로 `EGEN`, `TETO` 성향을 저장합니다. |
| 성향 요약 생성 | OpenAI API를 사용해 성향 요약, 추천 파트너 문장, 태그 3개를 생성합니다. |
| fallback 처리 | OpenAI API 호출 실패 시 기본 성향 요약과 기본 태그를 저장합니다. |
| 유형 이미지 매핑 | `typeImageUrl`, `typeImageUrl2`, `typeImageUrl3`, MBTI 기반 이미지 URL을 설정값에서 매핑합니다. |

### 4.4 스마트 매칭

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 매칭 시작 | 사용자의 매칭 크레딧을 확인한 뒤 후보를 조회합니다. |
| 반대 성별 필터링 | 사용자의 성별과 반대 성별인 사용자만 후보로 조회합니다. |
| 학과 중복 제외 | 가능한 경우 같은 학과 사용자를 후보에서 제외합니다. |
| 프로필 완료 사용자만 노출 | 프로필 입력을 완료한 활성 사용자만 매칭 후보에 포함합니다. |
| 재노출 방지 | `seen_candidates` 기록을 기준으로 이미 본 후보를 제외합니다. |
| 시그널 중복 제외 | 이미 시그널을 보낸 사용자는 다시 후보로 보여주지 않습니다. |
| 채팅방 존재 여부 확인 | 이미 Firestore 채팅방이 존재하는 사용자 쌍은 후보에서 제외합니다. |
| 후보 랜덤화 | 조건을 통과한 후보 목록을 섞은 뒤 최대 3명을 반환합니다. |
| 최근 매칭 결과 저장 | 반환된 후보를 `lastMatchJson`에 저장해 직전 결과 조회에 사용합니다. |
| 크레딧 차감 | 후보가 존재할 때만 매칭 크레딧을 1회 차감합니다. |

### 4.5 시그널 플러팅

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 시그널 보내기 | 사용자가 관심 있는 상대에게 시그널을 보냅니다. |
| 시그널 크레딧 차감 | 새로운 시그널을 보낼 때 시그널 크레딧을 1회 차감합니다. |
| 자기 자신 차단 | 자기 자신에게 시그널을 보내는 요청을 차단합니다. |
| 같은 학과 차단 | 같은 학과 사용자에게 시그널을 보내는 요청을 차단합니다. |
| 같은 성별 차단 | 같은 성별 사용자에게 시그널을 보내는 요청을 차단합니다. |
| 중복 채팅방 차단 | 이미 채팅방이 존재하는 상대에게 시그널을 보내는 것을 막습니다. |
| 시그널 상태 조회 | 특정 사용자에게 이미 시그널을 보냈는지 조회합니다. |
| 보낸 시그널 조회 | 내가 보낸 시그널 목록과 상태를 조회합니다. |
| 받은 시그널 조회 | 내가 받은 `SENT` 상태의 시그널 목록을 조회합니다. |
| 시그널 로그 저장 | 시그널 통계 산출을 위해 수신자의 학과와 MBTI를 로그로 저장합니다. |

### 4.6 시그널 수락 및 거절

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 시그널 거절 | 받은 시그널을 `DECLINED` 상태로 변경하고 수신자 목록에서 숨김 처리합니다. |
| 거절 알림 전송 | 시그널을 보낸 사용자에게 거절 상태를 실시간 알림으로 전달합니다. |
| 시그널 수락 | 받은 시그널을 `MUTUAL` 상태로 변경합니다. |
| 양방향 시그널 동기화 | 반대 방향 시그널이 존재하면 함께 `MUTUAL` 상태로 동기화합니다. |
| 매칭 로그 저장 | 매칭이 성사되면 두 사용자의 학과와 MBTI를 `match_logs`에 저장합니다. |
| 채팅방 생성 | 매칭 성사 시 Firestore에 1:1 채팅방을 생성합니다. |
| 매칭 알림 전송 | 양쪽 사용자에게 매칭 성사 알림과 채팅방 정보를 전송합니다. |
| 최근 매칭 후보 정리 | 매칭이 성사된 상대를 양쪽 사용자의 최근 매칭 결과에서 제거합니다. |

### 4.7 1:1 채팅방 관리

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 고정 roomId 생성 | 두 사용자 ID를 정렬해 `r_{id}_{id}` 형식의 채팅방 ID를 생성합니다. |
| 중복 채팅방 방지 | 동일 사용자 쌍의 Firestore 문서가 이미 있으면 새로 만들지 않습니다. |
| 참여자 저장 | 채팅방에 두 사용자의 ID를 `participants`로 저장합니다. |
| 상대 정보 저장 | 채팅방의 `peers`와 `listCard`에 상대 프로필 정보를 저장합니다. |
| 프로필 이미지 동기화 | 사용자가 프로필 이미지를 변경하면 기존 채팅방 표시 정보도 갱신합니다. |
| 닉네임 동기화 | 사용자가 이름을 변경하면 기존 채팅방 표시 이름도 갱신합니다. |
| 탈퇴 사용자 마스킹 | 탈퇴한 사용자는 채팅방에서 대체 이름과 대체 이미지로 표시합니다. |

### 4.8 실시간 WebSocket 알림

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| STOMP WebSocket 연결 | `/ws`, `/api/ws` 엔드포인트로 STOMP WebSocket 연결을 제공합니다. |
| WebSocket JWT 검증 | Handshake 또는 STOMP CONNECT 단계에서 JWT를 검증합니다. |
| 비인증 연결 차단 | 인증되지 않은 SUBSCRIBE, SEND 요청을 차단합니다. |
| 사용자별 세션 관리 | WebSocket 세션을 사용자 ID 기준으로 등록하고 해제합니다. |
| 시그널 알림 | 새 시그널과 거절 이벤트를 `/user/queue/signals`로 전송합니다. |
| 매칭 알림 | 매칭 성사 이벤트를 `/user/queue/matches`로 전송합니다. |
| 커밋 후 알림 | 트랜잭션 커밋 이후 알림을 보내 데이터와 알림 상태 불일치를 줄입니다. |

### 4.9 파일 업로드 및 정적 파일 제공

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 프로필 이미지 업로드 | multipart/form-data로 프로필 이미지를 업로드합니다. |
| 빈 파일 검증 | 파일이 비어 있으면 `EMPTY_FILE` 응답을 반환합니다. |
| 용량 검증 | 10MB를 초과하는 파일은 `FILE_TOO_LARGE`로 차단합니다. |
| 이미지 MIME 검증 | `image/`로 시작하지 않는 파일은 `NOT_IMAGE`로 차단합니다. |
| UUID 파일명 생성 | 원본 파일명 충돌을 막기 위해 UUID 기반 파일명으로 저장합니다. |
| 사용자별 디렉터리 저장 | 인증 사용자가 있으면 사용자 ID별 프로필 이미지 폴더에 저장합니다. |
| 정적 파일 제공 | 업로드된 파일을 `/files/**`, `/api/files/**` 경로로 제공합니다. |

### 4.10 유형 이미지 업로드

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 유형 이미지 업로드 | `/admin/type-images/{type}` API로 성향 유형 이미지를 업로드합니다. |
| type 값 검증 | `1~4`, `2.x`, `3.x`, `4.x`, `5.x` 형식의 허용된 타입만 저장합니다. |
| 프로필 유형 이미지 저장 | 업로드 파일을 `profile-types` 디렉터리에 저장합니다. |
| 설정 키 반환 | 업로드된 이미지가 어떤 `app.type-image...` 설정 키에 해당하는지 반환합니다. |

### 4.11 랭킹 및 통계

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 학과별 시그널 랭킹 | `signal_logs`를 기준으로 시그널을 많이 받은 학과 순위를 조회합니다. |
| MBTI별 시그널 랭킹 | `signal_logs`를 기준으로 시그널을 많이 받은 MBTI 순위를 조회합니다. |
| 학과별 매칭 랭킹 | `match_logs`를 기준으로 매칭이 많이 성사된 학과 순위를 조회합니다. |
| MBTI별 매칭 랭킹 | `match_logs`를 기준으로 매칭이 많이 성사된 MBTI 순위를 조회합니다. |
| 랭킹 이미지 매핑 | 학과 또는 MBTI에 대응되는 유형 이미지를 함께 반환합니다. |

### 4.12 이벤트 코드 사용

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| 이벤트 코드 입력 | 사용자가 이벤트 코드를 입력해 크레딧을 충전합니다. |
| 코드 대문자 정규화 | 입력된 코드는 trim 후 대문자로 변환해 검증합니다. |
| 1회성 사용 처리 | 사용되지 않은 코드만 `used=true`로 변경합니다. |
| 중복 사용 차단 | 이미 사용된 코드나 존재하지 않는 코드는 오류로 처리합니다. |
| 크레딧 지급 | 코드 사용 성공 시 매칭 크레딧과 시그널 크레딧을 각각 5개씩 증가시킵니다. |

### 4.13 푸시 구독 저장

사진 넣을 곳

| 기능 | 설명 |
|---|---|
| Push 구독 저장 | 사용자의 Web Push endpoint, p256dh, auth 값을 저장합니다. |
| 사용자별 구독 갱신 | 기존 구독 정보가 있으면 새 값으로 업데이트합니다. |
| Web Push 발송 기반 | 저장된 구독 정보를 사용해 사용자에게 Push 알림을 보낼 수 있는 구조를 제공합니다. |

## 5. 기술 스택 🛠️

| 구분 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.5 |
| Build | Gradle 8.14.3 |
| Web | Spring Web, Spring WebFlux |
| Database | MySQL, Firebase Firestore |
| ORM | Spring Data JPA, Hibernate |
| Security | Spring Security, JJWT 0.11.5 |
| OAuth | Kakao OAuth |
| Validation | Jakarta Validation |
| Realtime | Spring WebSocket, STOMP, SimpMessagingTemplate |
| Push | Firebase Admin SDK, Web Push |
| AI | OpenAI Chat Completions API |
| File | Multipart Upload, Local File Storage, Static Resource Handler |
| Utility | Lombok, Jackson, Apache HttpClient5 |
| Test | JUnit 5, Spring Boot Test, Spring Security Test |

### 로컬 실행 및 환경 설정

프로젝트 내부 이름은 Gradle 설정 기준 `UNI`이며, 서버 기본 포트는 `8080`, context-path는 `/api`입니다.

    ./gradlew.bat bootRun

주요 설정은 `src/main/resources/application.properties`에서 확인됩니다.

| 설정 | 설명 |
|---|---|
| `SPRING_DATASOURCE_URL` | MySQL 연결 URL, 기본값은 `jdbc:mysql://localhost:3306/unme` |
| `SPRING_DATASOURCE_USERNAME` | DB 사용자명 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 |
| `JWT_SECRET` | HS256 JWT 서명 키 |
| `JWT_ACCESS_TTL_SECONDS` | Access Token 만료 시간 |
| `JWT_REFRESH_TTL_SECONDS` | Refresh Token 만료 시간 |
| `KAKAO_CLIENT_ID` | 카카오 OAuth Client ID |
| `KAKAO_CLIENT_SECRET` | 카카오 OAuth Client Secret |
| `KAKAO_REDIRECT_URI` | 카카오 로그인 콜백 URL |
| `kakao.admin-key` | 카카오 연결 해제에 필요한 Admin Key |
| `FRONTEND_REDIRECT_BASE` | 로그인 후 이동할 프론트엔드 기본 주소 |
| `CORS_ALLOWED_ORIGINS` | CORS 허용 origin 목록 |
| `WS_ALLOWED_ORIGINS` | WebSocket 허용 origin 목록 |
| `OPENAI_API_KEY` | OpenAI API Key |
| `OPENAI_MODEL` | 성향 요약에 사용할 OpenAI 모델 |
| `firebase.credentials` | Firebase 서비스 계정 JSON 파일 경로 |
| `VAPID_PUBLIC` | Web Push 공개 키 |
| `VAPID_PRIVATE` | Web Push 개인 키 |
| `app.upload.dir` | 업로드 파일 저장 경로 |
| `app.public-base-url` | 업로드 URL 생성에 사용할 공개 API Base URL |

## 6. 시스템 구조 🧩

| 계층 | 역할 |
|---|---|
| Controller | HTTP 요청을 받고 인증 주체, PathVariable, RequestBody, MultipartFile을 Service로 전달 |
| Service | 도메인 규칙, 트랜잭션, 외부 API 연동, Firestore 채팅방 생성, 실시간 알림 처리 |
| Repository | JPA 기반 Entity 조회/저장, 랭킹 집계 쿼리, 코드 사용 처리 |
| Database | MySQL에 사용자, 시그널, 로그, 푸시 구독, 이벤트 코드 저장 |
| External Storage | Firestore `chatRooms` 컬렉션에 1:1 채팅방 저장 |
| Realtime | STOMP WebSocket으로 사용자별 큐에 시그널/매칭 알림 전송 |

인증 흐름은 `JwtAuthFilter`가 `Authorization: Bearer {token}` 헤더를 검증하고, 유효한 사용자 ID를 Spring Security 인증 객체로 등록하는 방식입니다. 탈퇴했거나 존재하지 않는 사용자는 필터 단계에서 차단됩니다. WebSocket 연결은 Handshake 또는 STOMP CONNECT 단계에서 JWT를 확인하고 사용자별 세션을 등록합니다.

## 7. 백엔드 핵심 구현 내용 🔥

### JWT 기반 인증/인가 구조

Access Token과 Refresh Token을 구분하기 위해 JWT claim에 `typ` 값을 저장했습니다. Access Token은 API 인증에 사용하고, Refresh Token은 HttpOnly Cookie에 저장하여 `/auth/refresh`에서 Access Token을 재발급합니다. 서버는 상태를 세션에 저장하지 않고 `SessionCreationPolicy.STATELESS`로 동작하므로, 프론트엔드와 분리된 API 서버 구조에 적합합니다.

### 카카오 OAuth 로그인 구조

`AuthController`는 카카오 인가 URL로 사용자를 리다이렉트하고, 콜백에서 `OAuthService`가 카카오 토큰 교환과 사용자 정보 조회를 수행합니다. 카카오 ID 또는 이메일 기준으로 기존 사용자를 찾고, 신규 사용자는 기본 크레딧 0과 `profileComplete=false` 상태로 생성합니다. 탈퇴 사용자는 `ACCOUNT_DEACTIVATED`로 처리해 무분별한 재가입을 막습니다.

### 매칭 도메인 설계

매칭은 단순 랜덤 조회가 아니라 `UserCandidateRepository`에서 성별, 학과, 프로필 완료 여부, 탈퇴 여부, 본 적 있는 후보 여부를 먼저 필터링합니다. 이후 Service에서 이미 시그널을 보낸 대상과 이미 채팅방이 있는 대상을 제외합니다. 이를 통해 같은 후보의 반복 노출을 줄이고, 매칭 이후의 사용자 경험을 안정적으로 관리할 수 있습니다.

### 시그널과 채팅방 연결

시그널은 `Signal` Entity로 관리하며 `SENT`, `MUTUAL`, `DECLINED` 상태를 가집니다. 수락 시 `match_logs`에 기록을 저장하고 Firestore 채팅방을 생성한 뒤, 양쪽 사용자에게 매칭 성사 알림을 보냅니다. 채팅방 생성은 사용자 ID를 정렬한 고정 roomId를 사용해 중복 방 생성을 방지했습니다.

### DTO 분리와 검증

프로필 등록 요청은 `UserOnboardingRequest`로 분리하고, 이름 길이, 학과, 학번, 출생 연도, 성별, 10문항 답변을 Jakarta Validation으로 검증합니다. 응답은 `UserProfileResponse`, 상대 상세 조회는 `PeerDetailResponse`로 분리하여 내 정보와 상대 정보의 노출 범위를 다르게 구성했습니다.

### 예외 처리 구조

도메인 예외는 `ApiException`과 `ErrorCode`로 표현하고, `GlobalExceptionHandler`가 일관된 JSON 응답으로 변환합니다. Validation 실패는 필드별 오류를 `details`에 담아 반환하며, 낙관적 락 충돌은 `CONFLICT`와 재시도 메시지로 응답합니다.

### 파일 업로드 처리

프로필 이미지 업로드는 파일 비어 있음, 10MB 초과, 이미지가 아닌 MIME 타입을 서버에서 검증합니다. 저장 파일명은 UUID 기반으로 생성하고, 사용자 ID가 있으면 사용자별 디렉터리에 저장합니다. 이후 `app.public-base-url`과 `app.api-prefix`를 조합해 프론트엔드가 바로 사용할 수 있는 URL을 반환합니다.

### 실시간 알림 구조

`RealtimeNotifier`는 `SimpMessagingTemplate`을 사용해 특정 사용자에게만 알림을 보냅니다. 알림은 트랜잭션 커밋 이후 실행되도록 `AfterCommitExecutor`를 사용해, DB 저장은 실패했는데 알림만 발송되는 상황을 줄였습니다.

### 탈퇴 사용자 비식별 처리

사용자 탈퇴 시 `deactivatedAt`을 기록하고 이름, 프로필 이미지, 최근 매칭 정보를 정리합니다. 또한 관련 시그널을 거절 처리하고, 기존 Firestore 채팅방의 상대 표시 정보를 `탈퇴한 사용자`와 대체 이미지로 갱신합니다. 이를 통해 채팅 기록은 유지하면서 개인정보 노출을 최소화합니다.

## 8. API 명세 📡

사진 넣을 곳

기본 context-path는 `/api`입니다.

| 기능 | Method | URL | 인증 필요 여부 | 설명 |
|---|---|---|---|---|
| 카카오 로그인 시작 | GET | `/api/auth/kakao/login` | 아니오 | 카카오 인가 URL로 302 리다이렉트 |
| 카카오 로그인 콜백 | GET | `/api/auth/kakao/callback` | 아니오 | 인가 코드로 로그인 처리 후 프론트엔드로 리다이렉트 |
| Access Token 재발급 | POST | `/api/auth/refresh` | 아니오 | Refresh Cookie 검증 후 Access Token 재발급 |
| 로그아웃 | POST | `/api/auth/logout` | 아니오 | Refresh Cookie 제거 |
| 내 인증 정보 조회 | GET | `/api/auth/me` | 예 | Firebase Custom Token과 내 프로필 반환 |
| 카카오 연결 해제 | DELETE | `/api/auth/kakao/unlink` | 예 | 카카오 unlink 및 탈퇴 마스킹 처리 |
| 닉네임 중복 확인 | GET | `/api/users/me/name/check` | 예 | 닉네임 사용 가능 여부 조회 |
| 프로필 등록/수정 | PUT | `/api/users/me/profile` | 예 | 기본 프로필과 성향 검사 등록 |
| 내 프로필 조회 | GET | `/api/users/me/profile` | 예 | 내 프로필 정보 조회 |
| 자기소개 수정 | PUT | `/api/users/me/introduce` | 예 | 자기소개 수정 |
| 인스타그램 수정 | PUT | `/api/users/me/instagram` | 예 | 인스타그램 URL 정규화 후 저장 |
| 프로필 이미지 URL 수정 | PUT | `/api/users/me/profile-image` | 예 | 프로필 이미지 URL 저장 및 채팅방 반영 |
| 푸시 구독 저장 | POST | `/api/users/me/me/push/subscribe` | 예 | Web Push 구독 정보 저장 |
| 상대 상세 조회 | GET | `/api/users/{userId}` | 예 | 상대 사용자 상세 정보 조회 |
| 최근 매칭 결과 조회 | GET | `/api/match/previous` | 예 | 직전 매칭 후보 목록 조회 |
| 시그널 상태 조회 | GET | `/api/signals/{targetId}/status` | 예 | 특정 대상에게 이미 시그널을 보냈는지 확인 |
| 매칭 시작 | POST | `/api/match/start` | 예 | 매칭 후보 최대 3명 조회 |
| 시그널 보내기 | POST | `/api/signals/{targetId}` | 예 | 상대에게 시그널 전송 |
| 시그널 거절 | POST | `/api/signals/decline/{signalId}` | 예 | 받은 시그널 거절 |
| 시그널 수락 | POST | `/api/signals/accept/{signalId}` | 예 | 시그널 수락 및 채팅방 생성 |
| 보낸 시그널 목록 | GET | `/api/signals/sent` | 예 | 내가 보낸 시그널 목록 조회 |
| 받은 시그널 목록 | GET | `/api/signals/received` | 예 | 내가 받은 시그널 목록 조회 |
| 학과별 매칭 랭킹 | GET | `/api/stats/rank/department-matches` | 예 | 매칭 로그 기준 학과 랭킹 조회 |
| MBTI별 시그널 랭킹 | GET | `/api/stats/rank/mbti-signals` | 예 | 시그널 로그 기준 MBTI 랭킹 조회 |
| MBTI별 매칭 랭킹 | GET | `/api/stats/rank/mbti-matches` | 예 | 매칭 로그 기준 MBTI 랭킹 조회 |
| 학과별 시그널 랭킹 | GET | `/api/stats/rank/department-signals` | 예 | 시그널 로그 기준 학과 랭킹 조회 |
| 이벤트 코드 사용 | POST | `/api/event/redeem` | 예 | 코드 사용 후 매칭/시그널 크레딧 증가 |
| 프로필 이미지 업로드 | POST | `/api/files/upload` | 아니오 | multipart 이미지 업로드, principal이 있으면 사용자별 경로 저장 |
| 유형 이미지 업로드 | POST | `/api/admin/type-images/{type}` | 아니오 | 유형 이미지 파일 업로드 |

## 9. API 요청/응답 예시 🧾

### 카카오 로그인 시작

요청

    GET /api/auth/kakao/login?next=/matching

응답

    HTTP/1.1 302 Found
    Location: https://kauth.kakao.com/oauth/authorize?client_id=...&redirect_uri=...&response_type=code&state=/matching

### Access Token 재발급

요청

    POST /api/auth/refresh
    Cookie: REFRESH={refreshToken}

응답

    {
      "accessToken": "jwt-access-token",
      "expiresIn": 1800
    }

### 프로필 등록/수정

요청

    PUT /api/users/me/profile
    Authorization: Bearer {accessToken}
    Content-Type: application/json

    {
      "name": "찬우",
      "department": "항공AI소프트웨어공학과",
      "studentNo": "20",
      "birthYear": "2001",
      "gender": "남자",
      "mbti": "ENFP",
      "q1": "a",
      "q2": "b",
      "q3": "a",
      "q4": "b",
      "q5": "a",
      "q6": "b",
      "q7": "a",
      "q8": "b",
      "q9": "a",
      "q10": "b"
    }

응답

    {
      "userId": 1,
      "kakaoId": "123456789",
      "email": "user@example.com",
      "nickname": "카카오닉네임",
      "name": "찬우",
      "department": "항공AI소프트웨어공학과",
      "studentNo": "20",
      "birthYear": "2001",
      "gender": "남자",
      "profileComplete": true,
      "matchCredits": 3,
      "signalCredits": 3,
      "version": 1,
      "typeTitle": "세련된 감각형",
      "typeContent": "만남을 빛내는 개성 넘치는 매력의 소유자!",
      "typeImageUrl": "https://api.likelionhsu.co.kr/api/files/profile-types/type4.png",
      "typeImageUrl2": "https://api.likelionhsu.co.kr/api/files/profile-types/type2.4.png",
      "styleSummary": "성향 요약",
      "recommendedPartner": "추천 파트너 문장",
      "tags": ["소통", "배려", "신뢰"],
      "introduce": null,
      "instagramUrl": null,
      "mbti": "ENFP",
      "egenType": "에겐",
      "createdAt": "2026-05-11T00:00:00",
      "updatedAt": "2026-05-11T00:00:00"
    }

### 매칭 시작

요청

    POST /api/match/start
    Authorization: Bearer {accessToken}

응답

    {
      "candidates": [
        {
          "userId": 2,
          "name": "상대닉네임",
          "department": "간호학과",
          "introduce": "안녕하세요",
          "typeImageUrl2": "https://api.likelionhsu.co.kr/api/files/profile-types/type2.4.png",
          "id": 2,
          "targetUserId": 2,
          "nickname": "상대닉네임",
          "major": "간호학과",
          "avatarUrl": "https://api.likelionhsu.co.kr/api/files/profile-types/type2.4.png",
          "profileImageUrl": "https://api.likelionhsu.co.kr/api/files/profile-types/type2.4.png"
        }
      ]
    }

### 시그널 보내기

요청

    POST /api/signals/2
    Authorization: Bearer {accessToken}

응답

    {
      "signalId": 10,
      "status": "SENT"
    }

### 시그널 수락 및 채팅방 생성

요청

    POST /api/signals/accept/10
    Authorization: Bearer {accessToken}

응답

    {
      "roomId": "r_1_2",
      "participants": [1, 2],
      "peers": {
        "1": {
          "userId": 2,
          "name": "상대닉네임",
          "department": "간호학과",
          "typeImageUrl2": "https://api.likelionhsu.co.kr/api/files/profile-types/type2.4.png",
          "avatarUrl": "https://api.likelionhsu.co.kr/api/files/profile-types/type2.4.png",
          "profileImageUrl": "https://api.likelionhsu.co.kr/api/files/profile-types/type2.4.png"
        }
      },
      "listCard": {},
      "createdAt": "2026-05-11T00:00:00Z"
    }

### 이벤트 코드 사용

요청

    POST /api/event/redeem
    Authorization: Bearer {accessToken}
    Content-Type: application/json

    {
      "code": "EVENT2026"
    }

응답

    {
      "matchCredits": 8,
      "signalCredits": 8
    }

### 랭킹 조회

요청

    GET /api/stats/rank/department-signals?limit=10
    Authorization: Bearer {accessToken}

응답

    [
      {
        "rank": 1,
        "department": "항공AI소프트웨어공학과",
        "count": 12,
        "imageUrl": "https://api.likelionhsu.co.kr/api/files/profile-types/type4.12.png"
      }
    ]

## 10. 데이터베이스 설계 🗄️

### 주요 테이블 요약

| 테이블 | 설명 |
|---|---|
| `users` | 카카오 계정 기반 사용자, 프로필, 성향 검사, 크레딧, 탈퇴 상태 저장 |
| `signals` | 사용자 간 시그널 상태 저장 |
| `seen_candidates` | 사용자가 이미 본 매칭 후보 기록 |
| `signal_logs` | 시그널 통계 산출용 로그 |
| `match_logs` | 매칭 성사 통계 산출용 로그 |
| `push_subscriptions` | Web Push 구독 정보 저장 |
| `verify_code` | 이벤트 코드와 사용 여부 저장 |

### users

| 컬럼 | 설명 |
|---|---|
| `id` | 사용자 ID |
| `kakao_id` | 카카오 사용자 ID |
| `email` | 카카오 계정 이메일 |
| `nickname` | 카카오 프로필 닉네임 |
| `gender` | 성별 |
| `name` | 서비스 내 닉네임 |
| `department` | 학과 |
| `student_no` | 학번 |
| `birth_year` | 출생 연도 |
| `profile_complete` | 프로필 입력 완료 여부 |
| `match_credits` | 매칭 가능 횟수 |
| `signal_credits` | 시그널 가능 횟수 |
| `version` | 낙관적 락 버전 |
| `dating_style_answers_json` | 10문항 답변 JSON |
| `dating_style_summary` | 성향 요약 |
| `dating_style_type_id` | 성향 유형 ID |
| `introduce` | 자기소개 |
| `instagram_url` | 인스타그램 URL |
| `style_recommended_partner` | 추천 파트너 설명 |
| `style_tags_json` | 성향 태그 JSON |
| `deactivated_at` | 탈퇴 처리 시각 |
| `profile_image_url` | 프로필 이미지 URL |
| `mbti` | MBTI |
| `egen_type` | EGEN/TETO 성향 |
| `last_match_json` | 최근 매칭 후보 JSON |
| `last_match_at` | 최근 매칭 시각 |
| `created_at` | 생성 시각 |
| `updated_at` | 수정 시각 |

### signals

| 컬럼 | 설명 |
|---|---|
| `id` | 시그널 ID |
| `sender_id` | 시그널 발신 사용자 ID |
| `receiver_id` | 시그널 수신 사용자 ID |
| `status` | `SENT`, `MUTUAL`, `DECLINED` |
| `receiver_deleted_at` | 수신자 목록에서 숨김 처리된 시각 |
| `version` | 낙관적 락 버전 |
| `created_at` | 생성 시각 |
| `updated_at` | 수정 시각 |

### seen_candidates

| 컬럼 | 설명 |
|---|---|
| `id` | 기록 ID |
| `viewer_id` | 후보를 본 사용자 ID |
| `seen_user_id` | 노출된 후보 사용자 ID |
| `created_at` | 생성 시각 |
| `updated_at` | 수정 시각 |

### signal_logs

| 컬럼 | 설명 |
|---|---|
| `id` | 로그 ID |
| `sender_id` | 발신 사용자 ID |
| `receiver_id` | 수신 사용자 ID |
| `receiver_department` | 수신자 학과 |
| `receiver_mbti` | 수신자 MBTI |
| `created_at` | 생성 시각 |
| `updated_at` | 수정 시각 |

### match_logs

| 컬럼 | 설명 |
|---|---|
| `id` | 로그 ID |
| `user_a_id` | 매칭 사용자 A |
| `user_b_id` | 매칭 사용자 B |
| `department_a` | 사용자 A 학과 |
| `department_b` | 사용자 B 학과 |
| `mbti_a` | 사용자 A MBTI |
| `mbti_b` | 사용자 B MBTI |
| `created_at` | 생성 시각 |
| `updated_at` | 수정 시각 |

### push_subscriptions

| 컬럼 | 설명 |
|---|---|
| `id` | 구독 ID |
| `user_id` | 사용자 ID |
| `endpoint` | Web Push endpoint |
| `p256dh` | Push 암호화 키 |
| `auth` | Push 인증 키 |
| `created_at` | 생성 시각 |
| `updated_at` | 수정 시각 |

### verify_code

| 컬럼 | 설명 |
|---|---|
| `id` | UUID 기반 코드 ID |
| `code` | 이벤트 코드 |
| `used` | 사용 여부 |
| `used_at` | 사용 시각 |
| `created_at` | 생성 시각 |
| `updated_at` | 수정 시각 |

### Entity 관계

| 관계 | 설명 |
|---|---|
| `users` 1 : N `signals.sender_id` | 한 사용자는 여러 시그널을 보낼 수 있음 |
| `users` 1 : N `signals.receiver_id` | 한 사용자는 여러 시그널을 받을 수 있음 |
| `users` 1 : 1 `push_subscriptions.user_id` | 사용자별 Web Push 구독 정보는 하나만 유지 |
| `seen_candidates.viewer_id`, `seen_candidates.seen_user_id` | JPA 연관관계 대신 사용자 ID 값으로 중복 노출 방지 기록 |
| `signal_logs`, `match_logs` | 통계 조회 최적화를 위해 사용자 관계 대신 필요한 스냅샷 값 저장 |

Firestore에는 JPA Entity는 아니지만 `chatRooms` 컬렉션이 사용됩니다. 이 컬렉션에는 `participants`, `peers`, `pairKey`, `listCard`, `createdAt` 정보가 저장됩니다.

## 11. 프로젝트 구조 📁

    src
     ├── main
     │   ├── java
     │   │   └── com
     │   │       └── example
     │   │           └── uni
     │   │               ├── UniApplication.java
     │   │               ├── auth
     │   │               │   ├── AuthController.java
     │   │               │   ├── CookieUtil.java
     │   │               │   ├── FirebaseBridgeService.java
     │   │               │   ├── JwtAuthFilter.java
     │   │               │   ├── JwtProvider.java
     │   │               │   ├── KakaoOAuthClient.java
     │   │               │   └── OAuthService.java
     │   │               ├── chat
     │   │               │   └── ChatRoomService.java
     │   │               ├── common
     │   │               │   ├── config
     │   │               │   │   ├── FirebaseConfig.java
     │   │               │   │   ├── HttpClientConfig.java
     │   │               │   │   ├── JpaConfig.java
     │   │               │   │   ├── SecurityConfig.java
     │   │               │   │   ├── StompAuthChannelInterceptor.java
     │   │               │   │   ├── UserPrincipalHandshakeHandler.java
     │   │               │   │   ├── WebSocketConfig.java
     │   │               │   │   └── WsJwtHandshakeInterceptor.java
     │   │               │   ├── domain
     │   │               │   │   ├── AfterCommitExecutor.java
     │   │               │   │   └── BaseTimeEntity.java
     │   │               │   ├── exception
     │   │               │   │   ├── ApiException.java
     │   │               │   │   ├── ErrorCode.java
     │   │               │   │   └── GlobalExceptionHandler.java
     │   │               │   └── realtime
     │   │               │       ├── RealtimeNotifier.java
     │   │               │       └── WsSessionRegistry.java
     │   │               ├── event
     │   │               │   ├── EventController.java
     │   │               │   ├── EventService.java
     │   │               │   ├── RedeemRequest.java
     │   │               │   ├── VerifyCode.java
     │   │               │   └── VerifyCodeRepository.java
     │   │               ├── match
     │   │               │   ├── MatchResultResponse.java
     │   │               │   ├── MatchingController.java
     │   │               │   ├── MatchingService.java
     │   │               │   ├── SeenCandidate.java
     │   │               │   ├── SeenCandidateRepository.java
     │   │               │   ├── Signal.java
     │   │               │   └── SignalRepository.java
     │   │               ├── picture
     │   │               │   ├── FileUploadController.java
     │   │               │   ├── StaticResourceConfig.java
     │   │               │   └── TypeImageUploadController.java
     │   │               ├── push
     │   │               │   ├── PushService.java
     │   │               │   ├── PushSubscriptionEntity.java
     │   │               │   └── PushSubscriptionRepository.java
     │   │               ├── rank
     │   │               │   ├── MatchLog.java
     │   │               │   ├── MatchLogRepository.java
     │   │               │   ├── MatchStatsController.java
     │   │               │   ├── SignalLog.java
     │   │               │   └── SignalLogRepository.java
     │   │               └── user
     │   │                   ├── ai
     │   │                   │   ├── GptTextGenClient.java
     │   │                   │   └── TextGenClient.java
     │   │                   ├── controller
     │   │                   │   ├── PeerDetailController.java
     │   │                   │   └── UserController.java
     │   │                   ├── domain
     │   │                   │   ├── Gender.java
     │   │                   │   └── User.java
     │   │                   ├── dto
     │   │                   │   ├── DatingStyleSummary.java
     │   │                   │   ├── PeerDetailResponse.java
     │   │                   │   ├── UserOnboardingRequest.java
     │   │                   │   └── UserProfileResponse.java
     │   │                   ├── repo
     │   │                   │   ├── UserCandidateRepository.java
     │   │                   │   └── UserRepository.java
     │   │                   └── service
     │   │                       └── UserService.java
     │   └── resources
     │       └── application.properties
     └── test
         └── java
             └── com
                 └── example
                     └── uni
                         └── UniApplicationTests.java

## 12. 트러블슈팅 🧯

### JWT 인증 실패와 탈퇴 사용자 접근 문제

**문제 상황**  
Access Token이 유효하더라도 탈퇴한 사용자가 기존 토큰으로 API를 호출할 수 있는 문제가 발생할 수 있습니다.

**원인**  
JWT 자체는 stateless 구조이기 때문에 토큰만 검증하면 사용자 계정의 현재 상태를 알 수 없습니다. 탈퇴 여부는 DB의 `deactivatedAt` 값을 함께 확인해야 합니다.

**해결 방법**  
`JwtAuthFilter`에서 JWT subject로 사용자 ID를 얻은 뒤 `UserRepository`로 사용자를 조회하고, 사용자가 없거나 `deactivatedAt`이 존재하면 즉시 `403`으로 차단했습니다. WebSocket 연결에서도 동일하게 사용자 상태를 확인합니다.

**결과**  
탈퇴 사용자 또는 존재하지 않는 사용자가 기존 토큰으로 API와 WebSocket에 접근하는 상황을 방지할 수 있게 되었습니다.

### 매칭 후보 중복 노출 문제

**문제 상황**  
매칭을 여러 번 실행할 때 같은 후보가 반복 노출되면 사용자는 서비스가 새 후보를 제공하지 못한다고 느낄 수 있습니다.

**원인**  
단순 랜덤 조회만 사용하면 과거에 본 후보, 이미 시그널을 보낸 후보, 이미 채팅방이 생긴 후보가 다시 포함될 수 있습니다.

**해결 방법**  
`seen_candidates` 테이블로 노출 이력을 저장하고, `UserCandidateRepository`에서 이미 본 사용자를 제외했습니다. Service 계층에서는 이미 보낸 시그널과 Firestore 채팅방 존재 여부까지 추가로 확인했습니다.

**결과**  
후보 추천의 반복성을 낮추고, 매칭 결과가 실제로 다음 행동으로 이어질 가능성이 높은 사용자 중심으로 정리되었습니다.

### 시그널 수락 후 채팅방 중복 생성 문제

**문제 상황**  
두 사용자 사이에서 시그널이 여러 방향으로 존재하거나 수락 요청이 반복될 경우 채팅방이 중복 생성될 수 있습니다.

**원인**  
채팅방 ID를 요청마다 새로 생성하면 같은 사용자 쌍에 대해 여러 방이 생길 수 있습니다.

**해결 방법**  
`ChatRoomService`에서 두 사용자 ID를 정렬한 뒤 `r_{작은ID}_{큰ID}` 형식으로 roomId를 고정했습니다. Firestore 문서가 이미 존재하면 새로 생성하지 않고 기존 문서를 반환합니다.

**결과**  
같은 사용자 쌍은 항상 같은 채팅방을 사용하게 되어 중복 방 생성 문제를 방지했습니다.

### 파일 업로드 검증 문제

**문제 상황**  
프로필 이미지 업로드 기능에서 이미지가 아닌 파일이나 지나치게 큰 파일이 저장될 수 있습니다.

**원인**  
클라이언트 검증만 의존하면 우회 요청으로 잘못된 파일이 서버에 저장될 수 있습니다.

**해결 방법**  
서버에서 파일 비어 있음, 10MB 초과, MIME 타입이 `image/`로 시작하는지 여부를 검증했습니다. 파일명은 UUID로 재생성해 원본 파일명 충돌을 줄였습니다.

**결과**  
업로드 가능한 파일 범위를 서버 기준으로 제한하고, 저장 파일명 충돌 가능성을 낮췄습니다.

### 실시간 알림과 트랜잭션 순서 문제

**문제 상황**  
시그널 저장이나 매칭 처리 트랜잭션이 실패했는데 WebSocket 알림이 먼저 전송되면, 사용자는 실제 DB 상태와 다른 알림을 받을 수 있습니다.

**원인**  
DB 트랜잭션과 WebSocket 전송은 서로 다른 실행 흐름이기 때문에 순서를 관리하지 않으면 불일치가 생길 수 있습니다.

**해결 방법**  
`AfterCommitExecutor`를 통해 트랜잭션이 활성화된 경우 커밋 이후에만 알림을 전송하도록 구성했습니다.

**결과**  
DB 반영이 완료된 이벤트에 대해서만 사용자 알림이 발송되도록 안정성을 높였습니다.

## 13. 프로젝트를 통해 배운 점 🌱

이 프로젝트를 구현하면서 단순 CRUD를 넘어 인증, 매칭 규칙, 실시간 알림, 외부 저장소 연동이 함께 동작하는 계층형 백엔드 구조를 경험했습니다. Controller는 요청과 응답의 경계를 담당하고, Service는 도메인 규칙과 트랜잭션을 처리하며, Repository는 데이터 접근을 담당하도록 역할을 나누는 것이 유지보수에 중요하다는 점을 체감했습니다.

또한 JWT 기반 인증은 토큰 검증만으로 끝나지 않고, 사용자 탈퇴 상태나 WebSocket 연결 상태까지 함께 고려해야 안전한 구조가 된다는 점을 배웠습니다. JPA Entity로 관리하는 데이터와 Firestore에 저장하는 채팅방 데이터를 함께 사용하면서, 관계형 데이터베이스와 문서형 저장소의 역할을 구분하는 경험도 할 수 있었습니다.

매칭과 시그널 기능에서는 사용자의 행동 이력을 저장하고, 중복 노출과 중복 채팅방 생성을 막는 설계가 서비스 품질에 직접적인 영향을 준다는 점을 확인했습니다. 특히 프론트엔드가 바로 사용할 수 있도록 응답 필드 alias를 함께 제공하고, 탈퇴 사용자를 안전하게 마스킹하는 과정에서 실제 서비스 운영을 고려한 API 설계 경험을 쌓았습니다.

## 14. 향후 개선 방향 🔧

| 개선 방향 | 설명 |
|---|---|
| 테스트 코드 보강 | 매칭 후보 필터링, 시그널 수락/거절, 이벤트 코드 사용, 탈퇴 마스킹에 대한 단위/통합 테스트 추가 |
| Swagger API 문서화 | Controller 기준 API 명세를 자동 문서화하여 프론트엔드 연동 효율 개선 |
| Refresh Token 저장소 개선 | 현재 Refresh Token은 JWT 검증 중심이므로, 서버 저장소를 두어 강제 만료와 기기별 로그아웃을 지원할 수 있음 |
| 관리자 기능 보강 | 유형 이미지 업로드 API는 존재하지만 SecurityConfig상 permitAll이므로, 관리자 인증/인가 체계 보강 필요 |
| 예외 응답 구조 통일 | 일부 컨트롤러에서 직접 Map 응답을 반환하므로 공통 응답 포맷을 확장해 일관성 강화 |
| 로그 관리 | 로그인, 매칭, 시그널, 파일 업로드, 외부 API 실패 로그를 운영 관점에서 구조화 |
| 파일 저장소 개선 | 로컬 파일 시스템 저장 방식을 S3 같은 외부 Object Storage로 확장 가능 |
| Web Push 활용 확대 | 구독 저장과 발송 서비스가 구현되어 있으므로 시그널/매칭 이벤트와 연결해 브라우저 푸시 알림 고도화 |
| 보안 설정 강화 | 운영 환경에서 CORS origin, 쿠키 도메인, 관리자 API, VAPID/카카오/Firebase 비밀값 관리를 더 엄격하게 분리 |
| 성능 최적화 | 매칭 후보 조회와 랭킹 집계 쿼리에 대한 인덱스 검토 및 캐싱 전략 추가 |
