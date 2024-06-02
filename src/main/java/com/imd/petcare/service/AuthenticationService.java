package com.imd.petcare.service;

import com.imd.petcare.configuration.security.jwt.JwtService;
import com.imd.petcare.dto.AuthenticationRequestDTO;
import com.imd.petcare.dto.AuthenticationResponseDTO;
import com.imd.petcare.dto.RefreshTokenRequestDTO;
import com.imd.petcare.model.RefreshToken;
import com.imd.petcare.repository.RefreshTokenRepository;
import com.imd.petcare.repository.UserRepository;
import com.imd.petcare.utils.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling user authentication and token generation.
 *
 * This service class provides methods for authenticating users, generating access tokens,
 * and refreshing access tokens.
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Constructs a new AuthenticationService with the provided dependencies.
     *
     * @param userRepository          the repository for managing user data
     * @param jwtService              the service for generating JWT tokens
     * @param authenticationManager   the authentication manager for user authentication
     * @param userDetailsService     the service for loading user details
     * @param refreshTokenRepository the repository for managing refresh tokens
     */
    public AuthenticationService(UserRepository userRepository,
                                     JwtService jwtService,
                                     AuthenticationManager authenticationManager,
                                     UserDetailsService userDetailsService,
                                     RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Authenticates a user and generates an authentication response containing access and refresh tokens.
     *
     * @param request the authentication request containing user credentials
     * @return an AuthenticationResponseDTO containing the generated tokens
     * @throws BusinessException if the credentials are invalid
     */
    @Transactional
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.login(), request.password()));
        }catch (BadCredentialsException exception){
            throw new BusinessException("Credenciais inválidas!", HttpStatus.BAD_REQUEST);
        }

        var user = userRepository.findByLogin(request.login()).orElseThrow();
        refreshTokenRepository.updateIsUsedByUserId(user.getId());

        var response = jwtService.generateToken(user, user);

        refreshTokenRepository.save(new RefreshToken(response.refreshToken(), user));

        return response;
    }

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param request the refresh token request containing the refresh token
     * @return an AuthenticationResponseDTO containing the new access token
     * @throws BusinessException if the refresh token is invalid or expired
     */
    @Transactional
    public AuthenticationResponseDTO refreshAccessToken(RefreshTokenRequestDTO request) {
        try {
            var username = jwtService.extractUsername(request.refreshToken());
            var userDetails = userDetailsService.loadUserByUsername(username);
            var user = userRepository.findByLogin(username).orElseThrow();
            var refreshToken = refreshTokenRepository.findByToken(request.refreshToken());

            if (!jwtService.isTokenValid(request.refreshToken(), userDetails) || refreshToken.isEmpty() || refreshToken.get().isIsUsed()) {
                throw new BusinessException("Refresh token inválido ou expirado!", HttpStatus.FORBIDDEN);
            }

            refreshTokenRepository.updateIsUsedByUserId(user.getId());
            var response = jwtService.generateToken(userDetails, user);
            refreshTokenRepository.save(new RefreshToken(response.refreshToken(), user));

            return response;
        } catch (Exception e) {
            throw new BusinessException("Erro ao gerar novo token de acesso: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
