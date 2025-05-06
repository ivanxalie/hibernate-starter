package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

import static org.example.entity.User.USER_COMPANY_AND_CHAT_ENTITY_GRAPH;
import static org.example.entity.User.USER_COMPANY_AND_PAYMENTS_GRAPH;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"company",
//        "profile",
        "userChats",
        "payments"})
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
@FetchProfile(
        name = USER_COMPANY_AND_PAYMENTS_GRAPH,
        fetchOverrides = {
                @FetchProfile.FetchOverride(
                        entity = User.class,
                        association = "company",
                        mode = FetchMode.JOIN
                ),
                @FetchProfile.FetchOverride(
                        entity = User.class,
                        association = "payments",
                        mode = FetchMode.JOIN
                )
        }
)
@NamedEntityGraph(
        name = USER_COMPANY_AND_CHAT_ENTITY_GRAPH,
        attributeNodes = {
                @NamedAttributeNode("company"),
                @NamedAttributeNode(value = "userChats", subgraph = "chats")
        },
        subgraphs = {
                @NamedSubgraph(name = "chats", attributeNodes = {
                        @NamedAttributeNode("chat")
                })
        }
)
public class User implements Comparable<User>, BaseEntity<Long> {
    public static final String USER_COMPANY_AND_PAYMENTS_GRAPH = "withCompanyAndPayment";
    public static final String USER_COMPANY_AND_CHAT_ENTITY_GRAPH = "WithCompanyAndChat";

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
//    @Fetch(FetchMode.JOIN)
    private Company company;

    //    @OneToOne
//            (mappedBy = "user",
//                    cascade = CascadeType.ALL,
//                    fetch = FetchType.LAZY
//            )
    @Transient
    private Profile profile;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<UserChat> userChats = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    @Builder.Default
//    @Fetch(FetchMode.SUBSELECT)
    private List<Payment> payments = new ArrayList<>();

    @Override
    public int compareTo(User o) {
        return o.id.compareTo(id);
    }

    public String fullName() {
        return String.format("%s %s", getPersonalInfo().getFirstName(), getPersonalInfo().getLastName());
    }
}
