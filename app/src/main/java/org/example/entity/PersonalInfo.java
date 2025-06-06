package org.example.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 9154080960028288028L;

    private String firstName;
    private String lastName;

    @NotNull
    private LocalDate birthDate;
}
