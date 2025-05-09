package org.example.mapper;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserCreateDto;
import org.example.entity.User;
import org.example.repository.CompanyRepository;

@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<UserCreateDto, User> {
    private final CompanyRepository companyRepository;

    @Override
    public User mapFrom(UserCreateDto userCreateDto) {
        return User.builder()
                .username(userCreateDto.username())
                .personalInfo(userCreateDto.personalInfo())
                .info(userCreateDto.info())
                .role(userCreateDto.role())
                .company(companyRepository.findById(userCreateDto.companyId())
                        .orElseThrow(IllegalArgumentException::new))
                .build();
    }
}
