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

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.IpAddress;
import com.mobsandgeeks.saripaar.annotation.MatchServerErrors;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A processor that checks all the {@link Rule}s against their {@link View}s.
 *
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
public class Validator {
    // Debug
    static final String TAG = "Validator";
    static final boolean DEBUG = false;

    private Object mController;
    private volatile boolean mAnnotationsProcessed;
    private List<ViewRulePair> validationForm = new ArrayList<ViewRulePair>();
    private List<ViewErrorKeyPair> serverValidationForm = new ArrayList<ViewErrorKeyPair>();
    private Map<String, Object> mProperties = new HashMap<String, Object>();
    private AsyncTask<Void, Void, List<ViewErrorPair>> mAsyncValidationTask;
    private ValidationListener mValidationListener;

    /**
     * Private constructor. Cannot be instantiated.
     */
    private Validator() {
        mAnnotationsProcessed = false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public <F extends android.app.Fragment> Validator(F fragment) {
        this();
        if (fragment == null) {
            throw new IllegalArgumentException("'controller' cannot be null");
        }
        mController = fragment;
        initForm();

    }

    public <F extends Fragment> Validator(F fragment) {
        this();
        if (fragment == null) {
            throw new IllegalArgumentException("'controller' cannot be null");
        }
        mController = fragment;
        initForm();
    }

    public <A extends Activity> Validator(A activity) {
        this();
        if (activity == null) {
            throw new IllegalArgumentException("'controller' cannot be null");
        }
        mController = activity;
        initForm();
    }

    private void initForm() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                mAnnotationsProcessed = false;
            }

            @Override
            protected Void doInBackground(Void... params) {
                createRulesFromAnnotations(getSaripaarAnnotatedFields());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAnnotationsProcessed = true;
                if (mValidationListener != null) {
                    mValidationListener.onFormPrepared();
                }
            }
        }.execute();
    }

    //Bundle[{email=has already been taken}]
    //Bundle[{email=has already been taken, password=is too short (minimum is 6 characters)}]
    public void mapServerErrors(Map<String, String> possibleField) {
        List<ViewErrorPair> list = new ArrayList<ViewErrorPair>();
        if (possibleField != null) {
            for (String key : possibleField.keySet()) {
                for (ViewErrorKeyPair viewErrorKeyPair : serverValidationForm) {
                    if (viewErrorKeyPair.errorKeys.contains(key)) {
                        list.add(new ViewErrorPair(viewErrorKeyPair.view, possibleField.get(key)));
                    }
                }
            }
        }
        if (mValidationListener != null) {
            mValidationListener.onServerMappingFinish(list);
        }
    }

    /**
     * Interface definition for a callback to be invoked when {@code validate()} is called.
     */
    public interface ValidationListener {

        public void onFormPrepared();

        /**
         * Called when all the {@link Rule}s added to this Validator are valid.
         */
        public void onValidationSucceeded();

        /**
         * Called if any of the {@link Rule}s fail.
         */
        public void onValidationFailed(List<ViewErrorPair> failedResults);

        /**
         * Called after server return error and json mapped to fields
         */
        public void onServerMappingFinish(List<ViewErrorPair> mappingResults);
    }

    /**
     * Add a {@link View} and it's associated {@link Rule} to the Validator.
     *
     * @param view The {@link View} to be validated.
     * @param rule The {@link Rule} associated with the view.
     * @throws IllegalArgumentException If {@code rule} is {@code null}.
     */
    public void put(View view, Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("'rule' cannot be null");
        }

        validationForm.add(new ViewRulePair(view, Collections.singletonList(rule)));
    }

    /**
     * Convenience method for adding multiple {@link Rule}s for a single {@link View}.
     *
     * @param view  The {@link View} to be validated.
     * @param rules {@link List} of {@link Rule}s associated with the view.
     * @throws IllegalArgumentException If {@code rules} is {@code null}.
     */
    public void put(View view, List<Rule<?>> rules) {
        if (rules == null) {
            throw new IllegalArgumentException("\'rules\' cannot be null");
        }

        for (Rule<?> rule : rules) {
            put(view, rule);
        }
    }

    /**
     * Convenience method for adding just {@link Rule}s to the Validator.
     *
     * @param rule A {@link Rule}, usually composite or custom.
     */
    public void put(Rule<?> rule) {
        put(null, rule);
    }

    /**
     * Validate all the {@link Rule}s against their {@link View}s.
     *
     * @throws IllegalStateException If a {@link ValidationListener} is not registered.
     */
    public synchronized void validate() {
        if (mValidationListener == null) {
            throw new IllegalStateException("Set a " + ValidationListener.class.getSimpleName() +
                    " before attempting to validate.");
        }
        List<ViewErrorPair> failedViewRulePair = validateAllRules();
        if (failedViewRulePair != null && failedViewRulePair.size() > 0 || !mAnnotationsProcessed) {
            mValidationListener.onValidationFailed(failedViewRulePair);
        } else {
            mValidationListener.onValidationSucceeded();
        }
    }

    /**
     * Asynchronously validates all the {@link Rule}s against their {@link View}s. Subsequent calls
     * to this method will cancel any pending asynchronous validations and start a new one.
     *
     * @throws IllegalStateException If a {@link ValidationListener} is not registered.
     */
    public void validateAsync() {
        if (mValidationListener == null) {
            throw new IllegalStateException("Set a " + ValidationListener.class.getSimpleName() +
                    " before attempting to validate.");
        }

        // Cancel the existing task
        if (mAsyncValidationTask != null) {
            mAsyncValidationTask.cancel(true);
            mAsyncValidationTask = null;
        }

        // Start a new one ;)
        mAsyncValidationTask = new AsyncTask<Void, Void, List<ViewErrorPair>>() {

            @Override
            protected List<ViewErrorPair> doInBackground(Void... params) {
                return validateAllRules();
            }

            @Override
            protected void onPostExecute(List<ViewErrorPair> pair) {
                if (pair != null && pair.size() > 0 || !mAnnotationsProcessed) {
                    mValidationListener.onValidationFailed(pair);
                } else {
                    mValidationListener.onValidationSucceeded();
                }

                mAsyncValidationTask = null;
            }

            @Override
            protected void onCancelled() {
                mAsyncValidationTask = null;
            }
        };

        mAsyncValidationTask.execute((Void[]) null);
    }

    /**
     * Used to find if the asynchronous validation task is running, useful only when you run the
     * Validator in asynchronous mode using the {@code validateAsync} method.
     *
     * @return True if the asynchronous task is running, false otherwise.
     */
    public boolean isValidating() {
        return mAsyncValidationTask != null &&
                mAsyncValidationTask.getStatus() != AsyncTask.Status.FINISHED;
    }

    /**
     * Cancels the asynchronous validation task if running, useful only when you run the
     * Validator in asynchronous mode using the {@code validateAsync} method.
     *
     * @return True if the asynchronous task was cancelled.
     */
    public boolean cancelAsync() {
        boolean cancelled = false;
        if (mAsyncValidationTask != null) {
            cancelled = mAsyncValidationTask.cancel(true);
            mAsyncValidationTask = null;
        }

        return cancelled;
    }

    /**
     * Returns the callback registered for this Validator.
     *
     * @return The callback, or null if one is not registered.
     */
    public ValidationListener getValidationListener() {
        return mValidationListener;
    }

    /**
     * Register a callback to be invoked when {@code validate()} is called.
     *
     * @param validationListener The callback that will run.
     */
    public void setValidationListener(ValidationListener validationListener) {
        this.mValidationListener = validationListener;
    }

    /**
     * Updates a property value if it exists, else creates a new one.
     *
     * @param name  The property name.
     * @param value Value of the property.
     * @throws IllegalArgumentException If {@code name} is {@code null}.
     */
    public void setProperty(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("\'name\' cannot be null");
        }

        mProperties.put(name, value);
    }

    /**
     * Retrieves the value of the given property.
     *
     * @param name The property name.
     * @return Value of the property or {@code null} if the property does not exist.
     * @throws IllegalArgumentException If {@code name} is {@code null}.
     */
    public Object getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("\'name\' cannot be null");
        }

        return mProperties.get(name);
    }

    /**
     * Removes the property from this Validator.
     *
     * @param name The property name.
     * @return The value of the removed property or {@code null} if the property was not found.
     */
    public Object removeProperty(String name) {
        return name != null ? mProperties.remove(name) : null;
    }

    /**
     * Checks if the specified property exists in this Validator.
     *
     * @param name The property name.
     * @return True if the property exists.
     */
    public boolean containsProperty(String name) {
        return name != null ? mProperties.containsKey(name) : false;
    }

    /**
     * Removes all properties from this Validator.
     */
    public void removeAllProperties() {
        mProperties.clear();
    }

    /**
     * Removes all the rules for the matching {@link View}
     *
     * @param view The {@code View} whose rules must be removed.
     */
    public void removeRulesFor(View view) {
        if (view == null) {
            throw new IllegalArgumentException("'view' cannot be null");
        }

        int index = 0;
        while (index < validationForm.size()) {
            ViewRulePair pair = validationForm.get(index);
            if (pair.getView() == view) {
                validationForm.remove(index);
                continue;
            }

            index++;
        }
    }

    /**
     * Validates all rules added to this Validator.
     *
     * @return {@code null} if all {@code Rule}s are valid, else returns the failed
     * {@code ViewRulePair}.
     */
    private List<ViewErrorPair> validateAllRules() {
        if (!mAnnotationsProcessed || validationForm.size() == 0) {
            Log.i(TAG, "No rules found. Passing validation by default.");
            return null;
        }
        List<ViewErrorPair> list = new ArrayList<ViewErrorPair>();
        for (ViewRulePair pair : validationForm) {
            if (pair == null) continue;

            // Validate views only if they are visible and enabled
            View view = pair.getView();
            if (view != null) {
                if (view.getVisibility() != View.VISIBLE || !view.isEnabled()) continue;
            }
            for (Rule rule : pair.getRules()) {
                if (!rule.isValid(view)) {
                    if (rule.getFailureMessage() != null) {
                        list.add(new ViewErrorPair(view, rule.getFailureMessage()));
                    }
                    break;
                } else {
                    try {
                        TextView textView = (TextView) view;
                        textView.setError(null, null);
                    } catch (ClassCastException ignore) {
                        //ignore
                    }
                }
            }
        }

        return list;
    }

    private void createRulesFromAnnotations(List<FieldAnnotationsPair> fieldAnnotationsPairs) {
        TextView passwordTextView = null;
        int passwordViewCount = 0;
        int confirmPasswordViewCount = 0;
        for (FieldAnnotationsPair pair : fieldAnnotationsPairs) {
            for (Annotation annotation : pair.annotations) {
                if (annotation.annotationType().equals(Password.class)) {
                    passwordTextView = (TextView) getView(pair.field);
                    passwordViewCount++;

                }
                if (annotation.annotationType().equals(ConfirmPassword.class)) {
                    confirmPasswordViewCount++;
                }
            }
        }

        if (passwordViewCount > 1) {
            throw new IllegalStateException("You cannot annotate " +
                    "two fields of the same form with @Password.");
        }
        if (confirmPasswordViewCount > 1) {
            throw new IllegalStateException("You cannot annotate " +
                    "two fields of the same form with @ConfirmPassword.");
        }
        if (confirmPasswordViewCount > 0 && passwordViewCount == 0) {
            throw new IllegalStateException("A @Password annotated field is required " +
                    "before you can use @ConfirmPassword.");
        }

        for (FieldAnnotationsPair pair : fieldAnnotationsPairs) {
            View view = getView(pair.field);
            if (view == null) {
                Log.w(TAG, String.format("Your %s - %s is null. Please check your field assignment(s).",
                        pair.field.getType().getSimpleName(), pair.field.getName()));
                continue;
            }
            List<Rule> rules = new ArrayList<Rule>();
            for (Annotation annotation : pair.annotations) {
                Rule<?> rule = null;
                Class<?> annotationType = annotation.annotationType();
                if (annotationType.equals(ConfirmPassword.class)) {
                    rule = AnnotationRuleFactory.getRule(pair.field, view, annotation, passwordTextView);
                } else if (annotationType.equals(MatchServerErrors.class)) {
                    serverValidationForm.add(getViewErrorKeyPair(pair.field, annotation));
                } else {
                    rule = AnnotationRuleFactory.getRule(pair.field, view, annotation);
                }
                if (rule != null) {
                    rules.add(rule);
                }
            }
            validationForm.add(new ViewRulePair(view, rules));
        }
    }

    private ViewErrorKeyPair getViewErrorKeyPair(Field field, Annotation annotation) {
        View view = getView(field);
        if (view == null) {
            Log.w(TAG, String.format("Your %s - %s is null. Please check your field assignment(s).",
                    field.getType().getSimpleName(), field.getName()));
            return null;
        }
        Class<?> annotationType = annotation.annotationType();
        if (annotationType.equals(MatchServerErrors.class)) {
            if (!TextView.class.isAssignableFrom(view.getClass())) {
                //Log.w(TAG, String.format(WARN_TEXT, field.getName(), Regex.class.getSimpleName()));
                return null;
            }
            List<String> errorKeys = Arrays.asList(((MatchServerErrors) annotation).value());
            return new ViewErrorKeyPair(view, errorKeys);
        }
        return null;
    }

    private View getView(Field field) {
        try {
            field.setAccessible(true);
            Object instance = mController;

            return (View) field.get(instance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<FieldAnnotationsPair> getSaripaarAnnotatedFields() {
        List<FieldAnnotationsPair> fieldAnnotationsPairs = new ArrayList<FieldAnnotationsPair>();
        List<Field> fieldsWithAnnotations = getViewFieldsWithAnnotations();

        for (Field field : fieldsWithAnnotations) {
            Annotation[] annotations = field.getAnnotations();
            List<Annotation> annotationsList = new ArrayList<Annotation>();
            for (Annotation annotation : annotations) {
                if (isSaripaarAnnotation(annotation)) {
                    if (DEBUG) {
                        Log.d(TAG, String.format("%s %s is annotated with @%s",
                                field.getType().getSimpleName(), field.getName(),
                                annotation.annotationType().getSimpleName()));
                    }
                    annotationsList.add(annotation);
                }
            }

            Collections.sort(annotationsList, new AnnotationComparator());
            fieldAnnotationsPairs.add(new FieldAnnotationsPair(field, annotationsList));
        }
        return fieldAnnotationsPairs;
    }

    private List<Field> getViewFieldsWithAnnotations() {
        List<Field> fieldsWithAnnotations = new ArrayList<Field>();
        List<Field> viewFields = getAllViewFields();
        for (Field field : viewFields) {
            Annotation[] annotations = field.getAnnotations();
            if (annotations == null || annotations.length == 0) {
                continue;
            }
            fieldsWithAnnotations.add(field);
        }
        return fieldsWithAnnotations;
    }

    private List<Field> getAllViewFields() {
        List<Field> viewFields = new ArrayList<Field>();

        // Declared fields
        Class<?> superClass = null;
        if (mController != null) {
            viewFields.addAll(getDeclaredViewFields(mController.getClass()));
            superClass = mController.getClass().getSuperclass();
        }

        // Inherited fields
        while (superClass != null && !superClass.equals(Object.class)) {
            List<Field> declaredViewFields = getDeclaredViewFields(superClass);
            if (declaredViewFields.size() > 0) {
                viewFields.addAll(declaredViewFields);
            }
            superClass = superClass.getSuperclass();
        }

        return viewFields;
    }

    private List<Field> getDeclaredViewFields(Class<?> clazz) {
        List<Field> viewFields = new ArrayList<Field>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field f : declaredFields) {
            if (View.class.isAssignableFrom(f.getType())) {
                viewFields.add(f);
            }
        }
        return viewFields;
    }

    private boolean isSaripaarAnnotation(Annotation annotation) {
        Class<?> annotationType = annotation.annotationType();
        return annotationType.equals(Checked.class) ||
                annotationType.equals(ConfirmPassword.class) ||
                annotationType.equals(Email.class) ||
                annotationType.equals(IpAddress.class) ||
                annotationType.equals(NumberRule.class) ||
                annotationType.equals(Password.class) ||
                annotationType.equals(Regex.class) ||
                annotationType.equals(Required.class) ||
                annotationType.equals(Select.class) ||
                annotationType.equals(TextRule.class) ||
                annotationType.equals(Optional.class) ||
                annotationType.equals(MatchServerErrors.class);
    }

    private class ViewErrorKeyPair {
        public View view;
        public List<String> errorKeys;

        private ViewErrorKeyPair(View view, List<String> errorKeys) {
            this.view = view;
            this.errorKeys = errorKeys;
        }
    }

    private class FieldAnnotationsPair {
        public Field field;
        public List<Annotation> annotations;

        public FieldAnnotationsPair(Field field, List<Annotation> annotations) {
            this.annotations = annotations;
            this.field = field;
        }
    }

    private class AnnotationComparator implements Comparator<Annotation> {

        @Override
        public int compare(Annotation lhs, Annotation rhs) {
            int lhsOrder = getAnnotationOrder(lhs);
            int rhsOrder = getAnnotationOrder(rhs);
            return lhsOrder < rhsOrder ? 1 : lhsOrder == rhsOrder ? 0 : -1;
        }

        private int getAnnotationOrder(Annotation annotation) {
            Class<?> annotatedClass = annotation.annotationType();
            if (annotatedClass.equals(Checked.class)) {
                return ((Checked) annotation).order();

            } else if (annotatedClass.equals(ConfirmPassword.class)) {
                return ((ConfirmPassword) annotation).order();

            } else if (annotatedClass.equals(Email.class)) {
                return ((Email) annotation).order();

            } else if (annotatedClass.equals(IpAddress.class)) {
                return ((IpAddress) annotation).order();

            } else if (annotatedClass.equals(NumberRule.class)) {
                return ((NumberRule) annotation).order();

            } else if (annotatedClass.equals(Password.class)) {
                return ((Password) annotation).order();

            } else if (annotatedClass.equals(Regex.class)) {
                return ((Regex) annotation).order();

            } else if (annotatedClass.equals(Required.class)) {
                return ((Required) annotation).order();

            } else if (annotatedClass.equals(Select.class)) {
                return ((Select) annotation).order();

            } else if (annotatedClass.equals(TextRule.class)) {
                return ((TextRule) annotation).order();

            } else if (annotatedClass.equals(Optional.class)) {
                return ((Optional) annotation).order();

            } else {
                return Integer.MIN_VALUE;
            }
        }
    }
}
