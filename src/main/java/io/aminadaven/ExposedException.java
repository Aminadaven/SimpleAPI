package io.aminadaven;

import jakarta.servlet.http.HttpServletResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExposedException {
    int value() default HttpServletResponse.SC_BAD_REQUEST;
}
