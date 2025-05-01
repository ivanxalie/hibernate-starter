package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"company", "profile", "userChats"})
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
@NamedQuery(name = "findUserByName", query = """
select u
                                        from User u
                                        left join u.company c
                                        where u.personalInfo.firstName = :firstName
                                        and c.name = :companyName
                                        order by u.personalInfo.lastName desc
""")
public class User implements Comparable<User>, BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String username;

    @Embedded
    private PersonalInfo personalInfo;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JdbcTypeCode(SqlTypes.JSON)
    private String info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne
            (mappedBy = "user",
                    cascade = CascadeType.ALL,
                    fetch = FetchType.LAZY
            )
    private Profile profile;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<UserChat> userChats = new ArrayList<>();

    @Override
    public int compareTo(User o) {
        return o.id.compareTo(id);
    }
}
