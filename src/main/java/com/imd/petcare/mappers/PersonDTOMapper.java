package com.imd.petcare.mappers;

import com.imd.petcare.dto.PersonDTO;
import com.imd.petcare.model.Person;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Person entities and PersonDTOs.
 *
 * This component provides methods for mapping Person entities to PersonDTOs and vice versa.
 */
@Component
public class PersonDTOMapper implements DtoMapper<Person, PersonDTO> {

    /**
     * Converts a Person entity to a PersonDTO.
     *
     * @param entity the Person entity to be converted
     * @return a PersonDTO representing the converted entity
     *
     * This method converts a Person entity object to its corresponding Data Transfer Object (DTO),
     * mapping each attribute from the entity to the DTO.
     */
    @Override
    public PersonDTO toDto(Person entity) {
        return new PersonDTO(
                entity.getId(),
                entity.getName(),
                entity.getIdentifier(),
                entity.getPhoneNumber(),
                entity.getBirthDate());
    }

    /**
     * Converts a PersonDTO to a Person entity.
     *
     * @param personDTO the PersonDTO to be converted
     * @return a Person entity representing the converted DTO
     *
     * This method converts a PersonDTO object to its corresponding entity object,
     * mapping each attribute from the DTO to the entity.
     */
    @Override
    public Person toEntity(PersonDTO personDTO) {

        Person entity = new Person();
        entity.setId(personDTO.id());
        entity.setName(personDTO.name());
        entity.setBirthDate(personDTO.birthDate());
        entity.setIdentifier(personDTO.identifier());
        entity.setPhoneNumber(personDTO.phoneNumber());

        return entity;
    }
}