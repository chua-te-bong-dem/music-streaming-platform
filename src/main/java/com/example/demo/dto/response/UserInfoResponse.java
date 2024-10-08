package com.example.demo.dto.response;


import com.example.demo.model.Address;
import com.example.demo.constant.Gender;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse implements Serializable {
    private String lastName;
    private String firstName;
    private Gender gender;
    private Date dateOfBirth;
    private String phoneNumber;
    private String email;
    private Set<Address> addresses;
}

