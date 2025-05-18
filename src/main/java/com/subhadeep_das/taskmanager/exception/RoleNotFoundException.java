package com.subhadeep_das.taskmanager.exception;
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}