package com.doomageddon.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {

    @NotBlank(message = "Name may not be blank")
    private String name;

    @NotBlank(message = "Password may not be blank")
    private String password;
}
