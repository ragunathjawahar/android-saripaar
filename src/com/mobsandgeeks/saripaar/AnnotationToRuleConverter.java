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
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 * @version 0.1
 */
class AnnotationToRuleConverter {
    // Debug
    static final String TAG = AnnotationToRuleConverter.class.getSimpleName();
 
    // Constants
    static final String WARN_TEMPLATE = "%s is a %s. @%s can only be applied to %s and " +
            "its subclasses.";

    public static Rule<?> getRule(Field field, View view, Annotation annotation) {
        Class<?> annotationClass = annotation.getClass();

        if (Required.class.isAssignableFrom(annotationClass)) {
            return getRequiredRule(field, view, (Required) annotation);
        } else if (Checked.class.isAssignableFrom(annotationClass)) {
            return getCheckedRule(field, view, (Checked) annotation);
        } else if (TextRule.class.isAssignableFrom(annotationClass)) {
            return getTextRule(field, view, (TextRule) annotation);
        }

        return null;
    }

    private static Rule<TextView> getRequiredRule(Field field, View view, Required required) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEMPLATE,
                    field.getName(), field.getType().getSimpleName(),
                    Required.class.getSimpleName(), "TextViews"));
            return null;
        }

        String message = required.message();
        if (required.messageResId() != 0) {
            message = view.getContext().getString(required.messageResId());
        }

        return Rules.required(message, required.trim());
    }

    private static Rule<View> getTextRule(Field field, View view, TextRule textRule) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEMPLATE,
                    field.getName(), field.getType().getSimpleName(),
                    TextRule.class.getSimpleName(), "TextViews"));
            return null;
        }

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        String message = textRule.message();
        if (textRule.messageResId() != 0) {
            message = view.getContext().getString(textRule.messageResId());
        }
        if (!textRule.regex().equals(Rules.EMPTY_STRING)) {
            rules.add(Rules.regex(null, textRule.regex(), textRule.trim()));
        }
        if (textRule.minLength() > 0) {
            rules.add(Rules.minLength(null, textRule.minLength(), textRule.trim()));
        }
        if (textRule.maxLength() != Integer.MAX_VALUE) {
            rules.add(Rules.maxLength(null, textRule.maxLength(), textRule.trim()));
        }

        return Rules.and(message, (Rule<TextView>[]) rules.toArray());
    }

    private static Rule<Checkable> getCheckedRule(Field field, View view, Checked checked) {
        if (!Checkable.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEMPLATE,
                    field.getName(), field.getType().getSimpleName(),
                    Checked.class.getSimpleName(), "Checkables"));
            return null;
        }

        String message = checked.message();
        if (checked.messageResId() != 0) {
            message = view.getContext().getString(checked.messageResId());
        }

        return Rules.checked(message, checked.checked());
    }

}
