package com.bombk1n.technicaltaskproductcatalog.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username: " + username + " already exists");
    }
}
