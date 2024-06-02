package com.imd.petcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {

    @Column(name = "token", nullable = false)
    @NotBlank(message = "O token de recuperação é obrigatório.")
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JoinColumn(name = "is_used")
    private boolean isUsed;

    public RefreshToken() { }

    public RefreshToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String code) {
        this.token = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean active) {
        this.isUsed = active;
    }

    public boolean isValid() {
        ZonedDateTime fiveMinutesAgo = ZonedDateTime.now().minus(5, ChronoUnit.MINUTES);
        return !createdAt.isBefore(fiveMinutesAgo) && !isUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(token, that.token) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, user);
    }

}
