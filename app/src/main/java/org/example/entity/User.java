package org.example.entity;

import jakarta.persistence.*;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
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
@Audited
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Users")
public class User implements Comparable<User>, BaseEntity<Long> {
    public static final String USER_COMPANY_AND_PAYMENTS_GRAPH = "withCompanyAndPayment";
    public static final String USER_COMPANY_AND_CHAT_ENTITY_GRAPH = "WithCompanyAndChat";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @EqualsAndHashCode.Include
    @NotNull
    private String username;

    @Embedded
    @Valid
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
    @NotAudited
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<UserChat> userChats = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    @Builder.Default
    @NotAudited
    private List<Payment> payments = new ArrayList<>();

    @Override
    public int compareTo(User o) {
        return o.id.compareTo(id);
    }

    public String fullName() {
        return String.format("%s %s", getPersonalInfo().getFirstName(), getPersonalInfo().getLastName());
    }
}
