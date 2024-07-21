package com.cj.olive.domain.Report.entity;

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
public class Report extends BaseTime {

    @Id
    @Column(name = "report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float latitude;

    private Float longitude;

    private int bpm;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public Report(@NotNull User user, Float latitude, Float longitude, int bpm) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.bpm = bpm;
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Report report)) return false;

        return Objects.equals(this.id, report.getId()) &&
                Objects.equals(this.user, report.getUser()) &&
                Objects.equals(this.latitude, report.getLatitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, latitude);
    }

}
