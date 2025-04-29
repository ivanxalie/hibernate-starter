package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"company", "profile", "chats"})
public class User {
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
                    fetch = FetchType.LAZY,
                    optional = false
            )
    private Profile profile;

    @ManyToMany
    @JoinTable(name = "users_chat",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id")
    )
    @Builder.Default
    private Set<Chat> chats = new HashSet<>();

    public void addChat(Chat chat) {
        chat.getUsers().add(this);
        chats.add(chat);
    }
}
