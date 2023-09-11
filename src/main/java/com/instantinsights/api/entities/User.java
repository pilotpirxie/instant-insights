package com.instantinsights.api.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "salt", nullable = false)
    private String salt;

    @Column(name = "email_verification_code", nullable = false)
    private String emailVerificationCode;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "register_ip", nullable = false)
    private InetAddress registerIp;

    @Column(name = "is_disabled", nullable = false)
    private boolean isDisabled;

    @Column(name = "totp_token")
    private String totpToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User(UUID id,
                String email,
                String password,
                String salt,
                String emailVerificationCode,
                LocalDateTime emailVerifiedAt,
                InetAddress registerIp,
                boolean isDisabled,
                String totpToken,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.emailVerificationCode = emailVerificationCode;
        this.emailVerifiedAt = emailVerifiedAt;
        this.registerIp = registerIp;
        this.isDisabled = isDisabled;
        this.totpToken = totpToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmailVerificationCode() {
        return emailVerificationCode;
    }

    public void setEmailVerificationCode(String emailVerificationCode) {
        this.emailVerificationCode = emailVerificationCode;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public InetAddress getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(InetAddress registerIp) {
        this.registerIp = registerIp;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public String getTotpToken() {
        return totpToken;
    }

    public void setTotpToken(String totpToken) {
        this.totpToken = totpToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}