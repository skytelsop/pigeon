package com.example.pigeon.web.dto;

import com.example.pigeon.validation.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {

    private String oldPassword;

    private String token;

    @ValidPassword
    private String newPassword;
}
