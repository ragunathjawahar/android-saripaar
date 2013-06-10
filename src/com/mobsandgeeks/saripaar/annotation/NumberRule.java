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

import com.mobsandgeeks.saripaar.Rules;

/**
 * Number rule annotation. Allows a specific primitive type contained in {@link NumberType}.
 * Additional options such as greater than (>), less than (<) and equals (==) are available. 
 *
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberRule {
    public int order();
    public NumberType type();
    public double gt()          default Double.MAX_VALUE;
    public double lt()          default Double.MIN_VALUE;
    public double eq()          default Double.MAX_VALUE;
    public String message()     default Rules.EMPTY_STRING;
    public int messageResId()   default 0;

    public enum NumberType {
        INTEGER, LONG, FLOAT, DOUBLE
    }
}
