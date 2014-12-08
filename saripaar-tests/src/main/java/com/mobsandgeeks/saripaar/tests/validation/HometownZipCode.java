package com.mobsandgeeks.saripaar.tests.validation;

import com.mobsandgeeks.saripaar.annotation.ValidateUsing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ValidateUsing(HometownZipCodeRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HometownZipCode {
    public int order()          default -1;
    public int messageResId()   default -1;
    public String message()     default "We accept orders only in 635001.";
}
