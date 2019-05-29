package uu.toolbox.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UUSqlDatabase
{
    String name() default "";
    int version() default 1;
    Class[] models() default {};
}
