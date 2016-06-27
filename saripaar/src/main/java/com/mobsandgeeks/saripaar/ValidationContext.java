/*
 * Copyright (C) 2015 Mobs & Geeks
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
import android.util.Pair;
import android.view.View;

import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.annotation.ValidateUsing;
import com.mobsandgeeks.saripaar.exception.ConversionException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grants access to information about other {@link android.view.View}s in the controller object.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class ValidationContext {

    // Attributes
    private Map<View, ArrayList<Pair<Rule, ViewDataAdapter>>> mViewRulesMap;
    private Context mContext;

    ValidationContext(final Context context) {
        this.mContext = context;
    }

    /**
     * Retrieves all {@link android.view.View}s that are annotated with the specified annotation.
     *
     * @param saripaarAnnotation  The annotation we are interested in.
     *
     * @return A {@link java.util.List} of {@link android.view.View}s annotated with the
     *      given annotation.
     */
    public List<View> getAnnotatedViews(final Class<? extends Annotation> saripaarAnnotation) {
        assertNotNull(saripaarAnnotation, "saripaarAnnotation");
        assertIsRegisteredAnnotation(saripaarAnnotation);

        // Get the AnnotationRule class
        Class<? extends AnnotationRule> annotationRuleClass = getRuleClass(saripaarAnnotation);

        // Find all views with the target rule
        List<View> annotatedViews = new ArrayList<View>();
        Set<View> views = mViewRulesMap.keySet();
        for (View view : views) {
            ArrayList<Pair<Rule, ViewDataAdapter>> ruleAdapterPairs = mViewRulesMap.get(view);
            for (Pair<Rule, ViewDataAdapter> ruleAdapterPair : ruleAdapterPairs) {
                boolean uniqueMatchingView =
                        annotationRuleClass.equals(ruleAdapterPair.first.getClass())
                                && !annotatedViews.contains(view);
                if (uniqueMatchingView) {
                    annotatedViews.add(view);
                }
            }
        }

        return annotatedViews;
    }

    /**
     * Retrieves the data from the given {@link android.view.View} using the appropriate
     * {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}.
     *
     * @param view  A {@link android.view.View}.
     * @param saripaarAnnotation  The annotation used to annotate the {@link android.view.View}.
     *
     * @return The data that's on the {@link android.view.View}.
     */
    public Object getData(final View view, Class<? extends Annotation> saripaarAnnotation) {
        assertNotNull(view, "view");
        assertNotNull(saripaarAnnotation, "saripaarAnnotation");

        Object data = null;
        ArrayList<Pair<Rule, ViewDataAdapter>> ruleAdapterPairs = mViewRulesMap.get(view);
        Class<? extends AnnotationRule> annotationRuleClass = getRuleClass(saripaarAnnotation);

        for (Pair<Rule, ViewDataAdapter> ruleAdapterPair : ruleAdapterPairs) {
            if (annotationRuleClass.equals(ruleAdapterPair.first.getClass())) {
                try {
                    data = ruleAdapterPair.second.getData(view);
                } catch (ConversionException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    /**
     * Get a {@link Context}.
     *
     * @return A {@link Context}.
     */
    public Context getContext() {
        return mContext;
    }

    void setViewRulesMap(final Map<View, ArrayList<Pair<Rule, ViewDataAdapter>>> viewRulesMap) {
        mViewRulesMap = viewRulesMap;
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void assertNotNull(final Object object, final String argumentName) {
        if (object == null) {
            String message = String.format("'%s' cannot be null.", argumentName);
            throw new IllegalArgumentException(message);
        }
    }

    private void assertIsRegisteredAnnotation(
            final Class<? extends Annotation> saripaarAnnotation) {
        if (!Validator.isSaripaarAnnotation(saripaarAnnotation)) {
            String message = String.format("%s is not a registered Saripaar annotation.",
                    saripaarAnnotation.getName());
            throw new IllegalArgumentException(message);
        }
    }

    private Class<? extends AnnotationRule> getRuleClass(
            final Class<? extends Annotation> saripaarAnnotation) {
        ValidateUsing validateUsingAnnotation = saripaarAnnotation
                .getAnnotation(ValidateUsing.class);
        return validateUsingAnnotation.value();
    }
}
