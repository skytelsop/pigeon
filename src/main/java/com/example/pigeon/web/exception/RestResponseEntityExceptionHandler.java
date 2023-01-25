package com.example.pigeon.web.exception;

import com.example.pigeon.web.utils.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messages;

    public RestResponseEntityExceptionHandler() { super(); }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException e,
                                                         final HttpHeaders headers,
                                                         final HttpStatus status,
                                                         final WebRequest request) {

        logger.error("400 Status Code", e);
        final BindingResult result = e.getBindingResult();
        final GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());

        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException e,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {

        logger.error("400 Status Code", e);
        final BindingResult result = e.getBindingResult();
        final GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(),
                "Invalid" + result.getObjectName());

        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({InvalidOldPasswordException.class})
    public ResponseEntity<Object> handleInvalidOldPassword(final RuntimeException e, final WebRequest request) {

        logger.error("400 Status Code", e);
        final GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.invalidOldPassword",
                null,
                request.getLocale()), "InvalidOldPassword");

        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ReCaptchaInvalidException.class})
    public ResponseEntity<Object> handleReCaptchaInvalid(final RuntimeException e, final WebRequest request) {

        logger.error("400 Status Code", e);
        final GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.invalidReCaptcha", null, request.getLocale()), "InvalidReCaptcha");

        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(final RuntimeException e,
                                                     final WebRequest request) {

        logger.error("404 Status Code", e);
        final GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.userNotFound",
                null,
                request.getLocale()), "UserNotFound");

        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public ResponseEntity<Object> handleUserAlreadyExist(final RuntimeException e,
                                                         final WebRequest request) {

        logger.error("409 Status Code", e);
        final GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.regError",
                null, request.getLocale()),
                "UserAlreadyExist");

        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({MailAuthenticationException.class})
    public ResponseEntity<Object> handleMail(final RuntimeException e, final WebRequest request) {

        logger.error("500 Status Code", e);
        final GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.email.config.error", null, request.getLocale()), "MailError");

        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ ReCaptchaUnavailableException.class })
    public ResponseEntity<Object> handleReCaptchaUnavailable(final RuntimeException e, final WebRequest request) {

        logger.error("500 Status Code", e);
        final GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.unavailableReCaptcha", null, request.getLocale()), "InvalidReCaptcha");

        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleInternal(final RuntimeException e, final WebRequest request) {

        logger.error("500 Status Code", e);
        final GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.error", null, request.getLocale()), "InternalError");

        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
