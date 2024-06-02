package com.imd.petcare.configuration.security.jwt;

import com.imd.petcare.dto.AuthenticationResponseDTO;
import com.imd.petcare.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for JSON Web Token (JWT) operations.
 *
 * This service provides methods for generating, validating, and extracting information from JWTs.
 */
@Service
public class JwtService {

    @Value("${secret.key}")
    private String SECRET_KEY;

    private final int EXPIRATION_TIME = 1000 * 60 * 24;
    private final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a claim from the given JWT token using the provided claims resolver.
     *
     * @param token the JWT token
     * @param claimsResolver a function to resolve the claim from the claims
     * @param <T> the type of the claim
     * @return the claim extracted from the token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates an authentication response containing the JWT token and refresh token for the given user.
     *
     * @param userDetails the user details of the authenticated user
     * @param user the user entity
     * @return an AuthenticationResponseDTO containing the JWT token and refresh token
     */
    public AuthenticationResponseDTO generateToken(UserDetails userDetails, User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", String.valueOf(user.getId()));
        extraClaims.put("name", user.getPerson().getName());
        extraClaims.put("email", user.getEmail());
        extraClaims.put(
                "role",
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","))
        );

        var token = generateToken(extraClaims, userDetails);
        var refreshToken = generateRefreshToken(userDetails);

        return new AuthenticationResponseDTO(token, refreshToken);
    }

    /**
     * Generates a JWT token with the given extra claims and user details.
     *
     * @param extraClaims a map of extra claims to be included in the token
     * @param userDetails the user details of the authenticated user
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())

                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a refresh token for the given user details.
     *
     * @param userDetails the user details of the authenticated user
     * @return the generated refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the given JWT token against the provided user details.
     *
     * @param token the JWT token to validate
     * @param userDetails the user details of the authenticated user
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if the given JWT token is expired.
     *
     * @param token the JWT token to check
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the given JWT token.
     *
     * @param token the JWT token
     * @return the expiration date extracted from the token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from the given JWT token.
     *
     * @param token the JWT token
     * @return the claims extracted from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key used to sign the JWT tokens.
     *
     * @return the signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
