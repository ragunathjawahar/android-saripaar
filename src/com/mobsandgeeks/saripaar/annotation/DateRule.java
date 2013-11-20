/*
 * Copyright (C) 2012 Mobs and Geeks
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.mobsandgeeks.saripaar.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date rule annotation. Allows to specify a specific pattern type.
 * Additional options such as greater than (>), less than (<) and equals (==) are available.
 *
 * @author Schuller Tom <tom@schuller.lu>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRule {
    public int order();
    public String pattern();
    public String gt() default "";
    public String lt() default "";
    public String messageParse()     default "Incorrect date format.";
    public String messageGt()     default "Date to low.";
    public String messageLt()     default "Date to high.";
    public int messageParseResId()   default 0;
    public int messageGtResId()   default 0;
    public int messageLtResId()   default 0;
}
