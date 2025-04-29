package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String street;
    @JdbcTypeCode(SqlTypes.CHAR)
    private String language;

    public void setUser(User user) {
        user.setProfile(this);
        this.user = user;
    }
}
