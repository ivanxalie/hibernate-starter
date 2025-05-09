package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.MapKeyJdbcTypeCode;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "users")
@Audited
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Companies")
public class Company implements BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @MapKey(name = "username")
    @NotAudited
    private SortedMap<String, User> users = new TreeMap<>();

    @ElementCollection
    @CollectionTable(name = "company_locale", joinColumns = @JoinColumn(name = "company_id"))
    @Builder.Default
    @Column(name = "description")
    @MapKeyColumn(name = "lang")
    @MapKeyJdbcTypeCode(SqlTypes.CHAR)
    @NotAudited
    private Map<String, String> locales = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getUsername(), user);
        user.setCompany(this);
    }
}
