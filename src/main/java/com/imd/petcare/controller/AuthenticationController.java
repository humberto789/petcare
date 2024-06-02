package com.imd.petcare.controller;

import com.imd.petcare.dto.ApiResponseDTO;
import com.imd.petcare.dto.AuthenticationRequestDTO;
import com.imd.petcare.dto.AuthenticationResponseDTO;
import com.imd.petcare.dto.RefreshTokenRequestDTO;
import com.imd.petcare.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling authentication-related endpoints.
 *
 * This controller provides endpoints for user authentication and token management.
 */
@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Handles authentication requests by validating user credentials and returning an authentication response.
     *
     * @param request the authentication request containing user credentials
     * @return a ResponseEntity containing an ApiResponseDTO with the authentication response
     *
     * This method accepts an AuthenticationRequestDTO, processes it using the authenticationService,
     * and returns a ResponseEntity with an ApiResponseDTO containing the authentication response.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponseDTO<AuthenticationResponseDTO>> authenticate(
            @RequestBody AuthenticationRequestDTO request) {
        return ResponseEntity.ok(
                new ApiResponseDTO<>(
                        true,
                        "Authentication completed successfully",
                        authenticationService.authenticate(request),
                        null
                ));
    }

    /**
     * Handles refresh token requests by validating the refresh token and returning a new access token.
     *
     * @param request the refresh token request containing the refresh token
     * @return a ResponseEntity containing an ApiResponseDTO with the new authentication response
     *
     * This method accepts a RefreshTokenRequestDTO, processes it using the authenticationService,
     * and returns a ResponseEntity with an ApiResponseDTO containing the new authentication response
     * after refreshing the access token.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseDTO<AuthenticationResponseDTO>> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        AuthenticationResponseDTO response = authenticationService.refreshAccessToken(request);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(
                        true,
                        "Token refreshed successfully",
                        response,
                        null
                ));
    }
}
