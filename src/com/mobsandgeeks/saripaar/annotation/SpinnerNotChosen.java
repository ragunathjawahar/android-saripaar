package com.mobsandgeeks.saripaar.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mhewedy on 7/8/13.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpinnerNotChosen {
    public int order();
    public String message()     default "choose one.";
    public int messageResId()   default 0;
    public int expectedPosition() default 0;
}
