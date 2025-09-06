package com.example.uni.chat;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository roomRepo;
    private final UserRepository userRepo;

    /** 상호 수락 이후 방 생성/재사용: 생성 시 accepted=true (동시성 안전) */
    @Transactional
    public ChatRoom createOrReuseRoom(UUID meId, UUID peerId){
        User me   = userRepo.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User peer = userRepo.findById(peerId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        // 레거시 레코드 호환: 두 방향 모두 먼저 조회
        Optional<ChatRoom> legacy = roomRepo.findByUserAAndUserB(me, peer)
                .or(() -> roomRepo.findByUserBAndUserA(me, peer));
        if (legacy.isPresent()) return legacy.get();

        // 캐노니컬 정렬(항상 userA.id <= userB.id)
        User a = me.getId().compareTo(peer.getId()) <= 0 ? me : peer;
        User b = (a == me) ? peer : me;

        // 다시 조회(캐노니컬) 후 생성 시도
        Optional<ChatRoom> found = roomRepo.findByUserAAndUserB(a, b);
        if (found.isPresent()) return found.get();

        try {
            return roomRepo.save(ChatRoom.builder()
                    .userA(a).userB(b)
                    .anonymousNameA("별" + a.getId().toString().substring(0,4))
                    .anonymousNameB("별" + b.getId().toString().substring(0,4))
                    .accepted(true)
                    .build());
        } catch (DataIntegrityViolationException e) {
            // 유니크 제약 충돌 ⇒ 누군가 먼저 만듦 → 재조회 반환
            return roomRepo.findByUserAAndUserB(a, b).orElseThrow(() -> e);
        }
    }
}