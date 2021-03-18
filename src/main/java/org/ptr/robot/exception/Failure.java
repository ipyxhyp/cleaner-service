package org.ptr.robot.exception;

public class Failure extends Throwable {


    private final Integer code;

    public Failure(String message, Integer failureCode) {
        super(message);
        this.code = failureCode;
    }

}
