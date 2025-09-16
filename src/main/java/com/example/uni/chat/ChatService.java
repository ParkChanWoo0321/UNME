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

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository roomRepo;
    private final UserRepository userRepo;

    @Transactional
    public ChatRoom createOrReuseRoom(Long meId, Long peerId) { // ← Long
        User me   = userRepo.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User peer = userRepo.findById(peerId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        Optional<ChatRoom> legacy = roomRepo.findByUserAAndUserB(me, peer)
                .or(() -> roomRepo.findByUserBAndUserA(me, peer));
        if (legacy.isPresent()) return legacy.get();

        // ID 오름차순으로 A/B 고정
        User a = me.getId().compareTo(peer.getId()) <= 0 ? me : peer;
        User b = (a == me) ? peer : me;

        Optional<ChatRoom> found = roomRepo.findByUserAAndUserB(a, b);
        if (found.isPresent()) return found.get();

        try {
            return roomRepo.saveAndFlush(
                    ChatRoom.builder()
                            .userA(a).userB(b)
                            .anonymousNameA("별" + last4(a.getId()))
                            .anonymousNameB("별" + last4(b.getId()))
                            .accepted(true)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            return roomRepo.findByUserAAndUserB(a, b).orElseThrow(() -> e);
        }
    }

    // Long ID 안전하게 끝 4자리 생성 (0패딩)
    private String last4(Long id) {
        long v = (id == null ? 0 : Math.abs(id)) % 10000;
        return String.format("%04d", v);
    }
}
