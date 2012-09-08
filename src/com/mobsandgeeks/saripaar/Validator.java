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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.view.View;

/**
 *A processor that checks all the {@link Rule}s against their {@link View}s.
 *
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 * @version 0.1
 */
public class Validator {
    private List<ViewRulePair> mViewsAndRules;
    private Map<String, Object> mProperties;
    private AsyncTask<?, ?, ViewRulePair> mAsyncValidationTask;
    private ValidationListener mValidationListener;

    /**
     * Creates a new Validator.
     */
    public Validator() {
        mViewsAndRules = new ArrayList<Validator.ViewRulePair>();
        mProperties = new HashMap<String, Object>();
    }

    /**
     * Interface definition for a callback to be invoked when <code>validate()</code> is called.
     */
    public interface ValidationListener {

        /**
         * Called before the Validator begins validation.
         */
        public void preValidation();

        /**
         * Called when all the {@link Rule}s added to this Validator are valid.
         */
        public void onSuccess();

        /**
         * Called if any of the {@link Rule}s fail.
         *
         * @param failedView The {@link View} that did not pass validation.
         * @param failedRule The failed {@link Rule} associated with the {@link View}.
         */
        public void onFailure(View failedView, Rule<?> failedRule);

        /**
         * Called after the validation is cancelled. This callback is called only if you run the
         * Validator asynchronously by calling the <code>validateAsync()</code> method.
         */
        public void onValidationCancelled();
    }

    /**
     * Add a {@link View} and it's associated {@link Rule} to the Validator.
     *
     * @param view The {@link View} to be validated.
     * @param rule The {@link Rule} associated with the view.
     *
     * @throws IllegalArgumentException If <code>rule</code> is <code>null</code>.
     */
    public void put(View view, Rule<?> rule) {
        if (rule == null) {
            throw new IllegalArgumentException("\'rule\' cannot be null");
        }

        mViewsAndRules.add(new ViewRulePair(view, rule));
    }

    /**
     * Convenience method for adding multiple {@link Rule}s for a single {@link View}.
     *
     * @param view The {@link View} to be validated.
     * @param rules {@link List} of {@link Rule}s associated with the view.
     *
     * @throws IllegalArgumentException If <code>rules</code> is <code>null</code>.
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

        mValidationListener.preValidation();

        ViewRulePair failedViewRulePair = validateAllRules();
        if (failedViewRulePair == null) {
            mValidationListener.onSuccess();
        } else {
            mValidationListener.onFailure(failedViewRulePair.view, failedViewRulePair.rule);
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
        mAsyncValidationTask = new AsyncTask<Object, Object, ViewRulePair>() {

            @Override
            protected void onPreExecute() {
                mValidationListener.preValidation();
            }

            @Override
            protected ViewRulePair doInBackground(Object... params) {
                return validateAllRules();
            }

            @Override
            protected void onPostExecute(ViewRulePair pair) {
                if (pair == null) {
                    mValidationListener.onSuccess();
                } else {
                    mValidationListener.onFailure(pair.view, pair.rule);
                }

                mAsyncValidationTask = null;
            }

            @Override
            protected void onCancelled() {
                mAsyncValidationTask = null;
                mValidationListener.onValidationCancelled();
            }
        };

        mAsyncValidationTask.execute(null);
    }

    /**
     * Used to find if the asynchronous validation task is running, useful only when you run the
     * Validator in asynchronous mode using the <code>validateAsync</code> method.
     *
     * @return True if the asynchronous task is running, false otherwise.
     */
    public boolean isValidating() {
        return mAsyncValidationTask != null &&
                mAsyncValidationTask.getStatus() != AsyncTask.Status.FINISHED;
    }

    /**
     * Cancels the asynchronous validation task if running, useful only when you run the
     * Validator in asynchronous mode using the <code>validateAsync</code> method.
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
     * Register a callback to be invoked when <code>validate()</code> is called.
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
     * @throws IllegalArgumentException If <code>name</code> is <code>null</code>.
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
     * @throws IllegalArgumentException If <code>name</code> is <code>null</code>.
     *
     * @return Value of the property or <code>null</code> if the property does not exist.
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
     * @return The value of the removed property or <code>null</code> if the property was not found.
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
     * Validates all rules added to this Validator.
     * @return <code>null</code> if all {@link Rule}s are valid, else returns the failed
     *          {@link ViewRulePair}.
     */
    private ViewRulePair validateAllRules() {
        ViewRulePair failedViewRulePair = null;
        for (ViewRulePair pair : mViewsAndRules) {
            if (pair == null) continue;

            if (!pair.rule.isValid(pair.view)) {
                failedViewRulePair = pair;
                break;
            }
        }

        return failedViewRulePair;
    }

    @SuppressWarnings("rawtypes") 
    private class ViewRulePair {
        private View view;
        private Rule rule;

        public ViewRulePair(View view, Rule<?> rule) {
            this.view = view;
            this.rule = rule;
        }
    }

}
