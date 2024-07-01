package com.tenten.studybadge.common.exception.basic;

import org.springframework.http.HttpStatus;

public abstract class AbstractException extends RuntimeException {

    abstract public HttpStatus getHttpStatus();
    abstract public String getErrorCode();
    abstract public String getMessage();

}
