package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate birthDate;
    private Integer age;
    @Enumerated(EnumType.STRING)
    private Role role;
}
