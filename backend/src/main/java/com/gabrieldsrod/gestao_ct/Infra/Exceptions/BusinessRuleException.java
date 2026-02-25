package com.gabrieldsrod.gestao_ct.Infra.Exceptions;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
