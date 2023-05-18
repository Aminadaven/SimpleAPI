package io.aminadaven;

import jakarta.servlet.http.HttpServletResponse;

@ExposedException(HttpServletResponse.SC_NOT_ACCEPTABLE)
public class WrongParamsException extends Exception {
    public WrongParamsException(String s) {
        super(s);
    }
}
