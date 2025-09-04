package com.example.uni.chat.service;

import com.example.uni.chat.domain.ChatMessage;
import com.example.uni.chat.domain.ChatRoom;
import com.example.uni.chat.domain.MessageType;
import com.example.uni.chat.repo.ChatMessageRepository;
import com.example.uni.chat.repo.ChatRoomRepository;
import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;

    @Transactional
    public ChatRoom createOrReuseRoom(UUID meId, UUID peerId){
        User me = userRepo.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User peer = userRepo.findById(peerId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        return roomRepo.findByUserAAndUserB(me, peer)
                .or(() -> roomRepo.findByUserBAndUserA(peer, me))
                .orElseGet(() -> roomRepo.save(ChatRoom.builder()
                        .userA(me).userB(peer)
                        .anonymousNameA("별"+me.getId().toString().substring(0,4))
                        .anonymousNameB("별"+peer.getId().toString().substring(0,4))
                        .accepted(false)
                        .build()));
    }

    @Transactional
    public void sendSignal(UUID roomId, UUID fromUserId, String action){
        ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User from = userRepo.findById(fromUserId).orElse(null);
        msgRepo.save(ChatMessage.builder()
                .room(room).sender(from).type(MessageType.SYSTEM_SIGNAL)
                .content("SIGNAL:"+action).build());
        if ("ACCEPT".equalsIgnoreCase(action)) room.setAccepted(true);
    }
}
