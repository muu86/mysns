package com.mj.mysns.common.exception;

public class DuplicatedUsernameException extends RuntimeException {

    public DuplicatedUsernameException() {
        super("사용 중인 이름입니다.");
    }

    public DuplicatedUsernameException(String message) {
        super(message);
    }
}
