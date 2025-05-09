package org.example.dto;

import org.example.entity.PersonalInfo;
import org.example.entity.Role;

public record UserReadDto(
        Long id,
        PersonalInfo personalInfo,
        String username,
        String info,
        Role role,
        CompanyReadDto company
) {

}
