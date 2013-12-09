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

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.IpAddress;
import com.mobsandgeeks.saripaar.annotation.NumberRule;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Regex;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.Select;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
    private boolean mAnnotationsProcessed;
    private List<ViewRulePair> mViewsAndRules;
    private Map<String, Object> mProperties;
    private AsyncTask<Void, Void, ViewRulePair> mAsyncValidationTask;
    private ValidationListener mValidationListener;

    /**
     * Private constructor. Cannot be instantiated.
     */
    private Validator() {
        mAnnotationsProcessed = false;
        mViewsAndRules = new ArrayList<Validator.ViewRulePair>();
        mProperties = new HashMap<String, Object>();
    }

    /**
     * Creates a new {@link Validator}.
     *
     * @param controller The instance that holds references to the Views that are
     * being validated. Usually an {@code Activity} or a {@code Fragment}. Also accepts
     * controller instances that have annotated {@code View} references.
     */
    public Validator(Object controller) {
        this();
        if (controller == null) {
            throw new IllegalArgumentException("'controller' cannot be null");
        }
        mController = controller;
    }

    /**
     * Interface definition for a callback to be invoked when {@code validate()} is called.
     */
    public interface ValidationListener {

        /**
         * Called when all the {@link Rule}s added to this Validator are valid.
         */
        public void onValidationSucceeded();

        /**
         * Called if any of the {@link Rule}s fail.
         *
         * @param failedView The {@link View} that did not pass validation.
         * @param failedRule The failed {@link Rule} associated with the {@link View}.
         */
        public void onValidationFailed(View failedView, Rule<?> failedRule);
    }

    /**
     * Add a {@link View} and it's associated {@link Rule} to the Validator.
     *
     * @param view The {@link View} to be validated.
     * @param rule The {@link Rule} associated with the view.
     *
     * @throws IllegalArgumentException If {@code rule} is {@code null}.
     */
    public void put(View view, Rule<?> rule) {
        if (rule == null) {
            throw new IllegalArgumentException("'rule' cannot be null");
        }

        mViewsAndRules.add(new ViewRulePair(view, rule));
    }

    /**
     * Convenience method for adding multiple {@link Rule}s for a single {@link View}.
     *
     * @param view The {@link View} to be validated.
     * @param rules {@link List} of {@link Rule}s associated with the view.
     *
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

        ViewRulePair failedViewRulePair = validateAllRules();
        if (failedViewRulePair == null) {
            mValidationListener.onValidationSucceeded();
        } else {
            mValidationListener.onValidationFailed(failedViewRulePair.view, failedViewRulePair.rule);
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
        mAsyncValidationTask = new AsyncTask<Void, Void, ViewRulePair>() {

            @Override
            protected ViewRulePair doInBackground(Void... params) {
                return validateAllRules();
            }

            @Override
            protected void onPostExecute(ViewRulePair pair) {
                if (pair == null) {
                    mValidationListener.onValidationSucceeded();
                } else {
                    mValidationListener.onValidationFailed(pair.view, pair.rule);
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
     * @param name The property name.
     * @param value Value of the property.
     *
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
     *
     * @throws IllegalArgumentException If {@code name} is {@code null}.
     *
     * @return Value of the property or {@code null} if the property does not exist.
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
     *
     * @return The value of the removed property or {@code null} if the property was not found.
     */
    public Object removeProperty(String name) {
        return name != null ? mProperties.remove(name) : null;
    }

    /**
     * Checks if the specified property exists in this Validator.
     *
     * @param name The property name.
     *
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
     * @param view The {@code View} whose rules must be removed.
     */
    public void removeRulesFor(View view) {
        if (view == null) {
            throw new IllegalArgumentException("'view' cannot be null");
        }

        int index = 0;
        while (index < mViewsAndRules.size()) {
            ViewRulePair pair = mViewsAndRules.get(index);
            if (pair.view == view) {
                mViewsAndRules.remove(index);
                continue;
            }

            index++;
        }
    }

    /**
     * Validates all rules added to this Validator.
     *
     * @return {@code null} if all {@code Rule}s are valid, else returns the failed
     *          {@code ViewRulePair}.
     */
    private ViewRulePair validateAllRules() {
        if (!mAnnotationsProcessed) {
            createRulesFromAnnotations(getSaripaarAnnotatedFields());
            mAnnotationsProcessed = true;
        }

        if (mViewsAndRules.size() == 0) {
            Log.i(TAG, "No rules found. Passing validation by default.");
            return null;
        }

        ViewRulePair failedViewRulePair = null;
        for (ViewRulePair pair : mViewsAndRules) {
            if (pair == null) continue;

            // Validate views only if they are visible and enabled
            if (pair.view != null) {
                if (!pair.view.isShown() || !pair.view.isEnabled()) continue;
            }

            if (!pair.rule.isValid(pair.view)) {
                failedViewRulePair = pair;
                break;
            }
        }

        return failedViewRulePair;
    }

    private void createRulesFromAnnotations(List<AnnotationFieldPair> annotationFieldPairs) {
        TextView passwordTextView = null;
        TextView confirmPasswordTextView = null;

        for (AnnotationFieldPair pair : annotationFieldPairs) {
            // Password
            if (pair.annotation.annotationType().equals(Password.class)) {
                if (passwordTextView == null) {
                    passwordTextView = (TextView) getView(pair.field);
                } else {
                    throw new IllegalStateException("You cannot annotate " +
                            "two fields in the same Activity with @Password.");
                }
            }

            // Confirm password
            if (pair.annotation.annotationType().equals(ConfirmPassword.class)) {
                if (passwordTextView == null) {
                    throw new IllegalStateException("A @Password annotated field is required " +
                            "before you can use @ConfirmPassword.");
                } else if (confirmPasswordTextView != null) {
                    throw new IllegalStateException("You cannot annotate " +
                            "two fields in the same Activity with @ConfirmPassword.");
                } else if (confirmPasswordTextView == null) {
                    confirmPasswordTextView = (TextView) getView(pair.field);
                }
            }

            // Others
            ViewRulePair viewRulePair = null;
            if (pair.annotation.annotationType().equals(ConfirmPassword.class)) {
                viewRulePair = getViewAndRule(pair.field, pair.annotation, passwordTextView);
            } else {
                viewRulePair = getViewAndRule(pair.field, pair.annotation);
            }
            if (viewRulePair != null) {
                if (DEBUG) {
                    Log.d(TAG, String.format("Added @%s rule for %s.",
                            pair.annotation.annotationType().getSimpleName(),
                            pair.field.getName()));
                }
                mViewsAndRules.add(viewRulePair);
            }
        }
    }

    private ViewRulePair getViewAndRule(Field field, Annotation annotation, Object... params) {
        View view = getView(field);
        if (view == null) {
            Log.w(TAG, String.format("Your %s - %s is null. Please check your field assignment(s).",
                    field.getType().getSimpleName(), field.getName()));
            return null;
        }

        Rule<?> rule = null;
        if (params != null && params.length > 0) {
            rule = AnnotationToRuleConverter.getRule(field, view, annotation, params);
        } else {
            rule = AnnotationToRuleConverter.getRule(field, view, annotation);
        }

        return rule != null ? new ViewRulePair(view, rule) : null;
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

    private List<AnnotationFieldPair> getSaripaarAnnotatedFields() {
        List<AnnotationFieldPair> annotationFieldPairs = new ArrayList<AnnotationFieldPair>();
        List<Field> fieldsWithAnnotations = getViewFieldsWithAnnotations();

        for (Field field : fieldsWithAnnotations) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (isSaripaarAnnotation(annotation)) {
                    annotationFieldPairs.add(new AnnotationFieldPair(annotation, field));
                }
            }
        }

        Collections.sort(annotationFieldPairs, new AnnotationFieldPairCompartor());

        return annotationFieldPairs;
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
                annotationType.equals(TextRule.class);
    }

    private class ViewRulePair {
        public View view;
        public Rule rule;

        public ViewRulePair(View view, Rule<?> rule) {
            this.view = view;
            this.rule = rule;
        }
    }

    private class AnnotationFieldPair {
        public Annotation annotation;
        public Field field;

        public AnnotationFieldPair(Annotation annotation, Field field) {
            this.annotation = annotation;
            this.field = field;
        }
    }

    private class AnnotationFieldPairCompartor implements Comparator<AnnotationFieldPair> {

        @Override
        public int compare(AnnotationFieldPair lhs, AnnotationFieldPair rhs) {
            int lhsOrder = getAnnotationOrder(lhs.annotation);
            int rhsOrder = getAnnotationOrder(rhs.annotation);
            return lhsOrder < rhsOrder ? -1 : lhsOrder == rhsOrder ? 0 : 1;
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

            } else {
                throw new IllegalArgumentException(String.format("%s is not a Saripaar annotation",
                        annotatedClass.getName()));
            }
        }
    }

}
