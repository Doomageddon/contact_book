package com.doomageddon.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateContactDto {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private Long phone;
}
