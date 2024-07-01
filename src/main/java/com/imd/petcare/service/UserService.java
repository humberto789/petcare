package com.imd.petcare.service;

import com.imd.petcare.dto.RoleDTO;
import com.imd.petcare.dto.UserDTO;
import com.imd.petcare.dto.UserDetailsDTO;
import com.imd.petcare.mappers.DtoMapper;
import com.imd.petcare.mappers.UserDTOMapper;
import com.imd.petcare.model.User;
import com.imd.petcare.model.enums.Role;
import com.imd.petcare.repository.GenericRepository;
import com.imd.petcare.repository.UserRepository;
import com.imd.petcare.utils.exception.BusinessException;
import com.imd.petcare.utils.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Service class for managing user entities, implementing GenericService for User entities and UserDTOs.
 *
 * This service class provides methods for managing user entities, including CRUD operations and validation.
 */
@Transactional
@Service
public class UserService implements GenericService<User, UserDTO> {
    private final UserRepository repository;
    private final UserDTOMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * Constructs a new UserService with the provided dependencies.
     *
     * @param repository       the repository for managing user data
     * @param mapper           the mapper for converting between User entities and UserDTOs
     * @param passwordEncoder the password encoder for encrypting passwords
     */
    public UserService(UserRepository repository, UserDTOMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userRepository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public GenericRepository<User> getRepository() {
        return this.repository;
    }

    @Override
    public DtoMapper<User, UserDTO> getDtoMapper() {
        return this.mapper;
    }

    /**
     * Retrieves all roles available in the system.
     *
     * @return a list of RoleDTO representing all available roles
     */
    public List<RoleDTO> getAllRoles() {
        return Arrays.stream(Role.values())
                .map(role -> new RoleDTO(role.name(), role.getDescription()))
                .toList();
    }

    /**
     * Updates a user with the specified ID using the provided UserDTO.
     *
     * @param id      the ID of the user to update
     * @param userDTO the UserDTO containing updated user information
     * @return the updated UserDTO
     * @throws BusinessException if the ID in the DTO does not match the provided ID, or if the user does not exist
     */
    @Override
    public UserDTO update(Long id, UserDTO userDTO) {
        if (id != userDTO.id()) {
            throw new BusinessException("O id não pode ser alterado!", HttpStatus.BAD_REQUEST);
        }
        User userDb = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id not found: " + id));

        User updatedEntity = getDtoMapper().toEntity(userDTO);

        updatedEntity.setId(id);
        updatedEntity.getPerson().setId(userDb.getPerson().getId());
        updatedEntity.setPassword(userDb.getPassword());
        updatedEntity.setRole(userDTO.role());

        checkIdentifierBeforeUpdate(userDb, updatedEntity);
        checkLoginBeforeUpdate(userDb.getLogin(), updatedEntity.getLogin());
        checkEmailBeforeUpdate(userDb.getEmail(), updatedEntity.getEmail());

        return mapper.toDto(userRepository.save(updatedEntity));
    }

    /**
     * Checks if the login needs to be updated and validates it.
     *
     * @param loginUserDb      the login from the database
     * @param loginUpdatedUser the updated login
     * @throws BusinessException if the updated login already exists in the database
     */
    public void checkLoginBeforeUpdate(String loginUserDb, String loginUpdatedUser) {
        if (!Objects.equals(loginUserDb, loginUpdatedUser)) {
            validateLogin(loginUpdatedUser);
        }
    }

    /**
     * Checks if the email needs to be updated and validates it.
     *
     * @param emailUserDb      the email from the database
     * @param emailUpdatedUser the updated email
     * @throws BusinessException if the updated email already exists in the database
     */
    public void checkEmailBeforeUpdate(String emailUserDb, String emailUpdatedUser) {
        if (!Objects.equals(emailUserDb, emailUpdatedUser)) {
            validateEmail(emailUpdatedUser);
        }
    }

    /**
     * Checks if the identifier needs to be updated and validates it.
     *
     * @param userDb      the user entity from the database
     * @param updatedUser the updated user entity
     * @throws BusinessException if the updated identifier already exists in the database
     */
    public void checkIdentifierBeforeUpdate(User userDb, User updatedUser) {

        var changeIdentifier = !Objects.equals(
                userDb.getPerson().getIdentifier(),
                updatedUser.getPerson().getIdentifier());

        if (changeIdentifier) {
            validateIdentifier(updatedUser.getPerson().getIdentifier());
        }
    }

    /**
     * Validates the uniqueness of the login.
     *
     * @param login the login to validate
     * @throws BusinessException if the login already exists in the database
     */
    public void validateLogin(String login) {
        if (repository.existsByLogin(login)) {
            throw new BusinessException(
                    "Login inválido: " + login + ". Já existe um usuário cadastrado com esse identificador",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Validates the uniqueness of the email.
     *
     * @param email the email to validate
     * @throws BusinessException if the email already exists in the database
     */
    public void validateEmail(String email) {
        if (repository.existsPersonByEmail(email)) {
            throw new BusinessException("Email inválido: " + email + ". Já existe um usuário cadastrado com esse email",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Validates the uniqueness of the identifier.
     *
     * @param identifier the identifier to validate
     * @throws BusinessException if the identifier already exists in the database
     */
    public void validateIdentifier(String identifier) {
        if (repository.existsPersonByIdentifier(identifier)) {
            throw new BusinessException(
                    String.format("%s inválido: %s. Já existe um usuário cadastrado com esse identificador", identifier),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Validates user data before saving.
     *
     * @param entity the user entity to validate
     * @throws BusinessException if the user data is invalid
     */
    @Override
    public void validateBeforeSave(User entity) {
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        validateIdentifier(entity.getPerson().getIdentifier());
        validateLogin(entity.getLogin());
        validateEmail(entity.getEmail());
    }

    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email to check.
     * @return True if a user with the specified email exists, otherwise false.
     * @throws BusinessException if the email is null, empty, or invalid.
     */
    public boolean existsByEmail(String email){
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("Email is invalid.", HttpStatus.BAD_REQUEST);
        }
        return repository.existsPersonByEmail(email);
    }

    /**
     * Retrieves user details by ID.
     *
     * @param id the ID of the user to retrieve
     * @return the UserDetailsDTO containing user details
     * @throws ResourceNotFoundException if the user with the specified ID is not found
     */
    public UserDetailsDTO findByUserId(Long id){
        User user = repository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Usuário não encontrado: " + id));

        return new UserDetailsDTO(
                user.getId(),
                user.getPerson().getName(),
                user.getPerson().getIdentifier(),
                user.getEmail(),
                user.getPerson().getPhoneNumber(),
                user.getPerson().getBirthDate(),
                user.getRole(),
                user.getLogin()
        );
    }

    /**
     * Retrieves a user by login.
     *
     * @param login the login of the user to retrieve
     * @return the UserDTO representing the retrieved user
     * @throws ResourceNotFoundException if the user with the specified login is not found
     */
    public UserDTO findByLogin(String login) {
        User user = repository.findByLogin(login).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + login));

        return mapper.toDto(user);
    }
}
