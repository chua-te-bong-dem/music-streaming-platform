package com.example.demo.dto.response;

import com.example.demo.constant.Gender;
import com.example.demo.model.Address;
import com.example.demo.model.ListeningHistory;
import com.example.demo.model.Playlist;
import com.example.demo.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
public class UserDetailsResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;
    private String phoneNumber;
    private String email;
    private String username;
    private String password;
    private Boolean active;
    private Set<Address> addresses;
    private Set<Role> roles;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
}
