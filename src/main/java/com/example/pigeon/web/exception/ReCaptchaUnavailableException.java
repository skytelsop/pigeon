package com.example.pigeon.web.exception;

public final class ReCaptchaUnavailableException extends RuntimeException {

    public ReCaptchaUnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
