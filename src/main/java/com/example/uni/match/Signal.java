package com.example.uni.match;

import com.example.uni.common.domain.BaseTimeEntity;
import com.example.uni.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@Entity
@Table(name = "signals",
        uniqueConstraints = @UniqueConstraint(name="uk_signal_pair", columnNames = {"sender_id","receiver_id"}),
        indexes = {
                @Index(name="idx_signal_sender",          columnList = "sender_id"),
                @Index(name="idx_signal_receiver",        columnList = "receiver_id"),
                @Index(name="idx_signal_sender_status",   columnList = "sender_id,status"),
                @Index(name="idx_signal_receiver_status", columnList = "receiver_id,status")
        })
@NoArgsConstructor @AllArgsConstructor @Builder
public class Signal extends BaseTimeEntity {

    @Id @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false) @JoinColumn(name="sender_id", nullable=false)
    private User sender;

    @ManyToOne(optional = false) @JoinColumn(name="receiver_id", nullable=false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    public enum Status { SENT, MUTUAL, CANCELED, DECLINED }
}
