package org.example.dto;

import org.example.entity.PersonalInfo;
import org.example.entity.Role;

public record UserCreateDto(
        PersonalInfo personalInfo,
        String username,
        String info,
        Role role,
        Integer companyId
) {
}
