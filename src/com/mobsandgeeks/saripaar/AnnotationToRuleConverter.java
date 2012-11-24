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

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.IpAddress;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Regex;
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
    static final String WARN_TEXT = "%s - @%s can only be applied to TextView and " +
            "its subclasses.";
    static final String WARN_CHECKABLE = "%s - @%s can only be applied to Checkable, " +
            "its implementations and subclasses.";

    public static Rule<?> getRule(Field field, View view, Annotation annotation) {
        Class<?> annotationClass = annotation.getClass();

        if (Required.class.isAssignableFrom(annotationClass)) {
            return getRequiredRule(field, view, (Required) annotation);
        } else if (Checked.class.isAssignableFrom(annotationClass)) {
            return getCheckedRule(field, view, (Checked) annotation);
        } else if (TextRule.class.isAssignableFrom(annotationClass)) {
            return getTextRule(field, view, (TextRule) annotation);
        } else if (Regex.class.isAssignableFrom(annotationClass)) {
            return getRegexRule(field, view, (Regex) annotation);
        } else if (NumberRule.class.isAssignableFrom(annotationClass)) {
            return getNumberRule(field, view, (NumberRule) annotation);
        } else if (Password.class.isAssignableFrom(annotationClass)) {
            return getPasswordRule(field, view, (Password) annotation);
        } else if (Email.class.isAssignableFrom(annotationClass)) {
            return getEmailRule(field, view, (Email) annotation);
        } else if (IpAddress.class.isAssignableFrom(annotationClass)) {
            return getIpAddressRule(field, view, (IpAddress) annotation);
        }

        return null;
    }

    public static Rule<?> getRule(Field field, View view, Annotation annotation, Object... params) {
        Class<?> annotationClass = annotation.getClass();

        if (ConfirmPassword.class.isAssignableFrom(annotationClass)) {
            TextView passwordTextView = (TextView) params[0];
            return getConfirmPasswordRule(field, view, (ConfirmPassword) annotation,
                    passwordTextView);
        }

        return (params == null || params.length == 0) ? getRule(field, view, annotation) : null;
    }

    private static Rule<TextView> getRequiredRule(Field field, View view, Required required) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), Required.class.getSimpleName()));
            return null;
        }

        int messageResId = required.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
            required.message();

        return Rules.required(message, required.trim());
    }

    private static Rule<View> getTextRule(Field field, View view, TextRule textRule) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), TextRule.class.getSimpleName()));
            return null;
        }

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = textRule.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
            textRule.message();

        if (textRule.minLength() > 0) {
            rules.add(Rules.minLength(null, textRule.minLength(), textRule.trim()));
        }
        if (textRule.maxLength() != Integer.MAX_VALUE) {
            rules.add(Rules.maxLength(null, textRule.maxLength(), textRule.trim()));
        }

        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);

        return Rules.and(message, ruleArray);
    }

    private static Rule<TextView> getRegexRule(Field field, View view, Regex regexRule) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), Regex.class.getSimpleName()));
            return null;
        }

        Context context = view.getContext();
        int messageResId = regexRule.messageResId();
        String message = messageResId != 0 ? context.getString(messageResId) : regexRule.message();

        int patternResId = regexRule.patternResId();
        String pattern = patternResId != 0 ? view.getContext().getString(patternResId) :
            regexRule.pattern();

        return Rules.regex(message, pattern, regexRule.trim());
    }

    private static Rule<View> getNumberRule(Field field, View view, NumberRule numberRule) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), NumberRule.class.getSimpleName()));
            return null;
        } else if (numberRule.type() == null) {
            throw new IllegalArgumentException(String.format("@%s.type() cannot be null.",
                    NumberRule.class.getSimpleName()));
        }

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = numberRule.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
            numberRule.message();

        switch (numberRule.type()) {
        case INTEGER: case LONG:
            Rules.regex(null, Rules.REGEX_INTEGER, true); break;
        case FLOAT: case DOUBLE:
            Rules.regex(null, Rules.REGEX_DECIMAL, true); break;
        }

        if (numberRule.lt() != Double.MIN_VALUE) {
            String ltNumber = String.valueOf(numberRule.lt());
            switch (numberRule.type()) {
            case INTEGER:   rules.add(Rules.lt(null, Integer.parseInt(ltNumber)));   break;
            case LONG:      rules.add(Rules.lt(null, Long.parseLong(ltNumber)));     break;
            case FLOAT:     rules.add(Rules.lt(null, Float.parseFloat(ltNumber)));   break;
            case DOUBLE:    rules.add(Rules.lt(null, Double.parseDouble(ltNumber))); break;
            }
        }
        if (numberRule.gt() != Double.MAX_VALUE) {
            String gtNumber = String.valueOf(numberRule.gt());
            switch (numberRule.type()) {
            case INTEGER:   rules.add(Rules.gt(null, Integer.parseInt(gtNumber)));   break;
            case LONG:      rules.add(Rules.gt(null, Long.parseLong(gtNumber)));     break;
            case FLOAT:     rules.add(Rules.gt(null, Float.parseFloat(gtNumber)));   break;
            case DOUBLE:    rules.add(Rules.gt(null, Double.parseDouble(gtNumber))); break;
            }
        }
        if (numberRule.eq() != Double.MAX_VALUE) {
            String gtNumber = String.valueOf(numberRule.gt());
            switch (numberRule.type()) {
            case INTEGER:   rules.add(Rules.eq(null, Integer.parseInt(gtNumber)));   break;
            case LONG:      rules.add(Rules.eq(null, Long.parseLong(gtNumber)));     break;
            case FLOAT:     rules.add(Rules.eq(null, Float.parseFloat(gtNumber)));   break;
            case DOUBLE:    rules.add(Rules.eq(null, Double.parseDouble(gtNumber))); break;
            }
        }

        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);

        return Rules.and(message, ruleArray);
    }

    private static Rule<TextView> getPasswordRule(Field field, View view, Password password) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), Password.class.getSimpleName()));
            return null;
        }

        int messageResId = password.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
            password.message();

        return Rules.required(message, false);
    }

    private static Rule<TextView> getConfirmPasswordRule(Field field, View view,
            ConfirmPassword confirmPassword, TextView passwordTextView) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(),
                    ConfirmPassword.class.getSimpleName()));
            return null;
        }

        int messageResId = confirmPassword.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
            confirmPassword.message();

        return Rules.eq(message, passwordTextView);
    }

    private static Rule<View> getEmailRule(Field field, View view, Email email) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), Regex.class.getSimpleName()));
            return null;
        }

        int messageResId = email.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
            email.message();

        return Rules.or(message, Rules.eq(null, Rules.EMPTY_STRING),
                Rules.regex(message, Rules.REGEX_EMAIL, true));
    }

    private static Rule<View> getIpAddressRule(Field field, View view, IpAddress ipAddress) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), IpAddress.class.getSimpleName()));
            return null;
        }

        int messageResId = ipAddress.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
            ipAddress.message();

        return Rules.or(message, Rules.eq(null, Rules.EMPTY_STRING),
                Rules.regex(message, Rules.REGEX_IP_ADDRESS, true));
    }

    private static Rule<Checkable> getCheckedRule(Field field, View view, Checked checked) {
        if (!Checkable.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_CHECKABLE, field.getName(),
                    Checked.class.getSimpleName()));
            return null;
        }

        int messageResId = checked.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
            checked.message();

        return Rules.checked(message, checked.checked());
    }

}
