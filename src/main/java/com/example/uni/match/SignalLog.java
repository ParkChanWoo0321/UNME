// com/example/uni/match/SignalLog.java
package com.example.uni.match;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "signal_logs",
        indexes = @Index(name = "idx_siglog_rcv_dept", columnList = "receiver_department"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "receiver_department")
    private String receiverDepartment;
}
