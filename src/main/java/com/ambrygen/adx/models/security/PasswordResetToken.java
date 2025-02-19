package com.ambrygen.adx.models.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ambrygen.adx.models.Auditable;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "password_reset_tokens")
@Data
public class PasswordResetToken extends Auditable {
    @Column(name = "reset_token", nullable = false, unique = true)
    private String resetToken;

    @Column(name = "expiry_date", nullable = false)
    Timestamp expiryDate;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PasswordResetToken)) {
            return false;
        }
        PasswordResetToken that = (PasswordResetToken) o;
        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
