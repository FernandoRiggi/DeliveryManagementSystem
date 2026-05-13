package br.ifsp.demo.annotation;

import org.junit.jupiter.api.Tag;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@UnitTest
@Tag("TDD")
public @interface TDD {}