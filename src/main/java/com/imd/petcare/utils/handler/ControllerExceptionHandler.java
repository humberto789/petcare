package com.imd.petcare.utils.handler;

import com.imd.petcare.dto.ApiResponseDTO;
import com.imd.petcare.dto.ErrorDTO;
import com.imd.petcare.utils.exception.BusinessException;
import com.imd.petcare.utils.exception.ConversionException;
import com.imd.petcare.utils.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for controllers in the application.
 * Handles various types of exceptions and maps them to appropriate error
 * responses.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Handles BusinessException and maps it to a custom error response.
     *
     * @param exception The BusinessException instance.
     * @param request   The HttpServletRequest.
     * @return ResponseEntity containing the error response.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDTO<ErrorDTO>> businessException(BusinessException exception,
                                                                      HttpServletRequest request) {

        var err = new ErrorDTO(
                ZonedDateTime.now(),
                exception.getHttpStatusCode().value(),
                "Erro de regra de negócio",
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<ErrorDTO>(
                false,
                "Erro: " + exception.getMessage(),
                null,
                err));
    }

    /**
     * Handles ResourceNotFoundException and maps it to a custom error response.
     *
     * @param exception The ResourceNotFoundException instance.
     * @param request   The HttpServletRequest.
     * @return ResponseEntity containing the error response.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<ErrorDTO>> notFound(ResourceNotFoundException exception,
                                                             HttpServletRequest request) {

        var err = new ErrorDTO(
                ZonedDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<ErrorDTO>(
                false,
                "Erro: " + exception.getMessage(),
                null,
                err));
    }

    /**
     * Handles ConversionException and maps it to a custom error response.
     *
     * @param exception The ConversionException instance.
     * @param request   The HttpServletRequest.
     * @return ResponseEntity containing the error response.
     */
    @ExceptionHandler(ConversionException.class)
    public ResponseEntity<ApiResponseDTO<ErrorDTO>> conversionException(ConversionException exception,
                                                                        HttpServletRequest request) {

        var err = new ErrorDTO(
                ZonedDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um problema inesperado durante a conversão de dados.",
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<ErrorDTO>(
                false,
                "Erro: " + exception.getMessage(),
                null,
                err));

    }

    /**
     * Handles TransactionSystemException and maps it to a custom error response.
     *
     * @param ex      The TransactionSystemException instance.
     * @param request The HttpServletRequest.
     * @return ResponseEntity containing the error response.
     */
    @ExceptionHandler({ TransactionSystemException.class })
    protected ResponseEntity<ApiResponseDTO<ErrorDTO>> handlePersistenceException(Exception ex,
                                                                                  HttpServletRequest request) {
        logger.info(ex.getClass().getName());

        Throwable cause = ((TransactionSystemException) ex).getRootCause();
        if (cause instanceof ConstraintViolationException consEx) {
            final List<String> errors = new ArrayList<>();
            for (final ConstraintViolation<?> violation : consEx.getConstraintViolations()) {
                errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
            }

            final var err = new ErrorDTO(
                    ZonedDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Erro ao salvar dados.",
                    errors.toString(),
                    request.getRequestURI());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<ErrorDTO>(
                    false,
                    "Erro: " + ex.getMessage(),
                    null,
                    err));
        }
        return internalErrorException(ex, request);
    }

    /**
     * Handles AccessDeniedException and maps it to a custom error response.
     *
     * @param exception The AccessDeniedException instance.
     * @param request   The HttpServletRequest.
     * @return ResponseEntity containing the error response.
     */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO<ErrorDTO>> accessDeniedException(AccessDeniedException exception,
                                                                          HttpServletRequest request) {

        var err = new ErrorDTO(
                ZonedDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado",
                exception.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO<ErrorDTO>(
                false,
                "Erro: " + exception.getMessage(),
                null,
                err));
    }

    /**
     * Handles any other unexpected exception and maps it to a generic error
     * response.
     *
     * @param e       The unexpected Exception instance.
     * @param request The HttpServletRequest.
     * @return ResponseEntity containing the error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<ErrorDTO>> internalErrorException(Exception e, HttpServletRequest request) {

        var err = new ErrorDTO(
                ZonedDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um problema inesperado.",
                e.getMessage(),
                request.getRequestURI());

        logger.error("Ocorreu um problema inesperado. ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<ErrorDTO>(
                false,
                "Erro: " + e.getMessage(),
                null,
                err));
    }
}