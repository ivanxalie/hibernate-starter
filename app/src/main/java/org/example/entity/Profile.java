package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
//    @JoinColumn(name = "user_id")
    @PrimaryKeyJoinColumn
    private User user;

    private String street;
    @JdbcTypeCode(SqlTypes.CHAR)
    private String language;

    public void setUser(User user) {
        user.setProfile(this);
        this.user = user;
        this.id = user.getId();
    }
}
