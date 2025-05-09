package org.example.mapper;

import lombok.RequiredArgsConstructor;
import org.example.dto.CompanyReadDto;
import org.example.dto.UserReadDto;
import org.example.entity.Company;
import org.example.entity.User;

@RequiredArgsConstructor
public class UserReadMapper implements Mapper<User, UserReadDto> {
    private final Mapper<Company, CompanyReadDto> companyMapper;

    @Override
    public UserReadDto mapFrom(User user) {
        return new UserReadDto(
                user.getId(),
                user.getPersonalInfo(),
                user.getUsername(),
                user.getInfo(),
                user.getRole(),
                companyMapper.mapFrom(user.getCompany())
        );
    }
}
