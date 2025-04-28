package org.example.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

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
    private Birthday birthDate;
}
