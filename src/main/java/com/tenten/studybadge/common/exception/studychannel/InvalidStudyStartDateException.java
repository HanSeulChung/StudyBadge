package com.tenten.studybadge.common.exception.studychannel;

import com.tenten.studybadge.common.exception.basic.AbstractException;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InvalidStudyStartDateException extends AbstractException {

    private static final String ERROR_CODE = "INVALID_STUDY_START_DATE";
    private static final String ERROR_MESSAGE = "스터디 시작 날짜는 오늘 날짜 이후로 설정해주세요.";

    @Override
    public HttpStatus getHttpStatus() {
        return BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public String getMessage() {
        return ERROR_MESSAGE;
    }

}
