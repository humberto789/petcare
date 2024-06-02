package com.imd.petcare.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.imd.petcare.model.enums.Role;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(
        long id,
        PersonDTO person,
        String login,
        String password,
        String email,
        Role role

) implements EntityDTO {

    @Override
    public UserDTO toResponse() {
        return new UserDTO(this.id(), this.person(), this.login(), null, this.email, this.role());
    }
}