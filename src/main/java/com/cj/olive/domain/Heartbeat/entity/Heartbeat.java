package com.cj.olive.domain.Heartbeat.entity;

import com.cj.olive.domain.User.entity.User;
import com.cj.olive.global.entity.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;


@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Heartbeat extends BaseTime {

    @Id
    @Column(name = "heartbeat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int avgBpm;
    private int maxBpm;
    private int minBpm;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Builder
    public Heartbeat(@NotNull User user, int avgBpm, int maxBpm, int minBpm) {
        this.avgBpm = avgBpm;
        this.maxBpm = maxBpm;
        this.minBpm = minBpm;
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Heartbeat report)) return false;

        return Objects.equals(this.id, report.getId()) &&
                Objects.equals(this.user, report.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user);
    }

}
