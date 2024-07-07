package com.imd.petcare.service;

import com.imd.petcare.dto.PersonDTO;
import com.imd.petcare.dto.RoleDTO;
import com.imd.petcare.dto.UserDTO;
import com.imd.petcare.dto.UserDetailsDTO;
import com.imd.petcare.mappers.UserDTOMapper;
import com.imd.petcare.model.Person;
import com.imd.petcare.model.User;
import com.imd.petcare.model.enums.Role;
import com.imd.petcare.repository.GenericRepository;
import com.imd.petcare.repository.UserRepository;
import com.imd.petcare.utils.exception.BusinessException;
import com.imd.petcare.utils.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Optional;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDTOMapper userDTOMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = spy(new UserService(userRepository,userDTOMapper, passwordEncoder));
    }

    @Test
    public void testGetAllRoles() {
        List<RoleDTO> roles = userService.getAllRoles();
        assertEquals(Role.values().length, roles.size());
    }

    @Test
    public void testUpdate_UserNotFound() {
        Long id = 1L;
        UserDTO userDTO = new UserDTO(
                id,
                new PersonDTO(1L, "name", "71111111111", "84987056926", LocalDate.now()),
                "login",
                "12345",
                "email@example.com",
                Role.USER);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(id, userDTO));
    }

    @Test
    public void testUpdate_UserDTOIdMismatch() {
        Long id = 1L;

        UserDTO userDTO = new UserDTO(
                id,
                new PersonDTO(1L, "name", "71111111111", "84987056926", LocalDate.now()),
                "login",
                "12345",
                "email@example.com",
                Role.USER);

        UserDTOMapper mapper = new UserDTOMapper();

        User user = mapper.toEntity(userDTO);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        assertThrows(NullPointerException.class, () -> userService.update(id, userDTO));
    }

    @Test
    public void testExistsByEmail_True() {
        String email = "email@example.com";
        when(userRepository.existsPersonByEmail(email)).thenReturn(true);

        boolean exists = userService.existsByEmail(email);
        assertTrue(exists);
    }

    @Test
    public void testExistsByEmail_False() {
        String email = "email@example.com";
        when(userRepository.existsPersonByEmail(email)).thenReturn(false);

        boolean exists = userService.existsByEmail(email);
        assertFalse(exists);
    }

    @Test
    public void testExistsByEmail_InvalidEmail() {
        String email = "";

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.existsByEmail(email));
        assertEquals("Email is invalid.", exception.getMessage());
    }

    @Test
    public void testFindByUserId_UserNotFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByUserId(id));
    }

    @Test
    public void testFindByUserId_UserFound() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setPerson(new Person());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDetailsDTO userDetailsDTO = userService.findByUserId(id);
        assertNotNull(userDetailsDTO);
        assertEquals(id, userDetailsDTO.id());
    }

    @Test
    public void testFindByLogin_UserNotFound() {
        String login = "login";
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByLogin(login));
    }

    @Test
    public void testFindByLogin_UserFound() {
        String login = "login";
        User user = new User();
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        UserDTO userDTO =  new UserDTO(
                1L,
                new PersonDTO(1L, "name", "71111111111", "84987056926", LocalDate.now()),
                "login",
                "12345",
                "email@example.com",
                Role.USER);

        when(userDTOMapper.toDto(user)).thenReturn(userDTO);

        UserDTO result = userService.findByLogin(login);
        assertNotNull(result);
    }

    @Test
    public void testValidateBeforeSave() {
        User user = new User();
        Person person = new Person();
        person.setIdentifier("12345");
        user.setPerson(person);
        user.setLogin("login");
        user.setEmail("email@example.com");
        user.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.existsPersonByIdentifier(anyString())).thenReturn(false);
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsPersonByEmail(anyString())).thenReturn(false);

        userService.validateBeforeSave(user);

        verify(passwordEncoder).encode("password");
        verify(userRepository).existsPersonByIdentifier("12345");
        verify(userRepository).existsByLogin("login");
        verify(userRepository).existsPersonByEmail("email@example.com");
    }

    @Test
    void checkLoginBeforeUpdate_ShouldValidateLogin_WhenLoginIsUpdated() {
        String loginUserDb = "oldLogin";
        String loginUpdatedUser = "newLogin";

        doNothing().when(userService).validateLogin(loginUpdatedUser);

        userService.checkLoginBeforeUpdate(loginUserDb, loginUpdatedUser);

        verify(userService).validateLogin(loginUpdatedUser);
    }

    @Test
    void checkEmailBeforeUpdate_ShouldValidateEmail_WhenEmailIsUpdated() {
        String emailUserDb = "old@example.com";
        String emailUpdatedUser = "new@example.com";

        doNothing().when(userService).validateEmail(emailUpdatedUser);

        userService.checkEmailBeforeUpdate(emailUserDb, emailUpdatedUser);

        verify(userService).validateEmail(emailUpdatedUser);
    }

    @Test
    void checkIdentifierBeforeUpdate_ShouldValidateIdentifier_WhenIdentifierIsUpdated() {
        User userDb = new User();
        User updatedUser = new User();
        Person personDb = new Person();
        Person personUpdated = new Person();

        personDb.setIdentifier("oldIdentifier");
        personUpdated.setIdentifier("newIdentifier");
        userDb.setPerson(personDb);
        updatedUser.setPerson(personUpdated);

        doNothing().when(userService).validateIdentifier(personUpdated.getIdentifier());

        userService.checkIdentifierBeforeUpdate(userDb, updatedUser);

        verify(userService).validateIdentifier(personUpdated.getIdentifier());
    }

    @Test
    void validateLogin_ShouldThrowException_WhenLoginExists() {
        String login = "existingLogin";

        when(userRepository.existsByLogin(login)).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.validateLogin(login));
    }

    @Test
    void validateEmail_ShouldThrowException_WhenEmailExists() {
        String email = "existing@example.com";

        when(userRepository.existsPersonByEmail(email)).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.validateEmail(email));
    }

    @Test
    void validateIdentifier_ShouldThrowException_WhenIdentifierExists() {
        String identifier = "existingIdentifier";

        when(userRepository.existsPersonByIdentifier(identifier)).thenReturn(true);

        assertThrows(MissingFormatArgumentException.class, () -> userService.validateIdentifier(identifier));
    }

    @Test
    void validateBeforeSave_ShouldEncodePasswordAndValidateUser() {
        User entity = new User();
        Person person = new Person();
        person.setIdentifier("identifier");
        entity.setPerson(person);
        entity.setLogin("login");
        entity.setEmail("email@example.com");
        entity.setPassword("encodedPassword");

        when(passwordEncoder.encode(entity.getPassword())).thenReturn("encodedPassword");
        doNothing().when(userService).validateIdentifier(person.getIdentifier());
        doNothing().when(userService).validateLogin(entity.getLogin());
        doNothing().when(userService).validateEmail(entity.getEmail());

        userService.validateBeforeSave(entity);

        verify(passwordEncoder).encode(entity.getPassword());
        verify(userService).validateIdentifier(person.getIdentifier());
        verify(userService).validateLogin(entity.getLogin());
        verify(userService).validateEmail(entity.getEmail());
    }

    @Test
    void update_ShouldUpdateUser_WhenValidInput() {
        Long userId = 1L;
        UserDTO userDTO =  new UserDTO(
                1L,
                new PersonDTO(1L, "name", "71111111111", "84987056926", LocalDate.now()),
                "newLogin",
                "12345",
                "newEmail",
                Role.USER);
        User userDb = new User();
        userDb.setId(userId);
        userDb.setLogin("oldLogin");
        userDb.setEmail("oldEmail");
        userDb.setPassword("oldPassword");
        Person personDb = new Person();
        personDb.setId(1L);
        personDb.setIdentifier("oldIdentifier");
        userDb.setPerson(personDb);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setLogin("newLogin");
        updatedUser.setEmail("newEmail");
        updatedUser.setRole(Role.USER);
        updatedUser.setPerson(new Person());
        updatedUser.getPerson().setId(1L);
        updatedUser.getPerson().setIdentifier("identifier");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userDb));
        when(userDTOMapper.toEntity(userDTO)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userDTOMapper.toDto(updatedUser)).thenReturn(userDTO);

        UserDTO result = userService.update(userId, userDTO);

        assertEquals(userDTO, result);
        verify(userRepository).findById(userId);
        verify(userDTOMapper).toEntity(userDTO);
        verify(userRepository).save(updatedUser);
        verify(userDTOMapper).toDto(updatedUser);
    }

    @Test
    void update_ShouldThrowBusinessException_WhenIdDoesNotMatch() {
        Long userId = 1L;
        UserDTO userDTO =  new UserDTO(
                2L,
                new PersonDTO(1L, "name", "71111111111", "84987056926", LocalDate.now()),
                "newLogin",
                "12345",
                "newEmail",
                Role.USER);
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.update(userId, userDTO));

        assertEquals("O id não pode ser alterado!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        Long userId = 1L;
        UserDTO userDTO =  new UserDTO(
                userId,
                new PersonDTO(1L, "name", "71111111111", "84987056926", LocalDate.now()),
                "newLogin",
                "12345",
                "newEmail",
                Role.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.update(userId, userDTO));

        assertEquals("Id not found: " + userId, exception.getMessage());
    }

    @Test
    void getRepository_ShouldReturnUserRepository() {
        GenericRepository<User> repository = userService.getRepository();
        assertSame(userRepository, repository);
    }

    @Test
    public void testGetAllRolesNotNull() {
        List<RoleDTO> roles = userService.getAllRoles();
        assertEquals(Role.values().length, roles.size());
        for (RoleDTO role : roles) {
            assertNotNull(role);
            assertNotNull(role.name());
            assertNotNull(role.description());
        }
    }

    @Test
    void testUpdate_CoversAllPitestPoints() {
        Long id = 1L;
        UserDTO userDTO = new UserDTO(
                id,
                new PersonDTO(1L, "name", "71111111111", "84987056926", LocalDate.now()),
                "newLogin",
                "12345",
                "newEmail@example.com",
                Role.ADMIN);

        User userDb = new User();
        userDb.setId(id);
        Person personDb = new Person();
        personDb.setId(1L);
        userDb.setPerson(personDb);
        userDb.setPassword("oldPassword");
        userDb.setLogin("oldLogin");
        userDb.setEmail("oldEmail@example.com");

        User updatedUser = spy(new User());
        Person updatedPerson = spy(new Person());
        updatedUser.setPerson(updatedPerson);

        when(userRepository.findById(id)).thenReturn(Optional.of(userDb));
        when(userDTOMapper.toEntity(userDTO)).thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userDTOMapper.toDto(any(User.class))).thenReturn(userDTO);

        doNothing().when(userService).checkIdentifierBeforeUpdate(any(User.class), any(User.class));
        doNothing().when(userService).checkLoginBeforeUpdate(anyString(), anyString());
        doNothing().when(userService).checkEmailBeforeUpdate(anyString(), anyString());

        UserDTO result = userService.update(id, userDTO);

        assertEquals(userDTO, result);

        verify(userRepository).findById(id);
        verify(userDTOMapper).toEntity(userDTO);
        verify(updatedUser).setId(id);
        verify(updatedUser).getPerson();
        verify(updatedPerson).setId(userDb.getPerson().getId());
        verify(updatedUser).setPassword(userDb.getPassword());
        verify(updatedUser).setRole(userDTO.role());
        verify(userService).checkIdentifierBeforeUpdate(userDb, updatedUser);
        verify(userService).checkLoginBeforeUpdate(userDb.getLogin(), updatedUser.getLogin());
        verify(userService).checkEmailBeforeUpdate(userDb.getEmail(), updatedUser.getEmail());
        verify(userRepository).save(updatedUser);
        verify(userDTOMapper).toDto(updatedUser);
    }

    @Test
    void testValidateBeforeSave_CoversAllPitestPoints() {
        // Arrange
        User entity = spy(new User());
        Person person = new Person();
        person.setIdentifier("12345");
        entity.setPerson(person);
        entity.setLogin("login");
        entity.setEmail("email@example.com");
        entity.setPassword("password");

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("password")).thenReturn(encodedPassword);

        userService.validateBeforeSave(entity);

        verify(entity).setPassword(encodedPassword);
        verify(userService).validateIdentifier(person.getIdentifier());
        verify(userService).validateLogin(entity.getLogin());
        verify(userService).validateEmail(entity.getEmail());
    }
}