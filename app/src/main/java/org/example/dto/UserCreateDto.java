package org.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.entity.PersonalInfo;
import org.example.entity.Role;
import org.example.validation.UpdateCheck;

public record UserCreateDto(
        @Valid PersonalInfo personalInfo,
        @NotNull String username,
        String info,
        @NotNull(groups = UpdateCheck.class)
        Role role,
        Integer companyId
) {
}
