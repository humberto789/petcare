package com.imd.petcare.mappers;

import com.imd.petcare.dto.UserDTO;
import com.imd.petcare.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between User entities and UserDTOs.
 *
 * This component provides methods for mapping User entities to UserDTOs and vice versa.
 */
@Component
public class UserDTOMapper implements DtoMapper<User, UserDTO> {

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param entity the User entity to be converted
     * @return a UserDTO representing the converted entity
     *
     * This method converts a User entity object to its corresponding Data Transfer Object (DTO),
     * mapping each attribute from the entity to the DTO. It also utilizes a PersonDTOMapper to convert
     * the associated Person entity to a PersonDTO.
     */
    @Override
    public UserDTO toDto(User entity) {
        return new UserDTO(
                entity.getId(),
                new PersonDTOMapper().toDto(entity.getPerson()),
                entity.getLogin(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getRole());
    }

    /**
     * Converts a UserDTO to a User entity.
     *
     * @param userDTO the UserDTO to be converted
     * @return a User entity representing the converted DTO
     *
     * This method converts a UserDTO object to its corresponding entity object,
     * mapping each attribute from the DTO to the entity. It also utilizes a PersonDTOMapper to convert
     * the associated PersonDTO to a Person entity.
     */
    @Override
    public User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.id());
        user.setLogin(userDTO.login());
        user.setPassword(userDTO.password());
        user.setEmail(userDTO.email());
        user.setRole(userDTO.role());
        user.setPerson(new PersonDTOMapper().toEntity(userDTO.person()));

        return user;
    }
}
