package com.example.demo.service.impl;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.request.UpdateRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.PageResponseCriteria;
import com.example.demo.dto.response.UserInfoResponse;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.DataInUseException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.SearchRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.specification.SearchOperator;
import com.example.demo.repository.specification.UserSpecificationBuilder;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SearchRepository searchRepository;

    @Override
    @Transactional
    public long addUser(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DataInUseException("Username is already in use");
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.getAddresses().forEach(user::saveAddress);

        Set<Role> roles = new HashSet<>();
        request.getRoles().forEach(roleRequest -> {
            Role role = roleRepository.findByNameWithUsers(roleRequest.getName().name())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            roles.add(role);
        });

        roles.forEach(user::saveRole);

        userRepository.save(user);

        return user.getId();
    }

    @Override
    public User getUser(Long id) {
        return getUserByIdWithRolesAndAddresses(id);
    }

    @Override
    @Transactional
    public long updateUser(Long id, UserRequest request) {
        User user = userRepository.findByIdWithRolesAndAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var requestUser = userRepository.findByUsername(request.getUsername());

        if (requestUser.isEmpty()) throw new ResourceNotFoundException("User not found");

        if (!requestUser.get().getUsername().equals(user.getUsername()))
            throw new InvalidDataException("Request username does not match with username with id: " + id);

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.getAddresses().forEach(user::saveAddress);

        // Delete old roles from user
        user.getRoles().forEach(role -> role.getUsers().remove(user));
        user.getRoles().clear();

        assignRolesToUser(user, request.getRoles());

        return user.getId();
    }
    @Override
    public long deleteUser(Long id) {
        getUserByIdWithRolesAndAddresses(id);
        userRepository.deleteById(id);
        return id;
    }

    @Override
    public UserInfoResponse getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsernameWithAddresses(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userMapper.toUserInfoResponse(user);
    }

    @Override
    @Transactional
    public long updateMyInfo(UpdateRequest request) {
        SecurityContext context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsernameWithAddresses(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userMapper.updateMyInfo(user, request);

        user.getAddresses().forEach(user::saveAddress);

        return user.getId();
    }

    @Override
    public PageResponse<?> getAllUsers(int pageNo, int pageSize, String sortBy) {

        Sort sort = resolveSortBy(sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        List<Long> ids = userRepository.findAllIds(pageable);

        List<User> users = userRepository.findAllByIdsAndSort(ids, sort);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(ids.size())
                .items(users)
                .build();
    }

    @Override
    @Transactional
    public long addRoleToUser(String username, String roleName) {
        User user = getUserByUsernameWithRoles(username);
        Role role = getRoleByNameWithUsers(username);

        if (!user.getRoles().contains(role)) {
            user.saveRole(role);
        }

        return user.getId();
    }

    @Override
    @Transactional
    public long deleteRoleFromUser(String username, String roleName) {
        User user = getUserByUsernameWithRoles(username);
        Role role = getRoleByNameWithUsers(username);

        if (user.getRoles().contains(role) && role.getUsers().contains(user)) {
            role.getUsers().remove(user);
            user.getRoles().remove(role);
        }

        return user.getId();
    }

    @Override
    public PageResponseCriteria<?> sortAndCriteriaSearch(int offset, int pageSize, String sortBy, String... search) {
        return searchRepository.criteriaSearch(offset, pageSize, sortBy, search);
    }

    @Override
    public PageResponse<?> sortAndSpecificationSearch(int pageNo, int pageSize, String sortBy, String search) {

        UserSpecificationBuilder builder = new UserSpecificationBuilder();

        Pattern pattern = Pattern.compile("([,']?)(\\w+)(!:|!~|!=|>=|<=|[:~=><])(\\w+)");
        Matcher matcher = pattern.matcher(search);

        while (matcher.find()) {
            String andOrLogic = matcher.group(1);
            String key = matcher.group(2);
            String operator = matcher.group(3);
            String value = matcher.group(4);

            if (andOrLogic != null && (andOrLogic.equals(SearchOperator.AND_OPERATOR) || andOrLogic.equals(SearchOperator.OR_OPERATOR))) {
                builder.with(andOrLogic, key, operator, value, null, null);
            } else {
                builder.with(key, operator, value, null, null);
            }
        }

        Sort sort = resolveSortBy(sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Long> ids = searchRepository.findIdsBySpecification(builder.build(), pageable);

        List<User> users = userRepository.findAllByIdsAndSort(ids.toList(), sort);

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(ids.getTotalPages())
                .items(users)
                .build();
    }

    private User getUserByIdWithRolesAndAddresses(long id) {
        return userRepository.findByIdWithRolesAndAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private User getUserByUsernameWithRoles(String username) {
        return userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));
    }

    private Role getRoleByNameWithUsers(String roleName) {
        return roleRepository.findByNameWithUsers(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    private void assignRolesToUser(User user, Set<RoleRequest> roleRequests) {
        roleRequests.forEach(roleRequest -> {
            Role role = roleRepository.findByNameWithUsers(roleRequest.getName().name())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            user.saveRole(role);
        });
    }

    private Sort resolveSortBy(String sortBy) {
        // Default sort by ID in ascending order
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("^(\\w+)(:)(asc|desc)$");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnToSort = matcher.group(1);
                String sortDirection = matcher.group(3);
                if (sortDirection.equalsIgnoreCase("asc")) {
                    sort = Sort.by(Sort.Direction.ASC, columnToSort);
                }
                else if (sortDirection.equalsIgnoreCase("desc")) {
                    sort = Sort.by(Sort.Direction.DESC, columnToSort);
                }
            }
        }

        return sort;
    }
}
