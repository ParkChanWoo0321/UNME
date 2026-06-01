# 너랑나랑 (UNME) 💘

<img width="267" height="70" alt="Image" src="https://github.com/user-attachments/assets/edacbf7d-5504-4d25-ac1f-32938bb6f758" />

## 개발자 및 개발 기간 👤

| 이름 | 역할 | 기간 |
|---|---|---|
| 박찬우 | Backend Developer | 25.09.08 ~ 26.09.28 |

## 1. 프로젝트 소개 🚀

<table>
  <tr>
    <td align="center">
      <img 
        src="https://github.com/user-attachments/assets/b579da8f-cb89-4de3-baf6-32ea544a6ff8"
        alt="프로젝트 이미지" 
        width="100%"
      />
    </td>
  </tr>
</table>

<br />

너랑나랑(UNME)은 한서대학교 학생들을 대상으로 한 대학생 소개팅·매칭 웹앱입니다. 사용자는 카카오 로그인을 통해 간편하게 서비스를 시작하고, 프로필 정보와 성향 검사를 입력한 뒤 자신에게 맞는 상대를 추천받을 수 있습니다. 서비스 흐름은 프로필 등록 → 성향 분석 → 매칭 후보 확인 → 플러팅 → 상호 수락 → 1:1 채팅으로 이어지며, 사용자가 복잡한 과정 없이 자연스럽게 인연을 찾을 수 있도록 설계하였습니다.

기존의 단순 랜덤 매칭 방식과 달리, 너랑나랑은 학과 다양성 보장, 중복 노출 방지, 과거 매칭·시그널 이력 관리, 크레딧 기반 사용 제한 등을 통해 보다 공정하고 안정적인 매칭 경험을 제공합니다. 또한 10문항 성향 검사를 기반으로 사용자의 연애 유형을 분석하고, MBTI와 유형 이미지를 자동으로 연결하여 사용자가 자신의 성향을 직관적으로 확인할 수 있도록 구성하였습니다.

매칭 이후에는 사용자가 마음에 드는 상대에게 플러팅을 보낼 수 있으며, 상대방이 이를 수락하면 자동으로 1:1 채팅방이 생성됩니다. 이를 통해 단순히 상대를 추천받는 것에서 끝나는 것이 아니라, 실제 대화와 연결까지 이어지는 완성형 교내 매칭 서비스를 목표로 하였습니다. 실시간 알림 기능을 통해 매칭, 플러팅, 채팅 이벤트를 즉시 확인할 수 있으며, 탈퇴 사용자 마스킹과 JWT 기반 인증을 적용하여 안전한 서비스 이용 환경을 제공합니다.

## 2. 프로젝트 기획 배경 📌

대학교 생활에서는 새로운 사람을 만나고 싶어도 같은 학과나 같은 활동 범위 안에서만 인간관계가 형성되는 경우가 많습니다. 특히 자연스럽게 이성을 만나거나 대화를 시작할 기회가 제한적이며, 관심이 있어도 직접 표현하기 어렵다는 점에서 교내 학생들을 위한 가볍고 안전한 연결 서비스의 필요성을 느꼈습니다.

기존 소개팅 서비스는 불특정 다수를 대상으로 하다 보니 신뢰성이 낮거나, 지역·나이·관심사와 같은 조건이 맞지 않아 실제 대학생이 사용하기에는 부담스러운 부분이 있습니다. 반면 너랑나랑은 한서대학교 학생이라는 공통된 기반 안에서 서비스를 제공하기 때문에 사용자는 보다 친근하고 안전한 환경에서 매칭을 경험할 수 있습니다. 또한 행사 포스터처럼 오프라인 부스와 QR 접근 방식을 함께 활용하면, 학생들이 직접 참여하고 체험할 수 있는 교내 이벤트형 서비스로도 확장할 수 있습니다.

이 프로젝트는 단순한 소개팅 기능 구현이 아니라, 실제 서비스 운영을 고려하여 기획하였습니다. 사용자가 반복적으로 같은 사람을 추천받지 않도록 중복 노출을 방지하고, 학과 다양성을 고려하여 더 넓은 교내 관계가 형성될 수 있도록 하였습니다. 또한 플러팅과 상호 수락 구조를 통해 일방적인 대화 요청이 아닌, 서로 관심이 있을 때만 채팅이 열리도록 설계하여 사용자의 부담을 줄였습니다.

따라서 너랑나랑은 대학생들이 가볍게 참여하면서도 안전하게 새로운 인연을 만들 수 있는 교내 맞춤형 매칭 서비스입니다. 매칭, 시그널, 채팅, 알림, 성향 분석 기능을 하나의 흐름으로 연결하여 “캠퍼스 로맨스, 지금 당장 시작하자”라는 서비스 콘셉트를 실제 웹앱으로 구현하고자 하였습니다.

## 3. 성과 및 회고 🏆

사진 넣을 곳

## 4. 주요 기능 ✨

### 4.1 카카오 로그인 및 사용자 생성

<table>
  <tr>
    <td align="center">
      <img 
        src="https://github.com/user-attachments/assets/43a5cc44-77c5-4de9-ad9c-33fd1ec79358"
        alt="프로젝트 이미지" 
        width="100%"
      />
    </td>
  </tr>
</table>
<br />

| 기능 | 설명 |
|---|---|
| 카카오 로그인 | 카카오 OAuth 인가 코드를 받아 사용자 정보를 조회하고 로그인 처리를 수행합니다. |
| 신규 사용자 생성 | 카카오 ID와 이메일 기준으로 기존 사용자를 찾고, 없으면 `users` 테이블에 신규 사용자를 생성합니다. |
| JWT 발급 | 로그인 성공 시 Access Token과 Refresh Token을 발급합니다. |
| Refresh Cookie 저장 | Refresh Token은 HttpOnly Cookie로 저장해 재발급에 사용합니다. |
| 탈퇴 계정 차단 | `deactivatedAt`이 존재하는 사용자는 재로그인을 차단합니다. |

### 4.2 프로필 등록 및 내 정보 관리

<table>
  <tr>
    <td width="50%" valign="top">
      <img 
        src="https://github.com/user-attachments/assets/f687fdcf-1840-4c22-bcb9-f24a16e8eb77" 
        width="100%" 
      />
      <br/>
      <img 
        src="https://github.com/user-attachments/assets/b021c153-7073-478d-b305-e6d4cb1799bf"
        width="100%" 
      />
    </td>
    <td width="50%" valign="top">
      <img 
        src="https://github.com/user-attachments/assets/5eacf553-bcbd-4d08-8022-c58023f14f16"
        width="100%" 
      />
      <br/>
      <img 
        src="https://github.com/user-attachments/assets/4229e2f9-aa87-45d4-9d68-7e27f858d295" 
        width="100%" 
      />
    </td>
  </tr>
</table>
<br />

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

<table>
  <tr>
    <td width="50%" valign="top" style="padding: 6px;">
      <img src="https://github.com/user-attachments/assets/94f00a9c-d98b-4e51-ba6c-5ccd8f4ec836" width="100%" />
    </td>
    <td width="50%" valign="top" style="padding: 6px;">
      <img src="https://github.com/user-attachments/assets/d597f699-45cc-4586-b5d3-f7be74905c15" width="100%" />
    </td>
  </tr>
  <tr>
    <td width="50%" valign="top" style="padding: 6px;">
      <img src="https://github.com/user-attachments/assets/620ab074-5bf8-415b-b365-c4c63c7d8447" width="100%" />
    </td>
    <td width="50%" valign="top" style="padding: 6px;">
      <img src="https://github.com/user-attachments/assets/1da22df5-e20a-4fd4-90e7-f366e4da98c9" width="100%" />
    </td>
  </tr>
  <tr>
    <td width="50%" valign="top" style="padding: 6px;">
      <img src="https://github.com/user-attachments/assets/30121636-2a17-4085-a186-a9dddeb9576c" width="100%" />
    </td>
    <td width="50%" valign="top" style="padding: 6px;">
      <img src="https://github.com/user-attachments/assets/23ce11ef-cc05-4346-8eed-0a875bfae0c1" width="100%" />
    </td>
  </tr>
</table>
<br />

| 기능 | 설명 |
|---|---|
| 10문항 성향 검사 | `q1`부터 `q10`까지의 A/B 답변을 저장합니다. |
| `typeId` 산출 | 답변의 A/B 개수를 기준으로 사용자 성향 유형 ID를 계산합니다. |
| EGEN/TETO 산출 | 답변 결과 또는 OpenAI 응답을 기반으로 `EGEN`, `TETO` 성향을 저장합니다. |
| 성향 요약 생성 | OpenAI API를 사용해 성향 요약, 추천 파트너 문장, 태그 3개를 생성합니다. |
| fallback 처리 | OpenAI API 호출 실패 시 기본 성향 요약과 기본 태그를 저장합니다. |
| 유형 이미지 매핑 | `typeImageUrl`, `typeImageUrl2`, `typeImageUrl3`, MBTI 기반 이미지 URL을 설정값에서 매핑합니다. |

### 4.4 스마트 매칭

<table>
  <tr>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
        src="https://github.com/user-attachments/assets/058d02de-5d22-4500-af95-52aac026e8e1" 
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
         src="https://github.com/user-attachments/assets/d211e473-d0d4-4f44-bf01-49c89d4d7ae4"
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
  </tr>
</table>
<br />

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

<table>
  <tr>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
        src="https://github.com/user-attachments/assets/fcffc0d4-b427-4e80-a06d-784e4ab37375" 
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
        src="https://github.com/user-attachments/assets/50a4b1af-4aab-4778-ab1a-3650148e0b95" 
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
  </tr>
</table>
<br />

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

<table>
  <tr>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
        src="https://github.com/user-attachments/assets/04d94d90-8a72-4414-9c47-5b6761f9686c" 
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
        src="https://github.com/user-attachments/assets/016a2b94-b411-4f06-9f98-655b0c22abea" 
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
  </tr>
</table>
<br />

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

<table>
  <tr>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
        src="https://github.com/user-attachments/assets/a539b2c8-bc18-4eae-ab7f-113ee0b65e61" 
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
        src="https://github.com/user-attachments/assets/2bc90fe7-20ec-4ec9-b185-940506a83b0c"
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
  </tr>
</table>
<br />

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

| 기능 | 설명 |
|---|---|
| 유형 이미지 업로드 | `/admin/type-images/{type}` API로 성향 유형 이미지를 업로드합니다. |
| type 값 검증 | `1~4`, `2.x`, `3.x`, `4.x`, `5.x` 형식의 허용된 타입만 저장합니다. |
| 프로필 유형 이미지 저장 | 업로드 파일을 `profile-types` 디렉터리에 저장합니다. |
| 설정 키 반환 | 업로드된 이미지가 어떤 `app.type-image...` 설정 키에 해당하는지 반환합니다. |

### 4.11 랭킹 및 통계

<table>
  <tr>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
        src="https://github.com/user-attachments/assets/1f2b822d-ebfd-4a9f-8e93-03fd1df7ce90" 
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
    <td width="50%" valign="top" style="padding: 6px;">
      <img 
        src="https://github.com/user-attachments/assets/5900729d-81c9-46b1-8372-c1d825efc085" 
        width="100%" 
        style="border: 1px solid #ddd; border-radius: 8px;"
      />
    </td>
  </tr>
</table>
<br />

| 기능 | 설명 |
|---|---|
| 학과별 시그널 랭킹 | `signal_logs`를 기준으로 시그널을 많이 받은 학과 순위를 조회합니다. |
| MBTI별 시그널 랭킹 | `signal_logs`를 기준으로 시그널을 많이 받은 MBTI 순위를 조회합니다. |
| 학과별 매칭 랭킹 | `match_logs`를 기준으로 매칭이 많이 성사된 학과 순위를 조회합니다. |
| MBTI별 매칭 랭킹 | `match_logs`를 기준으로 매칭이 많이 성사된 MBTI 순위를 조회합니다. |
| 랭킹 이미지 매핑 | 학과 또는 MBTI에 대응되는 유형 이미지를 함께 반환합니다. |

### 4.12 이벤트 코드 사용

<table>
  <tr>
    <td align="center">
      <img 
        src="https://github.com/user-attachments/assets/ba4b35a1-94c5-4c1f-80db-654f919c1820"
        alt="프로젝트 이미지" 
        width="100%"
      />
    </td>
  </tr>
</table>
<br />

| 기능 | 설명 |
|---|---|
| 이벤트 코드 입력 | 사용자가 이벤트 코드를 입력해 크레딧을 충전합니다. |
| 코드 대문자 정규화 | 입력된 코드는 trim 후 대문자로 변환해 검증합니다. |
| 1회성 사용 처리 | 사용되지 않은 코드만 `used=true`로 변경합니다. |
| 중복 사용 차단 | 이미 사용된 코드나 존재하지 않는 코드는 오류로 처리합니다. |
| 크레딧 지급 | 코드 사용 성공 시 매칭 크레딧과 시그널 크레딧을 각각 5개씩 증가시킵니다. |

### 4.13 푸시 구독 저장

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

`server.servlet.context-path=/api` 설정이 적용되어 있으므로, 아래 URL은 실제 외부 호출 경로 기준으로 작성했습니다.  
인증 필요 여부는 `SecurityConfig` 기준입니다.

### 8.1 공통 규칙

| 항목 | 내용 |
|---|---|
| Base Path | `/api` |
| 인증 방식 | `Authorization: Bearer {accessToken}` |
| Refresh Token 전달 방식 | HttpOnly Cookie, 기본 쿠키 이름 `REFRESH` |
| CORS Preflight | `OPTIONS /**` 전체 허용 |
| 공통 인증 실패 | 인증이 필요한 API에서 토큰이 없거나 유효하지 않으면 인증 실패 |
| 탈퇴 사용자 차단 | JWT가 유효해도 `deactivatedAt`이 존재하는 사용자는 차단 |
| 공통 예외 응답 | `{ "error": "ERROR_CODE", "message": "message" }` |
| Validation 예외 응답 | `{ "error": "VALIDATION_ERROR", "details": { "field": "message" } }` |

---

### 8.2 Auth API

#### 카카오 로그인 시작

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/auth/kakao/login` |
| 인증 필요 여부 | 아니오 |
| Controller | `AuthController.login` |
| 설명 | 카카오 OAuth 인가 URL로 리다이렉트합니다. |

**Query Parameter**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `next` | String | 아니오 | 로그인 완료 후 이동할 프론트엔드 경로 또는 URL |

**Response**

| 상태 코드 | 설명 |
|---|---|
| `302 Found` | 카카오 OAuth 인가 URL로 리다이렉트 |

---

#### 카카오 로그인 콜백

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/auth/kakao/callback` |
| 인증 필요 여부 | 아니오 |
| Controller | `AuthController.callback` |
| 설명 | 카카오 인가 코드를 받아 로그인 처리 후 프론트엔드로 리다이렉트합니다. |

**Query Parameter**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `code` | String | 예 | 카카오 OAuth 인가 코드 |
| `state` | String | 아니오 | 로그인 시작 시 전달한 이동 경로, 기본값 `/` |

**Response**

| 상태 코드 | 설명 |
|---|---|
| `302 Found` | 로그인 성공 시 Access Token을 URL fragment에 담아 프론트엔드로 리다이렉트 |
| `302 Found` | 탈퇴 계정이면 `error=ACCOUNT_DEACTIVATED` fragment를 담아 리다이렉트 |

**성공 Redirect 예시**

```text
{frontendBase}/matching#access={accessToken}
```

**탈퇴 계정 Redirect 예시**

```text
{frontendBase}/matching#error=ACCOUNT_DEACTIVATED
```

---

#### Access Token 재발급

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/auth/refresh` |
| 인증 필요 여부 | 아니오 |
| Controller | `AuthController.reissueAccess` |
| 설명 | Refresh Cookie를 검증하고 Access Token을 재발급합니다. |

**Cookie**

| 이름 | 필수 | 설명 |
|---|---|---|
| `REFRESH` | 예 | Refresh Token, 실제 이름은 `auth.cookie.name` 설정값 사용 |

**Response Body**

```json
{
  "accessToken": "jwt-access-token",
  "expiresIn": 1800
}
```

**Error Response**

```json
{
  "error": "NO_REFRESH"
}
```

```json
{
  "error": "NO_ACTIVE_USER"
}
```

---

#### 로그아웃

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/auth/logout` |
| 인증 필요 여부 | 아니오 |
| Controller | `AuthController.logout` |
| 설명 | Refresh Cookie를 제거합니다. |

**Response**

| 상태 코드 | 설명 |
|---|---|
| `204 No Content` | 로그아웃 성공 |

---

#### 내 인증 정보 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/auth/me` |
| 인증 필요 여부 | 예 |
| Controller | `AuthController.me` |
| 설명 | 로그인한 사용자의 프로필과 Firebase Custom Token을 반환합니다. |

**Header**

| 이름 | 필수 | 설명 |
|---|---|---|
| `Authorization` | 예 | `Bearer {accessToken}` |

**Response Body**

```json
{
  "firebaseCustomToken": "firebase-custom-token",
  "user": {
    "userId": 1,
    "kakaoId": "123456789",
    "email": "user@example.com",
    "nickname": "kakaoNickname",
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
    "typeImageUrl": "https://...",
    "typeImageUrl2": "https://...",
    "styleSummary": "성향 요약",
    "recommendedPartner": "추천 파트너",
    "tags": ["소통", "배려", "신뢰"],
    "introduce": "안녕하세요",
    "instagramUrl": "https://www.instagram.com/example",
    "mbti": "ENFP",
    "egenType": "에겐",
    "createdAt": "2026-06-01T00:00:00",
    "updatedAt": "2026-06-01T00:00:00"
  }
}
```

---

#### 카카오 연결 해제 및 탈퇴 처리

| 항목 | 내용 |
|---|---|
| Method | `DELETE` |
| URL | `/api/auth/kakao/unlink` |
| 인증 필요 여부 | 예 |
| Controller | `AuthController.unlink` |
| 설명 | 카카오 연결을 해제하고 사용자를 탈퇴 상태로 마스킹합니다. |

**Header**

| 이름 | 필수 | 설명 |
|---|---|---|
| `Authorization` | 예 | `Bearer {accessToken}` |

**Response**

| 상태 코드 | 설명 |
|---|---|
| `204 No Content` | 연결 해제 및 탈퇴 처리 성공 |

---

### 8.3 User API

#### 닉네임 중복 확인

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/users/me/name/check` |
| 인증 필요 여부 | 예 |
| Controller | `UserController.checkName` |
| 설명 | 서비스 내 닉네임 사용 가능 여부를 확인합니다. |

**Query Parameter**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `name` | String | 예 | 확인할 닉네임 |

**Response Body**

```json
{
  "available": true,
  "message": "사용 가능한 닉네임입니다."
}
```

---

#### 프로필 등록/수정

| 항목 | 내용 |
|---|---|
| Method | `PUT` |
| URL | `/api/users/me/profile` |
| 인증 필요 여부 | 예 |
| Controller | `UserController.profile` |
| 설명 | 기본 프로필, MBTI, 10문항 성향 답변을 등록하거나 수정합니다. |

**Request Body**

```json
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
```

**Request Field**

| 필드 | 타입 | 필수 | 검증/설명 |
|---|---|---|---|
| `name` | String | 예 | 2자 이상 8자 이하 |
| `department` | String | 예 | 학과 |
| `studentNo` | String | 예 | 학번 |
| `birthYear` | String | 예 | `1990~2006` 사이 4자리 |
| `gender` | Gender | 예 | `남자`, `여자`, `MALE`, `FEMALE` 허용 |
| `mbti` | String | 아니오 | MBTI |
| `q1` ~ `q10` | String | 예 | 각 문항 `a` 또는 `b` |

**Response Body**

`UserProfileResponse` 반환

```json
{
  "userId": 1,
  "kakaoId": "123456789",
  "email": "user@example.com",
  "nickname": "kakaoNickname",
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
  "typeImageUrl": "https://...",
  "typeImageUrl2": "https://...",
  "styleSummary": "성향 요약",
  "recommendedPartner": "추천 파트너",
  "tags": ["소통", "배려", "신뢰"],
  "introduce": null,
  "instagramUrl": null,
  "mbti": "ENFP",
  "egenType": "에겐",
  "createdAt": "2026-06-01T00:00:00",
  "updatedAt": "2026-06-01T00:00:00"
}
```

---

#### 내 프로필 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/users/me/profile` |
| 인증 필요 여부 | 예 |
| Controller | `UserController.getProfile` |
| 설명 | 로그인한 사용자의 프로필을 조회합니다. |

**Response Body**

`UserProfileResponse` 반환

---

#### 자기소개 수정

| 항목 | 내용 |
|---|---|
| Method | `PUT` |
| URL | `/api/users/me/introduce` |
| 인증 필요 여부 | 예 |
| Controller | `UserController.updateIntroduce` |
| 설명 | 로그인한 사용자의 자기소개를 수정합니다. |

**Request Body**

```json
{
  "introduce": "안녕하세요. 반갑습니다."
}
```

**Response Body**

`UserProfileResponse` 반환

---

#### 인스타그램 수정

| 항목 | 내용 |
|---|---|
| Method | `PUT` |
| URL | `/api/users/me/instagram` |
| 인증 필요 여부 | 예 |
| Controller | `UserController.updateInstagram` |
| 설명 | 인스타그램 ID 또는 URL을 받아 정규화된 인스타그램 URL로 저장합니다. |

**Request Body**

```json
{
  "instagram": "example_id"
}
```

또는

```json
{
  "instagramId": "example_id"
}
```

**Response Body**

`UserProfileResponse` 반환

---

#### 프로필 이미지 URL 수정

| 항목 | 내용 |
|---|---|
| Method | `PUT` |
| URL | `/api/users/me/profile-image` |
| 인증 필요 여부 | 예 |
| Controller | `UserController.updateProfileImage` |
| 설명 | 프로필 이미지 URL을 저장하고 기존 채팅방 표시 이미지에도 반영합니다. |

**Request Body**

```json
{
  "imageUrl": "https://~~/api/files/profile-images/1/image.png"
}
```

**Response Body**

`UserProfileResponse` 반환

---

#### 푸시 구독 저장

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/users/me/me/push/subscribe` |
| 인증 필요 여부 | 예 |
| Controller | `UserController.subscribePush` |
| 설명 | Web Push 구독 정보를 저장합니다. |

**Request Body**

```json
{
  "endpoint": "https://push-service.example/subscription",
  "keys": {
    "p256dh": "p256dh-key",
    "auth": "auth-key"
  }
}
```

**Response**

| 상태 코드 | 설명 |
|---|---|
| `200 OK` | 저장 성공, 응답 Body 없음 |

---

#### 상대 상세 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/users/{userId}` |
| 인증 필요 여부 | 예 |
| Controller | `PeerDetailController.peerDetail` |
| 설명 | 특정 사용자 상세 프로필을 조회합니다. |

**Path Variable**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `userId` | Long | 예 | 조회할 상대 사용자 ID |

**Response Body**

```json
{
  "userId": 2,
  "name": "상대닉네임",
  "department": "간호학과",
  "studentNo": "21",
  "birthYear": "2002",
  "gender": "여자",
  "typeTitle": "따뜻한 통찰력형",
  "typeContent": "깊은 교감의 매력 소유자!",
  "typeImageUrl": "https://...",
  "typeImageUrl2": "https://...",
  "styleSummary": "성향 요약",
  "recommendedPartner": "추천 파트너",
  "tags": ["안정감", "소통", "배려"],
  "introduce": "안녕하세요",
  "instagramUrl": "https://www.instagram.com/example",
  "mbti": "INFJ",
  "egenType": "에겐"
}
```

---

### 8.4 Matching / Signal API

#### 최근 매칭 결과 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/match/previous` |
| 인증 필요 여부 | 예 |
| Controller | `MatchingController.previous` |
| 설명 | 로그인한 사용자의 직전 매칭 후보 목록을 조회합니다. |

**Response Body**

```json
{
  "candidates": [
    {
      "userId": 2,
      "name": "상대닉네임",
      "department": "간호학과",
      "introduce": "안녕하세요",
      "typeImageUrl": "https://...",
      "typeImageUrl2": "https://...",
      "typeImageUrl3": "https://...",
      "avatarUrl": "https://...",
      "profileImageUrl": "https://...",
      "id": 2,
      "targetUserId": 2,
      "nickname": "상대닉네임",
      "major": "간호학과"
    }
  ]
}
```

---

#### 시그널 상태 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/signals/{targetId}/status` |
| 인증 필요 여부 | 예 |
| Controller | `MatchingController.signalStatus` |
| 설명 | 특정 사용자에게 이미 시그널을 보냈는지 확인합니다. |

**Path Variable**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `targetId` | Long | 예 | 시그널 상태를 확인할 상대 사용자 ID |

**Response Body**

```json
{
  "alreadySent": true
}
```

---

#### 매칭 시작

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/match/start` |
| 인증 필요 여부 | 예 |
| Controller | `MatchingController.start` |
| 설명 | 매칭 크레딧을 사용해 최대 3명의 후보를 조회합니다. |

**Response Body**

```json
{
  "candidates": [
    {
      "userId": 2,
      "name": "상대닉네임",
      "department": "간호학과",
      "introduce": "안녕하세요",
      "typeImageUrl2": "https://...",
      "id": 2,
      "targetUserId": 2,
      "nickname": "상대닉네임",
      "major": "간호학과",
      "avatarUrl": "https://...",
      "profileImageUrl": "https://..."
    }
  ]
}
```

---

#### 시그널 보내기

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/signals/{targetId}` |
| 인증 필요 여부 | 예 |
| Controller | `MatchingController.sendSignal` |
| 설명 | 특정 사용자에게 시그널을 보냅니다. |

**Path Variable**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `targetId` | Long | 예 | 시그널을 받을 사용자 ID |

**Response Body**

```json
{
  "signalId": 10,
  "status": "SENT"
}
```

---

#### 시그널 거절

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/signals/decline/{signalId}` |
| 인증 필요 여부 | 예 |
| Controller | `MatchingController.decline` |
| 설명 | 받은 시그널을 거절합니다. |

**Path Variable**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `signalId` | Long | 예 | 거절할 시그널 ID |

**Response Body**

```json
{
  "ok": true
}
```

---

#### 시그널 수락

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/signals/accept/{signalId}` |
| 인증 필요 여부 | 예 |
| Controller | `MatchingController.accept` |
| 설명 | 받은 시그널을 수락하고 Firestore 1:1 채팅방을 생성합니다. |

**Path Variable**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `signalId` | Long | 예 | 수락할 시그널 ID |

**Response Body**

```json
{
  "roomId": "r_1_2",
  "participants": [1, 2],
  "peers": {
    "1": {
      "userId": 2,
      "name": "상대닉네임",
      "department": "간호학과",
      "typeImageUrl2": "https://...",
      "avatarUrl": "https://...",
      "profileImageUrl": "https://..."
    },
    "2": {
      "userId": 1,
      "name": "내닉네임",
      "department": "항공AI소프트웨어공학과",
      "typeImageUrl2": "https://...",
      "avatarUrl": "https://...",
      "profileImageUrl": "https://..."
    }
  },
  "listCard": {
    "1": {
      "userId": 2,
      "name": "상대닉네임",
      "department": "간호학과",
      "typeImageUrl2": "https://...",
      "avatarUrl": "https://...",
      "profileImageUrl": "https://..."
    }
  },
  "createdAt": "2026-06-01T00:00:00Z"
}
```

---

#### 보낸 시그널 목록 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/signals/sent` |
| 인증 필요 여부 | 예 |
| Controller | `MatchingController.listSent` |
| 설명 | 로그인한 사용자가 보낸 시그널 목록을 조회합니다. |

**Response Body**

```json
[
  {
    "signalId": 10,
    "toUser": {
      "userId": 2,
      "name": "상대닉네임",
      "department": "간호학과",
      "typeImageUrl2": "https://...",
      "avatarUrl": "https://...",
      "profileImageUrl": "https://..."
    },
    "status": "SENT",
    "createdAt": "2026-06-01T00:00:00",
    "message": "성공적으로 신호를 보냈어요!"
  }
]
```

---

#### 받은 시그널 목록 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/signals/received` |
| 인증 필요 여부 | 예 |
| Controller | `MatchingController.listReceived` |
| 설명 | 로그인한 사용자가 받은 `SENT` 상태의 시그널 목록을 조회합니다. |

**Response Body**

```json
[
  {
    "signalId": 10,
    "fromUser": {
      "userId": 2,
      "name": "상대닉네임",
      "department": "간호학과",
      "typeImageUrl2": "https://...",
      "id": 2,
      "targetUserId": 2,
      "nickname": "상대닉네임",
      "major": "간호학과",
      "avatarUrl": "https://...",
      "profileImageUrl": "https://..."
    },
    "status": "SENT",
    "createdAt": "2026-06-01T00:00:00",
    "message": "새로운 신호가 있어요!"
  }
]
```

---

### 8.5 Rank / Stats API

#### 학과별 매칭 랭킹 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/stats/rank/department-matches` |
| 인증 필요 여부 | 예 |
| Controller | `MatchStatsController.departmentMatchRanking` |
| 설명 | 매칭 로그 기준 학과별 매칭 랭킹을 조회합니다. |

**Query Parameter**

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---|---|---|---|
| `limit` | int | 아니오 | `10` | 조회할 랭킹 개수 |

**Response Body**

```json
[
  {
    "rank": 1,
    "department": "항공AI소프트웨어공학과",
    "count": 12,
    "imageUrl": "https://..."
  }
]
```

---

#### MBTI별 시그널 랭킹 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/stats/rank/mbti-signals` |
| 인증 필요 여부 | 예 |
| Controller | `MatchStatsController.mbtiSignalsRanking` |
| 설명 | 시그널 로그 기준 MBTI별 시그널 랭킹을 조회합니다. |

**Query Parameter**

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---|---|---|---|
| `limit` | int | 아니오 | `10` | 조회할 랭킹 개수 |

**Response Body**

```json
[
  {
    "rank": 1,
    "mbti": "ENFP",
    "count": 10,
    "imageUrl": "https://..."
  }
]
```

---

#### MBTI별 매칭 랭킹 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/stats/rank/mbti-matches` |
| 인증 필요 여부 | 예 |
| Controller | `MatchStatsController.mbtiMatchesRanking` |
| 설명 | 매칭 로그 기준 MBTI별 매칭 랭킹을 조회합니다. |

**Query Parameter**

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---|---|---|---|
| `limit` | int | 아니오 | `10` | 조회할 랭킹 개수 |

**Response Body**

```json
[
  {
    "rank": 1,
    "mbti": "ENFP",
    "count": 8,
    "imageUrl": "https://..."
  }
]
```

---

#### 학과별 시그널 랭킹 조회

| 항목 | 내용 |
|---|---|
| Method | `GET` |
| URL | `/api/stats/rank/department-signals` |
| 인증 필요 여부 | 예 |
| Controller | `MatchStatsController.departmentSignalRanking` |
| 설명 | 시그널 로그 기준 학과별 시그널 랭킹을 조회합니다. |

**Query Parameter**

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---|---|---|---|
| `limit` | int | 아니오 | `10` | 조회할 랭킹 개수 |

**Response Body**

```json
[
  {
    "rank": 1,
    "department": "간호학과",
    "count": 15,
    "imageUrl": "https://..."
  }
]
```

---

### 8.6 Event API

#### 이벤트 코드 사용

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/event/redeem` |
| 인증 필요 여부 | 예 |
| Controller | `EventController.redeem` |
| 설명 | 이벤트 코드를 사용하고 매칭/시그널 크레딧을 충전합니다. |

**Request Body**

```json
{
  "code": "EVENT2026"
}
```

**Request Field**

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `code` | String | 예 | 사용할 이벤트 코드 |

**Response Body**

```json
{
  "matchCredits": 8,
  "signalCredits": 8
}
```

---

### 8.7 File API

#### 프로필 이미지 업로드

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/files/upload` |
| 인증 필요 여부 | 아니오 |
| Controller | `FileUploadController.upload` |
| Content-Type | `multipart/form-data` |
| 설명 | 이미지 파일을 업로드하고 접근 가능한 URL을 반환합니다. 인증 토큰이 있으면 사용자 ID별 폴더에 저장됩니다. |

**Multipart Form Data**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `file` | MultipartFile | 예 | 업로드할 이미지 파일 |

**검증 조건**

| 조건 | 실패 응답 |
|---|---|
| 파일이 비어 있음 | `{ "error": "EMPTY_FILE" }` |
| 파일 크기 10MB 초과 | `{ "error": "FILE_TOO_LARGE" }` |
| MIME 타입이 `image/`로 시작하지 않음 | `{ "error": "NOT_IMAGE" }` |

**Response Body**

```json
{
  "url": "https://~~/api/files/profile-images/1/image.png"
}
```

---

#### 유형 이미지 업로드

| 항목 | 내용 |
|---|---|
| Method | `POST` |
| URL | `/api/admin/type-images/{type}` |
| 인증 필요 여부 | 아니오 |
| Controller | `TypeImageUploadController.upload` |
| Content-Type | `multipart/form-data` |
| 설명 | 성향/학과/MBTI 유형 이미지를 업로드합니다. 코드상 현재 SecurityConfig에서 인증 없이 허용됩니다. |

**Path Variable**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `type` | String | 예 | 업로드할 유형 이미지 타입 |

**허용되는 `type` 형식**

| 형식 | 설명 |
|---|---|
| `1` ~ `4` | 기본 유형 이미지 |
| `2.1` ~ `2.4` | 확장 유형 이미지 2번 세트 |
| `3.1` ~ `3.5` | 확장 유형 이미지 3번 세트 |
| `4.default`, `4.1` ~ `4.54` | 학과/랭킹용 유형 이미지 |
| `5.1` ~ `5.16` | MBTI용 유형 이미지 |

**Multipart Form Data**

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `file` | MultipartFile | 예 | 업로드할 이미지 파일 |

**Response Body**

```json
{
  "type": "2.1",
  "saved": "/srv/unme/uploads/profile-types/type2.1.png",
  "url": "https://host/api/files/profile-types/type2.1.png",
  "propertyKey": "app.type-image2.1",
  "propertyValue": "https://host/api/files/profile-types/type2.1.png"
}
```

---

### 8.8 Static Resource API

`StaticResourceConfig`에서 등록된 정적 파일 제공 경로입니다. Controller API는 아니지만 프로젝트 코드에 존재하는 외부 접근 경로입니다.

| 기능 | Method | URL | 인증 필요 여부 | 설명 |
|---|---|---|---|---|
| 업로드 파일 조회 | GET | `/api/files/**` | 아니오 | `app.upload.dir` 아래 파일을 정적 리소스로 제공합니다. |
| 업로드 파일 조회 | GET | `/api/api/files/**` | 아니오 | 코드상 `/api/files/**` ResourceHandler도 등록되어 있어 context-path 적용 시 접근 가능한 경로입니다. |

---

### 8.9 WebSocket / STOMP API

`WebSocketConfig`에서 등록된 WebSocket 엔드포인트입니다. HTTP Controller는 아니지만 실시간 알림 기능에 사용됩니다.

| 구분 | URL 또는 Prefix | 인증 필요 여부 | 설명 |
|---|---|---|---|
| WebSocket Endpoint | `/api/ws` | 연결 시 JWT 검증 가능 | 코드상 `/ws` 엔드포인트가 등록되어 있고 context-path `/api`가 적용된 외부 접근 경로 |
| WebSocket Endpoint | `/api/api/ws` | 연결 시 JWT 검증 가능 | 코드상 `/api/ws` 엔드포인트도 등록되어 있어 context-path 적용 시 접근 가능한 경로 |
| SockJS Endpoint | `/api/ws` | 연결 시 JWT 검증 가능 | SockJS fallback 지원 |
| SockJS Endpoint | `/api/api/ws` | 연결 시 JWT 검증 가능 | SockJS fallback 지원 |
| Application Destination Prefix | `/app` | 예 | 클라이언트가 메시지를 보낼 때 사용하는 prefix |
| Simple Broker Prefix | `/topic` | 예 | 브로드캐스트/토픽 메시지 prefix |
| Simple Broker Prefix | `/queue` | 예 | 큐 메시지 prefix |
| User Destination Prefix | `/user` | 예 | 사용자별 개인 큐 prefix |
| Signal Queue | `/user/queue/signals` | 예 | 시그널 수신/거절 알림을 받는 개인 큐 |
| Match Queue | `/user/queue/matches` | 예 | 매칭 성사 알림을 받는 개인 큐 |

**WebSocket 인증 방식**

| 방식 | 설명 |
|---|---|
| Query Token | Handshake 단계에서 `?token={accessToken}`을 전달하면 `WsJwtHandshakeInterceptor`가 검증 |
| STOMP Header | CONNECT 단계에서 `Authorization: Bearer {accessToken}` 전달 시 `StompAuthChannelInterceptor`가 검증 |
| 연결 차단 조건 | 토큰 누락, 만료, 형식 오류, 탈퇴 사용자, 존재하지 않는 사용자 |

## 9. 데이터베이스 설계 🗄️

<table>
  <tr>
    <td align="center">
      <img 
        src="https://github.com/user-attachments/assets/2366ed3b-38d1-4854-a760-40bb2504ce84"
        alt="프로젝트 이미지" 
        width="100%"
      />
    </td>
  </tr>
</table>
<br />

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

## 10. 프로젝트 구조 📁

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

## 11. 트러블슈팅 🧯

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

## 12. 프로젝트를 통해 배운 점 🌱

이 프로젝트를 구현하면서 단순 CRUD를 넘어 인증, 매칭 규칙, 실시간 알림, 외부 저장소 연동이 함께 동작하는 계층형 백엔드 구조를 경험했습니다. Controller는 요청과 응답의 경계를 담당하고, Service는 도메인 규칙과 트랜잭션을 처리하며, Repository는 데이터 접근을 담당하도록 역할을 나누는 것이 유지보수에 중요하다는 점을 체감했습니다.

또한 JWT 기반 인증은 토큰 검증만으로 끝나지 않고, 사용자 탈퇴 상태나 WebSocket 연결 상태까지 함께 고려해야 안전한 구조가 된다는 점을 배웠습니다. JPA Entity로 관리하는 데이터와 Firestore에 저장하는 채팅방 데이터를 함께 사용하면서, 관계형 데이터베이스와 문서형 저장소의 역할을 구분하는 경험도 할 수 있었습니다.

매칭과 시그널 기능에서는 사용자의 행동 이력을 저장하고, 중복 노출과 중복 채팅방 생성을 막는 설계가 서비스 품질에 직접적인 영향을 준다는 점을 확인했습니다. 특히 프론트엔드가 바로 사용할 수 있도록 응답 필드 alias를 함께 제공하고, 탈퇴 사용자를 안전하게 마스킹하는 과정에서 실제 서비스 운영을 고려한 API 설계 경험을 쌓았습니다.

## 13. 향후 개선 방향 🔧

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
