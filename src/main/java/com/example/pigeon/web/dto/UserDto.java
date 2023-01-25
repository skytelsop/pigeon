package com.example.pigeon.web.dto;

import com.example.pigeon.model.CompanyType;
import com.example.pigeon.validation.PasswordMatches;
import com.example.pigeon.validation.ValidEmail;
import com.example.pigeon.validation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@PasswordMatches
@Data
public class UserDto {

    @NotNull
    @Size(min = 1, message = "{Size.userDto.firstName}")
    private String firstName;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.lastName}")
    private String lastName;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.companyName}")
    private String companyName;

    // TODO :
    /**
    @NotNull
    private CompanyType companyType;
     **/

    @ValidEmail
    @NotNull
    @Size(min = 1, message = "{Size.userDto.email}")
    private String email;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.phoneNumber}")
    private String phoneNumber;

    @ValidPassword
    private String password;

    @NotNull
    @Size(min = 1)
    private String matchingPassword;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.referenceName}")
    private String referenceName;

    @NotNull
    @Size(min = 1)
    private String postalCode;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.streetAddress}")
    private String streetAddress;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.state}")
    private String state;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.city}")
    private String city;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.country}")
    private String country;

    private boolean isUsing2FA;

    private Integer role;
}
