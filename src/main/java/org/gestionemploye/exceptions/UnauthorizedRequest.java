package org.gestionemploye.exceptions;

public class UnauthorizedRequest extends RuntimeException {
    public UnauthorizedRequest(String message){super(message);}
}

