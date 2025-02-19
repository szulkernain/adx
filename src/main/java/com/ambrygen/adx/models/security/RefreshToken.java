package com.ambrygen.adx.models.security;

import com.ambrygen.adx.models.Auditable;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken extends Auditable {
    @Column(name = "token")
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RefreshToken)) {
            return false;
        }
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
