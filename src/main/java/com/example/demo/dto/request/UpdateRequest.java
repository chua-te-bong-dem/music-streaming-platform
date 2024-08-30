package com.example.demo.dto.request;

import com.example.demo.constant.GenderEnum;
import com.example.demo.validator.gender.GenderValid;
import com.example.demo.validator.phone.PhoneNumber;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest implements Serializable {

    @NotBlank(message = "Last name can not be blank!")
    @Pattern(regexp = "^[a-zA-Z]{1,20}$", message = "Last name can only be between 1 to 20 alphabet characters")
    private String lastName;

    @NotBlank(message = "First name can not be blank!")
    @Pattern(regexp = "^[a-zA-Z]{1,20}$", message = "First name can only be between 1 to 20 alphabet characters")
    private String firstName;

    @GenderValid(message = "Gender can only be MALE|FEMALE|OTHER")
    private GenderEnum genderEnum;

    @NotNull(message = "Date of birth can not be blank")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @PhoneNumber(message = "Phone number invalid!")
    private String phoneNumber;

    @Email(message = "Email invalid!")
    private String email;

    @NotEmpty(message = "Addresses can not be empty")
    private Set<AddressRequest> addresses;
}
