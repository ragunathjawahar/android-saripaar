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
package com.mobsandgeeks.saripaar;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Required;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 * @version 0.1
 */
class AnnotationToRuleFactory {
    // Debug
    static final String TAG = AnnotationToRuleFactory.class.getSimpleName();

    // Constants
    static final String WARN_TEMPLATE = "%s is a %s. @%s can be applied to %s and " +
            "its subclasses only.";

    public static Rule<?> getRule(Field field, View view, Annotation annotation) {
        if (Required.class.isAssignableFrom(annotation.getClass())) {
            return getRequiredRule(field, view, (Required) annotation);
        }

        return null;
    }

    private static Rule<TextView> getRequiredRule(Field field, View view, Required required) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEMPLATE,
                    field.getName(), field.getType().getSimpleName(), "TextViews",
                    Required.class.getSimpleName()));
            return null;
        }

        String message = required.message();
        if (required.messageResId() != 0) {
            message = view.getContext().getString(required.messageResId());
        }

        return Rules.required(message, required.trim());
    }

}
