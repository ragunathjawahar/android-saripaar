/*
 * Copyright (C) 2014 Mobs & Geeks
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

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.adapter.CheckBoxBooleanAdapter;
import com.mobsandgeeks.saripaar.adapter.RadioButtonBooleanAdapter;
import com.mobsandgeeks.saripaar.adapter.RadioGroupBooleanAdapter;
import com.mobsandgeeks.saripaar.adapter.SpinnerIndexAdapter;
import com.mobsandgeeks.saripaar.adapter.TextViewDoubleAdapter;
import com.mobsandgeeks.saripaar.adapter.TextViewFloatAdapter;
import com.mobsandgeeks.saripaar.adapter.TextViewIntegerAdapter;
import com.mobsandgeeks.saripaar.adapter.TextViewStringAdapter;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.annotation.ValidateUsing;
import com.mobsandgeeks.saripaar.exception.SaripaarViolationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Maintains a registry of all {@link android.view.View}s and
 * {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}s that are registered to rule
 * {@link java.lang.annotation.Annotation}s.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
final class Registry {
    // Constants
    public static final String TAG = "Registry";

    // Stock adapters that come with Saripaar
    private static final Map<Class<? extends View>,
            HashMap<Class<?>, ViewDataAdapter>> STOCK_ADAPTERS =
                    new HashMap<Class<? extends View>, HashMap<Class<?>, ViewDataAdapter>>();

    // Attributes
    private Map<Class<? extends Annotation>,
            HashMap<Class<? extends View>, ViewDataAdapter>> mMappings;

    /**
     * Good Ol' constructor.
     */
    Registry() {
        mMappings = new HashMap<Class<? extends Annotation>,
                HashMap<Class<? extends View>, ViewDataAdapter>>();
    }

    /**
     * This is a convenience method for Annotations that operate on {@link android.widget.TextView}
     * and it's subclasses such as {@link android.widget.EditText}. Use this to register your custom
     * annotation if your {@link AnnotationRule} performs validations on
     * {@link java.lang.String}s, {@link java.lang.Integer}s, {@link java.lang.Float}s and
     * {@link java.lang.Double} values.
     *
     * @param ruleAnnotations  Varargs of rule {@link java.lang.annotation.Annotation}s that operate
     *      on {@link android.widget.TextView}s.
     */
    @SuppressWarnings("unchecked")
    public void register(final Class<? extends Annotation>... ruleAnnotations) {
        for (Class<? extends Annotation> ruleAnnotation : ruleAnnotations) {
            assertIsValidRuleAnnotation(ruleAnnotation);

            final ValidateUsing validateUsing = ruleAnnotation.getAnnotation(ValidateUsing.class);
            final Class<?> ruleDataType = Reflector.getRuleDataType(validateUsing);

            HashMap<Class<?>, ViewDataAdapter> viewDataAdapterMap =
                    STOCK_ADAPTERS.get(TextView.class);
            if (viewDataAdapterMap != null) {
                ViewDataAdapter dataAdapter = viewDataAdapterMap.get(ruleDataType);
                if (dataAdapter != null) {
                    register(TextView.class, ruleDataType, dataAdapter, ruleAnnotation);
                } else {
                    String message = String.format(
                            "Unable to find a matching adapter for `%s`, that returns a `%s`.",
                            ruleAnnotation.getName(), ruleDataType.getName());
                    throw new SaripaarViolationException(message);
                }
            }
        }
    }

    /**
     * Registers {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}s for the associated
     * {@link AnnotationRule}s and their {@link android.view.View}s.
     *
     * @param viewType  The {@link android.view.View} type on which the {@link AnnotationRule}
     *      can be used.
     * @param ruleDataType  Data type expected by the {@link AnnotationRule}.
     * @param viewDataAdapter  The {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}
     *      that can get the data for the {@link AnnotationRule} from the
     *      {@link android.view.View}.
     * @param ruleAnnotations  Varargs of rule {@link java.lang.annotation.Annotation}s that
     *      can be used with the {@link android.view.View} and the
     *      {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}.
     *
     * @param <VIEW>  Type parameter that is a subclass of {@link android.view.View} class.
     * @param <DATA_TYPE>  Data type expected by the {@link AnnotationRule} and
     *      is returned by the {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}.
     */
    public <VIEW extends View, DATA_TYPE> void register(
            final Class<VIEW> viewType,
            final Class<DATA_TYPE> ruleDataType,
            final ViewDataAdapter<VIEW, DATA_TYPE> viewDataAdapter,
            final Class<? extends Annotation>... ruleAnnotations) {

        if (ruleAnnotations != null && ruleAnnotations.length > 0) {
            for (Class<? extends Annotation> ruleAnnotation : ruleAnnotations) {
                register(ruleAnnotation, ruleDataType, viewType, viewDataAdapter);
            }
        }
    }

    /**
     * Retrieve all registered rule annotations.
     *
     * @return {@link java.util.Set} containing all registered rule
     *      {@link java.lang.annotation.Annotation}s.
     */
    public Set<Class<? extends Annotation>> getRegisteredAnnotations() {
        return mMappings.keySet();
    }

    /**
     * Retrieves the registered {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter} associated
     * with the rule {@link java.lang.annotation.Annotation} and
     * {@link android.view.View}. If no registered adapter is not found, the method looks for a
     * compatible adapter instead.
     *
     * @param annotationType  The rule annotation type that requires a data adapter.
     * @param viewType  The {@link android.view.View} whose adapter we are looking for.
     *
     * @param <VIEW>  Type parameter that ensures type safety for the {@link View} and
     *      the {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}.
     *
     * @return Registered or compatible
     *      {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter} or null if none is
     *      found.
     */
    @SuppressWarnings("unchecked")
    public <VIEW extends View> ViewDataAdapter<VIEW, ?> getDataAdapter(
            final Class< ? extends Annotation> annotationType,
            final Class<VIEW> viewType) {

        HashMap<Class<? extends View>, ViewDataAdapter> viewDataAdapterHashMap =
            mMappings.get(annotationType);

        // Check for a direct match
        ViewDataAdapter matchingViewAdapter = null;
        if (viewDataAdapterHashMap != null) {
            matchingViewAdapter = viewDataAdapterHashMap.get(viewType);

            // If no 'ViewDataAdapter' is registered, check for a compatible one
            if (matchingViewAdapter == null) {
                matchingViewAdapter = getCompatibleViewDataAdapter(viewDataAdapterHashMap,
                        viewType);
            }
        }

        return matchingViewAdapter;
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private <VIEW extends View, DATA_TYPE> void register(
            final Class<? extends Annotation> ruleAnnotation,
            final Class<DATA_TYPE> ruleDataType,
            final Class<VIEW> view,
            final ViewDataAdapter<VIEW, DATA_TYPE> viewDataAdapter) {
        assertIsValidRuleAnnotation(ruleAnnotation);
        assertCompatibleReturnType(ruleDataType, viewDataAdapter);

        // Get the view-adapter pairs registered to a rule annotation.
        HashMap<Class<? extends View>, ViewDataAdapter> viewAdapterPairs;
        if (mMappings.containsKey(ruleAnnotation)) {
            viewAdapterPairs = mMappings.get(ruleAnnotation);
        } else {
            viewAdapterPairs = new HashMap<Class<? extends View>, ViewDataAdapter>();
            mMappings.put(ruleAnnotation, viewAdapterPairs);
        }

        if (viewAdapterPairs.containsKey(view)) {
            String message = String.format("A '%s' for '%s' has already been registered.",
                    ruleAnnotation.getName(), view.getName());
            Log.w(TAG, message);
        } else {
            viewAdapterPairs.put(view, viewDataAdapter);
        }
    }

    private void assertIsValidRuleAnnotation(final Class<? extends Annotation> ruleAnnotation) {
        // 1. Check for @ValidateUsing annotation
        boolean validRuleAnnotation = Reflector.isAnnotated(ruleAnnotation, ValidateUsing.class);
        if (!validRuleAnnotation) {
            String message = String.format("'%s' is not annotated with '%s'.",
                    ruleAnnotation.getName(), ValidateUsing.class.getName());
            throw new IllegalArgumentException(message);
        }

        // 2. Check for 'sequence' attribute
        assertAttribute(ruleAnnotation, "sequence", Integer.TYPE);

        // 3. Check for 'message' attribute
        assertAttribute(ruleAnnotation, "message", String.class);

        // 4. Check for 'messageResId' attribute
        assertAttribute(ruleAnnotation, "messageResId", Integer.TYPE);
    }

    private void assertAttribute(final Class<? extends Annotation> annotationType,
            final String attributeName, final Class<?> attributeType) {
        Method attributeMethod = Reflector.getAttributeMethod(annotationType, attributeName);

        if (attributeMethod == null) {
            String message = String.format("'%s' requires the '%s' attribute.",
                    annotationType.getName(), attributeName);
            throw new SaripaarViolationException(message);
        }

        final Class<?> returnType = attributeMethod.getReturnType();
        if (!attributeType.equals(returnType)) {
            String message = String.format("'%s' in '%s' should be of type '%s', but was '%s'.",
                    attributeName, annotationType.getName(),
                    attributeType.getName(), returnType.getName());
            throw new SaripaarViolationException(message);
        }
    }

    private <DATA_TYPE, VIEW extends View> void assertCompatibleReturnType(
            final Class<DATA_TYPE> ruleDataType,
            final ViewDataAdapter<VIEW, DATA_TYPE> viewDataAdapter) {
        Method getDataMethod = Reflector.findGetDataMethod(viewDataAdapter.getClass());

        Class<?> adapterReturnType = getDataMethod.getReturnType();
        if (!ruleDataType.equals(adapterReturnType)) {
            String message = String.format("'%s' returns '%s', but expecting '%s'.",
                    viewDataAdapter.getClass().getName(),
                    adapterReturnType.getName(),
                    ruleDataType.getName());
            throw new IllegalArgumentException(message);
        }
    }

    private <VIEW extends View> ViewDataAdapter getCompatibleViewDataAdapter(
            final HashMap<Class<? extends View>, ViewDataAdapter> viewDataAdapterHashMap,
            final Class<VIEW> viewType) {

        ViewDataAdapter compatibleViewAdapter = null;
        Set<Class<? extends View>> registeredViews = viewDataAdapterHashMap.keySet();
        for (Class<? extends View> registeredView : registeredViews) {
            if (registeredView.isAssignableFrom(viewType)) {
                compatibleViewAdapter = viewDataAdapterHashMap.get(registeredView);
            }
        }
        return compatibleViewAdapter;
    }

    // Register all views along with their corresponding adapters
    static {
        HashMap<Class<?>, ViewDataAdapter> adapters;

        // CheckBox
        adapters = new HashMap<Class<?>, ViewDataAdapter>();
        adapters.put(Boolean.class, new CheckBoxBooleanAdapter());
        STOCK_ADAPTERS.put(CheckBox.class, adapters);

        // RadioButton
        adapters = new HashMap<Class<?>, ViewDataAdapter>();
        adapters.put(Boolean.class, new RadioButtonBooleanAdapter());
        STOCK_ADAPTERS.put(RadioButton.class, adapters);

        // RadioGroup
        adapters = new HashMap<Class<?>, ViewDataAdapter>();
        adapters.put(Boolean.class, new RadioGroupBooleanAdapter());
        STOCK_ADAPTERS.put(RadioGroup.class, adapters);

        // Spinner
        adapters = new HashMap<Class<?>, ViewDataAdapter>();
        adapters.put(Integer.class, new SpinnerIndexAdapter());
        STOCK_ADAPTERS.put(Spinner.class, adapters);

        // TextView
        adapters = new HashMap<Class<?>, ViewDataAdapter>();
        adapters.put(String.class, new TextViewStringAdapter());
        adapters.put(Integer.class, new TextViewIntegerAdapter());
        adapters.put(Float.class, new TextViewFloatAdapter());
        adapters.put(Double.class, new TextViewDoubleAdapter());
        STOCK_ADAPTERS.put(TextView.class, adapters);
    }
}
