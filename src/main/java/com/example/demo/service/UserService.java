package com.example.demo.service;

import com.example.demo.dto.request.UpdateInfoRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.PageResponseCriteria;
import com.example.demo.dto.response.UserInfoResponse;
import com.example.demo.model.User;

public interface UserService {
    long addUser(UserRequest request);
    User getUser(Long id);
    long updateUser(Long id, UserRequest request);
    long deleteUser(Long id);
    UserInfoResponse getMyInfo();
    long updateMyInfo(UpdateInfoRequest request);
    PageResponse<?> getAllUsers(int pageNo, int pageSize, String sortBy);
    long addRoleToUser(String username, String roleName);
    long deleteRoleFromUser(String username, String roleName);
    PageResponseCriteria<?> sortAndCriteriaSearch(int offset, int pageSize, String sortBy, String... search);
    PageResponse<?> sortAndSpecificationSearch(int pageNo, int pageSize, String sortBy, String search);
}
