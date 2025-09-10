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

    @Transactional
    public ChatRoom createOrReuseRoom(UUID meId, UUID peerId){
        User me   = userRepo.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User peer = userRepo.findById(peerId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        Optional<ChatRoom> legacy = roomRepo.findByUserAAndUserB(me, peer)
                .or(() -> roomRepo.findByUserBAndUserA(me, peer));
        if (legacy.isPresent()) return legacy.get();

        User a = me.getId().compareTo(peer.getId()) <= 0 ? me : peer;
        User b = (a == me) ? peer : me;

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
            return roomRepo.findByUserAAndUserB(a, b).orElseThrow(() -> e);
        }
    }
}