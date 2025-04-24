package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.converter.BirthdayConverter;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String username;
    private String firstName;
    private String lastName;
    @Convert(converter = BirthdayConverter.class)
    private Birthday birthDate;
    @Enumerated(EnumType.STRING)
    private Role role;
}
