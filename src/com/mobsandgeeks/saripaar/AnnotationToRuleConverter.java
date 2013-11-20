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
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.mobsandgeeks.saripaar.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class contains {@code static} methods that return appropriate {@link Rule}s for Saripaar
 * annotations.
 *
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
class AnnotationToRuleConverter {
    // Debug
    static final String TAG = AnnotationToRuleConverter.class.getSimpleName();

    // Constants
    static final String WARN_TEXT = "%s - @%s can only be applied to TextView and " +
            "its subclasses.";
    static final String WARN_Spinner_TEXT = "%s - @%s can only be applied to Spinner and " +
            "its subclasses.";
    static final String WARN_RadioGroup_TEXT = "%s - @%s can only be applied to RadioGroup and " +
            "its subclasses.";
    static final String WARN_CHECKABLE = "%s - @%s can only be applied to Checkable, " +
            "its implementations and subclasses.";

    public static Rule<?> getRule(Field field, View view, Annotation annotation) {
        Class<?> annotationClass = annotation.getClass();

        if (Required.class.isAssignableFrom(annotationClass)) {
            if (view instanceof Spinner)
                return getRequiredRule(field, (Spinner) view, (Required) annotation);
            if (view instanceof RadioGroup)
                return getRequiredRule(field, (RadioGroup) view, (Required) annotation);
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

    public static List<Rule<?>> getRules(Field field, View view, Annotation annotation) {
        Class<?> annotationClass = annotation.getClass();
        if (DateRule.class.isAssignableFrom(annotationClass)) {
            return getDateRules(field, view, (DateRule) annotation);
        }
        return Collections.emptyList();
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

    private static Rule<Spinner> getRequiredRule(Field field, Spinner spinner, Required required) {
        if (!Spinner.class.isAssignableFrom(spinner.getClass())) {
            Log.w(TAG, String.format(WARN_Spinner_TEXT, field.getName(), Required.class.getSimpleName()));
            return null;
        }

        int messageResId = required.messageResId();
        String message = messageResId != 0 ? spinner.getContext().getString(messageResId) :
                required.message();

        return Rules.requiredSpinner(message);
    }

    private static Rule<RadioGroup> getRequiredRule(Field field, RadioGroup radioGroup, Required required) {
        if (!RadioGroup.class.isAssignableFrom(radioGroup.getClass())) {
            Log.w(TAG, String.format(WARN_RadioGroup_TEXT, field.getName(), Required.class.getSimpleName()));
            return null;
        }

        int messageResId = required.messageResId();
        String message = messageResId != 0 ? radioGroup.getContext().getString(messageResId) :
                required.message();

        return Rules.requiredRadioGroup(message);
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

    private static List<Rule<?>> getDateRules(Field field, View view, DateRule dateRule) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            Log.w(TAG, String.format(WARN_TEXT, field.getName(), DateRule.class.getSimpleName()));
            return Collections.emptyList();
        } else if (dateRule.pattern() == null) {
            throw new IllegalArgumentException(String.format("@%s.pattern() cannot be null.",
                    DateRule.class.getSimpleName()));
        }

        String pattern = dateRule.pattern();
        DateFormat dateFormat = new SimpleDateFormat(pattern);

        List<Rule<?>> rules = new ArrayList<Rule<?>>();

        int messageParseResId = dateRule.messageParseResId();
        String messageParse = messageParseResId != 0 ? view.getContext().getString(messageParseResId) : dateRule.messageParse();
        rules.add(Rules.formatDate(messageParse, dateFormat));

        if (dateRule.lt().trim().length() > 0) {
            String ltTxt = dateRule.lt();

            try {
                int messageLtResId = dateRule.messageLtResId();
                String messageLt = messageLtResId != 0 ? view.getContext().getString(messageLtResId) : dateRule.messageLt();
                Date ltDate = evaluateDate(ltTxt, dateFormat);
                rules.add(Rules.ltDate(messageLt, ltDate, dateFormat));
            } catch (ParseException e) {
                throw new IllegalArgumentException(String.format("@%s.lt() cannot be parsed using pattern.",
                        DateRule.class.getSimpleName()));
            }
        }

        if (dateRule.gt().trim().length() > 0) {
            String gtTxt = dateRule.gt();
            try {
                int messageGtResId = dateRule.messageGtResId();
                String messageGt = messageGtResId != 0 ? view.getContext().getString(messageGtResId) : dateRule.messageGt();
                Date gtDate = evaluateDate(gtTxt, dateFormat);
                rules.add(Rules.gtDate(messageGt, gtDate, dateFormat));
            } catch (ParseException e) {
                throw new IllegalArgumentException(String.format("@%s.lt() cannot be parsed using pattern.",
                        DateRule.class.getSimpleName()));
            }
        }

        return rules;
    }


    public static void main(String[] args) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            System.out.println("new Date()            = " + dateFormat.format(new Date()));
            System.out.println("now-1y+1M-1d+1h-1m+5s = " + dateFormat.format(evaluateDate("now-1y+1M-1d+1h-1m+5s", dateFormat)));
            System.out.println("now-0y-1M+1d-1h+1m-3s = " + dateFormat.format(evaluateDate("now-0y-1M+1d-1h+1m-3s", dateFormat)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Date evaluateDate(String txt, DateFormat dateFormat) throws ParseException {
        if (txt.startsWith("now")) {
            List<String> splits = new ArrayList<String>();
            txt = txt.substring(3);
            String[] splittedPlus = txt.split("[+]");
            for (String spitPlus : splittedPlus) {
                if (spitPlus.length() > 0) {
                    if (spitPlus.charAt(0) != '-') {
                        spitPlus = "+" + spitPlus;
                    }
                    String[] splittedMinus = spitPlus.split("[-]");
                    for (String splitMinus : splittedMinus) {
                        if (splitMinus.length() > 0) {
                            if (splitMinus.charAt(0) != '+') {
                                splitMinus = "-" + splitMinus;
                            }
                            splits.add(splitMinus);
                        }
                    }
                }
            }
            Calendar cal = GregorianCalendar.getInstance();
            for (String split : splits) {
                String operation = split.substring(0, 1);
                String unit = split.substring(split.length() - 1, split.length());
                String amount = split.substring(1, split.length() - 1);
                int value = Integer.parseInt(amount) * (operation.equals("+") ? 1 : -1);
                if (unit.equals("y")) {
                    cal.add(Calendar.YEAR, value);
                } else if (unit.equals("M")) {
                    cal.add(Calendar.MONTH, value);
                } else if (unit.equals("d")) {
                    cal.add(Calendar.DAY_OF_YEAR, value);
                } else if (unit.equals("h")) {
                    cal.add(Calendar.HOUR_OF_DAY, value);
                } else if (unit.equals("m")) {
                    cal.add(Calendar.MINUTE, value);
                } else if (unit.equals("s")) {
                    cal.add(Calendar.SECOND, value);
                } else {
                    System.err.println("evaluateDate.invalid UNIT: '" + unit + "'");
                }
            }
            return cal.getTime();
        } else {
            return dateFormat.parse(txt);
        }
    }
}
