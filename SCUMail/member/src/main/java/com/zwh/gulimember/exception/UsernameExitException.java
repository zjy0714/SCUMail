package com.zwh.gulimember.exception;

public class UsernameExitException extends RuntimeException{

    public UsernameExitException(){
        super("用户名已存在");
    }
}
