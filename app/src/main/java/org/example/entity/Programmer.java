package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public class Programmer extends User {
    @Enumerated(EnumType.STRING)
    private Language language;
}
