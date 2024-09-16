package com.example.demo.service.impl;

import com.example.demo.constant.RoleName;
import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.request.UpdateInfoRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.PageResponseCriteria;
import com.example.demo.dto.response.UserDetailsResponse;
import com.example.demo.dto.response.UserInfoResponse;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.DataInUseException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.search.UserSearchRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.specification.SearchOperator;
import com.example.demo.repository.specification.UserSpecificationBuilder;
import com.example.demo.service.UserService;
import com.example.demo.utils.SortUtil;
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

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserSearchRepository userSearchRepository;

    @Override
    @Transactional
    public long addUser(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DataInUseException("Username is already in use");
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<RoleName> roleNames = request.getRoles().stream()
                .map(RoleRequest::getName)
                .collect(Collectors.toSet());

        Set<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new ResourceNotFoundException("One or more roles were not found");
        }

        roles.forEach(user::saveRole);

        userRepository.save(user);

        return user.getId();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findByIdWithAllFields(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public long updateUser(Long id, UserRequest request) {
        User user = userRepository.findByIdWithRolesAndAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String requestUsername = request.getUsername();

        if (!userRepository.existsByUsername(requestUsername))
            throw new ResourceNotFoundException("User not found with username: " + requestUsername);

        if (!requestUsername.equals(user.getUsername()))
            throw new InvalidDataException("Request username does not match with username with id: " + id);

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Delete old roles from user
        user.getRoles().forEach(role -> role.getUsers().remove(user));
        user.getRoles().clear();

        Set<RoleName> roleNames = request.getRoles().stream()
                .map(RoleRequest::getName)
                .collect(Collectors.toSet());

        Set<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new ResourceNotFoundException("One or more roles were not found");
        }

        roles.forEach(user::saveRole);

        return user.getId();
    }
    @Override
    public long deleteUser(Long id) {
        if (userRepository.existsById(id))
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
    public long updateMyInfo(UpdateInfoRequest request) {
        SecurityContext context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsernameWithAddresses(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userRepository.existsByEmail(request.getEmail()))
            throw new DataInUseException("Email is in used");

        userMapper.updateMyInfo(user, request);

        user.getAddresses().forEach(user::saveAddress);

        return user.getId();
    }

    @Override
    public PageResponse<?> getAllUsers(int pageNo, int pageSize, String sortBy) {

        Sort sort = SortUtil.resolveSortBy(sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Long> ids = userRepository.findAllIds(pageable);

        List<User> users = userRepository.findAllByIdsAndSort(ids.toList(), sort);

        List<UserDetailsResponse> result = users.stream()
                .map(userMapper::toUserDetailsResponse)
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(ids.getTotalPages())
                .items(result)
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
        return userSearchRepository.criteriaSearch(offset, pageSize, sortBy, search);
    }

    @Override
    public PageResponse<?> sortAndSpecificationSearch(int pageNo, int pageSize, String sortBy, String search) {

        UserSpecificationBuilder builder = new UserSpecificationBuilder();

        if (StringUtils.hasLength(search)) {
            Pattern pattern = Pattern.compile("([,']?)(\\w+)(!:|!~|!=|>=|<=|[:~=><])(\\w+)");
            Matcher matcher = pattern.matcher(search);

            while (matcher.find()) {
                String andOrLogic = matcher.group(1);
                String key = matcher.group(2);
                String operator = matcher.group(3);
                String value = matcher.group(4);

                if (andOrLogic != null && (andOrLogic.equals(SearchOperator.AND_OPERATOR) ||
                                           andOrLogic.equals(SearchOperator.OR_OPERATOR))) {
                    builder.with(andOrLogic, key, operator, value, null, null);
                } else {
                    builder.with(key, operator, value, null, null);
                }
            }
        }

        Sort sort = SortUtil.resolveSortBy(sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Long> ids = userSearchRepository.findIdsBySpecification(builder.build(), pageable);

        List<User> users = userRepository.findAllByIdsAndSort(ids.toList(), sort);

        List<UserDetailsResponse> result = users.stream()
                .map(userMapper::toUserDetailsResponse)
                .toList();

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(ids.getTotalPages())
                .items(result)
                .build();
    }

    private User getUserByUsernameWithRoles(String username) {
        return userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));
    }

    private Role getRoleByNameWithUsers(String roleName) {
        return roleRepository.findByNameWithUsers(RoleName.valueOf(roleName))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }
}
