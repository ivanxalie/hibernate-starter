package org.example.service;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.Subgraph;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserCreateDto;
import org.example.dto.UserReadDto;
import org.example.entity.Company;
import org.example.entity.User;
import org.example.mapper.UserCreateMapper;
import org.example.mapper.UserReadMapper;
import org.example.repository.UserRepository;
import org.example.validation.UpdateCheck;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserReadMapper userReadMapper;
    private final UserCreateMapper userCreateMapper;

    @Transactional
    public Long create(UserCreateDto userDto) {
        // validate
        @Cleanup ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        var result = validator.validate(userDto, UpdateCheck.class);
        if (!result.isEmpty())
            throw new ConstraintViolationException(result);
        User user = userCreateMapper.mapFrom(userDto);
        return userRepository.save(user).getId();
    }

    @Transactional
    public boolean delete(Long id) {
        var maybeUser = userRepository.findById(id);
        maybeUser.ifPresent(userRepository::delete);
        return maybeUser.isPresent();
    }

    @Transactional
    public Optional<UserReadDto> findById(Long id) {
        Map<String, Object> params = new HashMap<>();
        EntityGraph<User> userEntityGraph = userRepository.createAndPutEntityGraphIntoMap(params);
        userEntityGraph.addAttributeNodes("company");
        Subgraph<Company> companySubgraph = userEntityGraph.addSubgraph("company");
        companySubgraph.addAttributeNodes("locales");
        return findById(id, params);
    }

    @Transactional
    public Optional<UserReadDto> findById(Long id, Map<String, Object> props) {
        return userRepository
                .findById(id, props)
                .map(userReadMapper::mapFrom);
    }
}
