package com.example.pigeon.web.exception;


import org.springframework.security.core.AuthenticationException;

public final class UnusualLocationException extends AuthenticationException {

    public UnusualLocationException(final String message) { super(message); }
}
