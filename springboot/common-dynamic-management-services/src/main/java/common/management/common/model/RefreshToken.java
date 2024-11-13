package common.management.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken extends BaseEntity{
    @Column(nullable = false,unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private Instant expiryDate;

    @OneToOne
    private User user;

    public RefreshToken(Instant expiryDate, User user) {
        this.refreshToken = UUID.randomUUID().toString();
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public RefreshToken update(Instant expiryDate){
        this.refreshToken = UUID.randomUUID().toString();
        this.expiryDate = expiryDate;
        return this;
    }
}
