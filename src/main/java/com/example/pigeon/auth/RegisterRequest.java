package com.example.pigeon.auth;

import com.example.pigeon.entity.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
public class RegisterRequest {

    @NotNull
    @NotEmpty
    private String firstName;
    @NotNull
    @NotEmpty
    private String lastName;
    @NotNull
    @NotEmpty
    private String companyName;
    @NotNull
    @NotEmpty
    private CompanyType companyType;
    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;
    @NotNull
    @NotEmpty
    private String phoneNumber;
    @NotNull
    @NotEmpty
    private String matchingPassword;
    @NotNull
    @NotEmpty
    private String password;
    @NotNull
    @NotEmpty
    private String referenceName;
    @NotNull
    @NotEmpty
    private String postalCode;
    @NotNull
    @NotEmpty
    private String streetAddress;
    @NotNull
    @NotEmpty
    private String state;
    @NotNull
    @NotEmpty
    private String city;
    @NotNull
    @NotEmpty
    private String country;
}
