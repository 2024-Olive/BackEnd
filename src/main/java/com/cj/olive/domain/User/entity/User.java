package com.cj.olive.domain.User.entity;

import com.cj.olive.domain.User.model.UserTypeEnum;
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
public class User extends BaseTime {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phoneNumber;

    private String deviceToken;

    private int threshold;

    @Enumerated(EnumType.STRING)
    private UserTypeEnum userType;

    @Builder
    public User(@NotNull String username, String nickname, String password, String phoneNumber, UserTypeEnum userType) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        // TODO: 추후에 나이 수집해서 나이별 심박수 정상 범위 체크할지 고민하기
        this.threshold = 100;
    }

    public UserTypeEnum updateUserType(UserTypeEnum userTypeEnum) {
        this.userType = userTypeEnum;
        return this.userType;
    }

    public int updateThresholdValue(int threshold) {
        this.threshold = threshold;
        return this.threshold;
    }

    public Boolean isAdmin() {
        return this.getUserType().equals(UserTypeEnum.ADMIN);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User user)) return false;

        return Objects.equals(this.id, user.getId()) &&
                Objects.equals(this.username, user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

}
