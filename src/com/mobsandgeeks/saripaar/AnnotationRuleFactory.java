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

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.IpAddress;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.Optional;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Regex;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.Select;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Class contains {@code static} methods that return appropriate {@link Rule}s for Saripaar
 * annotations.
 *
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
class AnnotationRuleFactory {
    // Debug
    static final String TAG = "AnnotationToRuleConverter";

    // Constants
    static final String WARN_TEXT = "%s - @%s can only be applied to TextView and " +
            "its subclasses.";
    static final String WARN_CHECKABLE = "%s - @%s can only be applied to Checkable, " +
            "its implementations and subclasses.";

    static final String WARN_SPINNER = "%s - @%s can only be applied to Spinner, " +
            "its implementations and subclasses.";

    public static Rule<?> getRule(Field field, View view, Annotation annotation) {
        Class<?> annotationType = annotation.annotationType();

        if (Checked.class.equals(annotationType)) {
            return getCheckedRule(field, view, (Checked) annotation);
        } else if (Required.class.equals(annotationType)) {
            return getRequiredRule(field, view, (Required) annotation);
        } else if (Optional.class.equals(annotationType)) {
            return getOptionalRule(field, view, (Optional) annotation);
        } else if (TextRule.class.equals(annotationType)) {
            return getTextRule(field, view, (TextRule) annotation);
        } else if (Regex.class.equals(annotationType)) {
            return getRegexRule(field, view, (Regex) annotation);
        } else if (NumberRule.class.equals(annotationType)) {
            return getNumberRule(field, view, (NumberRule) annotation);
        } else if (Password.class.equals(annotationType)) {
            return getPasswordRule(field, view, (Password) annotation);
        } else if (Email.class.equals(annotationType)) {
            return getEmailRule(field, view, (Email) annotation);
        } else if (IpAddress.class.equals(annotationType)) {
            return getIpAddressRule(field, view, (IpAddress) annotation);
        } else if (Select.class.equals(annotationType)) {
            return getSelectRule(field, view, (Select) annotation);
        }

        return null;
    }

    private static Rule<Spinner> getSelectRule(Field field, View view, Select select) {
        if (!Spinner.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_SPINNER, field.getName(),
                    Spinner.class.getSimpleName()));
            return null;
        }

        int messageResId = select.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
                select.message();

        int unexpectedSelection = select.defaultSelection();

        return Rules.spinnerNotEq(message, unexpectedSelection);
    }

    public static Rule<?> getRule(Field field, View view, Annotation annotation, Object... params) {
        Class<?> annotationType = annotation.annotationType();

        if (ConfirmPassword.class.equals(annotationType)) {
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
        String defaultMessage = Validator.getMessages().getRequireRuleMessage();
        if (TextUtils.isEmpty(message) && defaultMessage != null) {
            message = defaultMessage;
        }
        return Rules.required(message, required.trim());
    }

    private static Rule<?> getOptionalRule(Field field, View view, Optional annotation) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), Optional.class.getSimpleName()));
            return null;
        }
        return Rules.optional();
    }

    private static Rule<View> getTextRule(Field field, View view, TextRule textRule) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), TextRule.class.getSimpleName()));
            return null;
        }
        Validator.getMessages().getTextRuleMessage();
        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = textRule.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) :
                textRule.message();
        int minLength = textRule.minLength();
        int maxLength = textRule.maxLength();

        try {
            MinMaxProvider minMaxProvider = (MinMaxProvider) textRule.minMaxProvider().newInstance();
            minLength = minMaxProvider.getMin();
            maxLength = minMaxProvider.getMax();
            message = minMaxProvider.errorMessage();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        if (minLength > 0) {
            rules.add(Rules.minLength(null, minLength, textRule.trim()));
        }
        if (maxLength < Integer.MAX_VALUE) {
            rules.add(Rules.maxLength(null, maxLength, textRule.trim()));
        }

        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);

        String defaultMessage = Validator.getMessages().getTextRuleMessage();
        if (TextUtils.isEmpty(message) && defaultMessage != null) {
            message = defaultMessage;
        }
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
        String pattern = null;
        int patternResId = regexRule.patternResId();
        pattern = patternResId != 0 ? view.getContext().getString(patternResId) :
                regexRule.pattern();
        try {
            PatternProvider patternProvider = (PatternProvider) regexRule.patternProvider().newInstance();
            pattern = patternProvider.getPattern();
            message = patternProvider.errorMessage();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        String defaultMessage = Validator.getMessages().getRegExRuleMessage();
        if (TextUtils.isEmpty(message) && defaultMessage != null) {
            message = defaultMessage;
        }
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
            case INTEGER:
            case LONG:
                Rules.regex(null, Rules.REGEX_INTEGER, true);
                break;
            case FLOAT:
            case DOUBLE:
                Rules.regex(null, Rules.REGEX_DECIMAL, true);
                break;
        }

        if (numberRule.lt() != Double.MIN_VALUE) {
            String ltNumber = String.valueOf(numberRule.lt());
            double number = Double.parseDouble(ltNumber);
            switch (numberRule.type()) {
                case INTEGER:
                    rules.add(Rules.lt(null, ((int) number)));
                    break;
                case LONG:
                    rules.add(Rules.lt(null, ((long) number)));
                    break;
                case FLOAT:
                    rules.add(Rules.lt(null, Float.parseFloat(ltNumber)));
                    break;
                case DOUBLE:
                    rules.add(Rules.lt(null, Double.parseDouble(ltNumber)));
                    break;
            }
        }
        if (numberRule.gt() != Double.MAX_VALUE) {
            String gtNumber = String.valueOf(numberRule.gt());
            double number = Double.parseDouble(gtNumber);
            switch (numberRule.type()) {
                case INTEGER:
                    rules.add(Rules.gt(null, ((int) number)));
                    break;
                case LONG:
                    rules.add(Rules.gt(null, ((long) number)));
                    break;
                case FLOAT:
                    rules.add(Rules.gt(null, Float.parseFloat(gtNumber)));
                    break;
                case DOUBLE:
                    rules.add(Rules.gt(null, Double.parseDouble(gtNumber)));
                    break;
            }
        }
        if (numberRule.eq() != Double.MAX_VALUE) {
            String eqNumber = String.valueOf(numberRule.eq());
            double number = Double.parseDouble(eqNumber);
            switch (numberRule.type()) {
                case INTEGER:
                    rules.add(Rules.eq(null, ((int) number)));
                    break;
                case LONG:
                    rules.add(Rules.eq(null, ((long) number)));
                    break;
                case FLOAT:
                    rules.add(Rules.eq(null, Float.parseFloat(eqNumber)));
                    break;
                case DOUBLE:
                    rules.add(Rules.eq(null, Double.parseDouble(eqNumber)));
                    break;
            }
        }

        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);
        String defaultMessage = Validator.getMessages().getNumberRuleMessage();
        if (TextUtils.isEmpty(message) && defaultMessage != null) {
            message = defaultMessage;
        }
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
        String defaultMessage = Validator.getMessages().getPasswordRuleMessage();
        if (TextUtils.isEmpty(message) && defaultMessage != null) {
            message = defaultMessage;
        }
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
        String defaultMessage = Validator.getMessages().getConfirmPasswordRuleMessage();
        if (TextUtils.isEmpty(message) && defaultMessage != null) {
            message = defaultMessage;
        }
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
        String defaultMessage = Validator.getMessages().getIpAddressRuleMessage();
        if (TextUtils.isEmpty(message) && defaultMessage != null) {
            message = defaultMessage;
        }
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
