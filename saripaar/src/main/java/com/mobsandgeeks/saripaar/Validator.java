/*
 * Copyright (C) 2014 Mobs and Geeks
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

import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.mobsandgeeks.saripaar.adapter.CheckBoxBooleanAdapter;
import com.mobsandgeeks.saripaar.adapter.RadioButtonBooleanAdapter;
import com.mobsandgeeks.saripaar.adapter.SpinnerIndexAdapter;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.annotation.AssertFalse;
import com.mobsandgeeks.saripaar.annotation.AssertTrue;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.CreditCard;
import com.mobsandgeeks.saripaar.annotation.DecimalMax;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Url;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Isbn;
import com.mobsandgeeks.saripaar.annotation.IpAddress;
import com.mobsandgeeks.saripaar.annotation.Max;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.mobsandgeeks.saripaar.annotation.Select;
import com.mobsandgeeks.saripaar.annotation.Size;
import com.mobsandgeeks.saripaar.annotation.ValidateUsing;
import com.mobsandgeeks.saripaar.exception.ConversionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
@SuppressWarnings("unchecked")
public class Validator {

    public interface ValidationListener {
        void onValidationSucceeded();
        void onValidationFailed(List<ValidationError> errors);
    }

    /**
     * Validation modes.
     */
    public enum Mode {

        /**
         * BURST mode will validate all rules before calling the
         * {@link com.mobsandgeeks.saripaar.Validator.ValidationListener#onValidationFailed(java.util.List)}
         * callback.
         */
        BURST,

        /**
         * IMMEDIATE mode will stop the validation as soon as it encounters the first failing rule.
         */
        IMMEDIATE
    }

    // Entries are registered inside a static block (Placed at the end of source)
    private static final Registry SARIPAAR_REGISTRY = new Registry();

    // Holds adapter entries that are mapped to corresponding views.
    private static final
        Map<Class<? extends View>, HashMap<Class<?>, ViewDataAdapter>> REGISTERED_ADAPTERS =
            new HashMap<Class<? extends View>, HashMap<Class<?>, ViewDataAdapter>>();

    // Attributes
    private Object mController;
    private Mode mValidationMode;
    private Map<View, ArrayList<RuleAdapterPair>> mViewRulesMap;
    private boolean mOrderedRules;
    private ValidationListener mValidationListener;

    public Validator(final Object controller) {
        if (controller == null) {
            throw new IllegalArgumentException("'controller' cannot be null.");
        }
        mController = controller;
        mValidationMode = Mode.BURST;
    }

    public static void register(final Class<? extends Annotation> ruleAnnotation) {
        SARIPAAR_REGISTRY.register(ruleAnnotation);
    }

    public static <VIEW extends View, DATA_TYPE> void registerAdapter(
            final Class<VIEW> viewType, final ViewDataAdapter<VIEW, DATA_TYPE> viewDataAdapter) {
        assertNotNull(viewType, "viewType");
        assertNotNull(viewDataAdapter, "viewDataAdapter");

        HashMap<Class<?>, ViewDataAdapter> dataTypeAdapterMap = REGISTERED_ADAPTERS.get(viewType);
        if (dataTypeAdapterMap == null) {
            dataTypeAdapterMap = new HashMap<Class<?>, ViewDataAdapter>();
            REGISTERED_ADAPTERS.put(viewType, dataTypeAdapterMap);
        }

        // Find adapter's data type
        Method getDataMethod = Reflector.findGetDataMethod(viewDataAdapter.getClass());
        Class<?> adapterDataType = getDataMethod.getReturnType();

        dataTypeAdapterMap.put(adapterDataType, viewDataAdapter);
    }

    public Mode getValidationMode() {
        return mValidationMode;
    }

    public void setValidationMode(final Mode validationMode) {
        if (validationMode == null) {
            throw new IllegalArgumentException("'validationMode' cannot be null.");
        }
        this.mValidationMode = validationMode;
    }

    public void setValidationListener(final ValidationListener validationListener) {
        this.mValidationListener = validationListener;
    }

    public synchronized void validate() {
        createRulesSafelyAndLazily();

        View lastView = getLastView();
        if (Mode.BURST.equals(mValidationMode)) {
            validateTill(lastView, false, null);
        } else if (Mode.IMMEDIATE.equals(mValidationMode)) {
            String reasonSuffix = String.format("in %s mode.", Mode.IMMEDIATE.toString());
            validateTill(lastView, true, reasonSuffix);
        } else {
            throw new RuntimeException("This should never happen!");
        }
    }

    public synchronized void validateBefore(final View view) {
        createRulesSafelyAndLazily();
        View previousView = getViewBefore(view);
        validateTill(previousView, true, "when using 'validateBefore(View)'.");
    }

    public synchronized void validateTill(final View view) {
        createRulesSafelyAndLazily();
        validateTill(view, true, "when using 'validateTill(View)'.");
    }

    public void put(final View view, final QuickRule... quickRules) {
        if (view == null) {
            throw new IllegalArgumentException("'view' cannot be null.");
        }
        if (quickRules == null || quickRules.length == 0) {
            throw new IllegalArgumentException("'quickRules' cannot be null or empty.");
        }
        createRulesSafelyAndLazily();

        // If there were no rules, create an empty list
        ArrayList<RuleAdapterPair> ruleAdapterPairs = mViewRulesMap.get(view);
        ruleAdapterPairs = ruleAdapterPairs == null
            ? new ArrayList<RuleAdapterPair>() : ruleAdapterPairs;

        for (QuickRule quickRule : quickRules) {
            ruleAdapterPairs.add(new RuleAdapterPair(quickRule, null));
        }
        mViewRulesMap.put(view, ruleAdapterPairs);
    }

    private void createRulesSafelyAndLazily() {
        // Create rules lazily, because we don't have to worry about the order of
        // instantiating the Validator.
        if (mViewRulesMap == null) {
            final List<Field> annotatedFields = getSaripaarAnnotatedFields(mController.getClass());
            mViewRulesMap = createRules(annotatedFields);
        }

        if (mViewRulesMap.size() == 0) {
            String message = "No rules found. You must have at least one rule to validate. " +
                "If you are using custom annotations, make sure that you have registered " +
                "them using the 'Validator.register()' method.";
            throw new IllegalStateException(message);
        }
    }

    private void validateTill(final View view, final boolean requiresOrderedRules,
            final String reasonSuffix) {
        // Do we need ordered rules?
        if (requiresOrderedRules) {
            assertOrderedRules(mOrderedRules, reasonSuffix);
        }

        // Have we registered a validation listener?
        assertNotNull(mValidationListener, "validationListener");

        // Everything good. Bingo! validate ;)
        final ValidationReport validationReport = getValidationErrors(view,
            mViewRulesMap, mValidationMode);
        final List<ValidationError> validationErrors = validationReport.errors;
        if (validationErrors.size() == 0 && !validationReport.hasMoreErrors) {
            mValidationListener.onValidationSucceeded();
        } else {
            mValidationListener.onValidationFailed(validationErrors);
        }
    }

    private static void assertNotNull(final Object object, final String argumentName) {
        if (object == null) {
            String message = String.format("'%s' cannot be null.", argumentName);
            throw new IllegalArgumentException(message);
        }
    }

    private void assertOrderedRules(boolean orderedRules, String reasonSuffix) {
        if (!orderedRules) {
            String message = String.format(
                "Rules are unordered, every rule annotation and custom rule should have " +
                    "valid 'order' attribute set " + reasonSuffix);
            throw new IllegalStateException(message);
        }
    }

    private List<Field> getSaripaarAnnotatedFields(final Class<?> controllerClass) {
        Set<Class<? extends Annotation>> saripaarAnnotations =
            SARIPAAR_REGISTRY.getRegisteredAnnotations();

        List<Field> annotatedFields = new ArrayList<Field>();
        List<Field> controllerViewFields = getControllerViewFields(controllerClass);
        for (Field field : controllerViewFields) {
            if (isSaripaarAnnotatedField(field, saripaarAnnotations)) {
                annotatedFields.add(field);
            }
        }

        // Sort
        SaripaarFieldsComparator comparator = new SaripaarFieldsComparator();
        Collections.sort(annotatedFields, comparator);
        mOrderedRules = comparator.areOrderedFields();

        return annotatedFields;
    }

    private View getLastView() {
        final Set<View> views = mViewRulesMap.keySet();

        View lastView = null;
        for (View view : views) {
            lastView = view;
        }

        return lastView;
    }

    private View getViewBefore(final View view) {
        ArrayList<View> views = new ArrayList<View>(mViewRulesMap.keySet());

        final int nViews = views.size();
        View currentView;
        View previousView = null;
        for (int i = 0; i < nViews; i++) {
            currentView = views.get(i);
            if (currentView == view) {
                previousView = i > 0 ? views.get(i - 1) : null;
                break;
            }
        }

        return previousView;
    }

    private ValidationReport getValidationErrors(final View targetView,
        final Map<View, ArrayList<RuleAdapterPair>> viewRulesMap, final Mode validationMode) {

        final List<ValidationError> validationErrors = new ArrayList<ValidationError>();
        final Set<View> views = viewRulesMap.keySet();

        // Don't add errors for views that are placed after the specified view in validateTill()
        boolean addErrorToReport = targetView != null;

        // Does the form have more errors? Used in validateTill()
        boolean hasMoreErrors = false;

        validation:
        for (View view : views) {
            ArrayList<RuleAdapterPair> ruleAdapterPairs = viewRulesMap.get(view);
            int rulesForThisView = ruleAdapterPairs.size();

            // Validate all the rules for the given view.
            for (int i = 0; i < rulesForThisView; i++) {
                final RuleAdapterPair ruleAdapterPair = ruleAdapterPairs.get(i);
                final Rule rule = ruleAdapterPair.rule;
                final ViewDataAdapter dataAdapter = ruleAdapterPair.dataAdapter;

                // Validate only views that are visible and enabled
                if (view.isShown() && view.isEnabled()) {
                    ValidationError validationError = validateViewWithRule(view, rule, dataAdapter);
                    boolean isLastRuleForView = rulesForThisView == i + 1;

                    if (validationError != null) {
                        if (addErrorToReport) {
                            validationErrors.add(validationError);
                        } else {
                            hasMoreErrors = true;
                        }

                        if (Mode.IMMEDIATE.equals(validationMode) && isLastRuleForView) {
                            break validation;
                        }
                    }

                    // Don't add reports for subsequent views
                    if (view.equals(targetView) && isLastRuleForView) {
                        addErrorToReport = false;
                    }
                }
            }
        }

        return new ValidationReport(validationErrors, hasMoreErrors);
    }

    private List<Field> getControllerViewFields(final Class<?> controllerClass) {
        List<Field> controllerViewFields = new ArrayList<Field>();

        // Fields declared in the controller
        controllerViewFields.addAll(getViewFields(controllerClass));

        // Inherited fields
        Class<?> superClass = controllerClass.getSuperclass();
        while (!superClass.equals(Object.class)) {
            List<Field> viewFields = getViewFields(superClass);
            if (viewFields.size() > 0) {
                controllerViewFields.addAll(viewFields);
            }
            superClass = superClass.getSuperclass();
        }

        return controllerViewFields;
    }

    private boolean isSaripaarAnnotatedField(final Field field,
            final Set<Class<? extends Annotation>> registeredAnnotations) {
        boolean hasOrderAnnotation = field.getAnnotation(Order.class) != null;
        boolean hasSaripaarAnnotation = false;

        if (!hasOrderAnnotation) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                hasSaripaarAnnotation = registeredAnnotations.contains(annotation.annotationType());
                if (hasSaripaarAnnotation) {
                    break;
                }
            }
        }

        return hasOrderAnnotation || hasSaripaarAnnotation;
    }

    private List<Field> getViewFields(final Class<?> clazz) {
        List<Field> viewFields = new ArrayList<Field>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (View.class.isAssignableFrom(field.getType())) {
                viewFields.add(field);
            }
        }

        return viewFields;
    }

    private Map<View, ArrayList<RuleAdapterPair>> createRules(final List<Field> annotatedFields) {
        final Map<View, ArrayList<RuleAdapterPair>> viewRulesMap =
            new LinkedHashMap<View, ArrayList<RuleAdapterPair>>();

        for (Field field : annotatedFields) {
            final ArrayList<RuleAdapterPair> ruleAdapterPairs = new ArrayList<RuleAdapterPair>();
            final Annotation[] fieldAnnotations = field.getAnnotations();
            for (Annotation fieldAnnotation : fieldAnnotations) {
                if (isSaripaarAnnotation(fieldAnnotation)) {
                    RuleAdapterPair ruleAdapterPair = getRuleAdapterPair(fieldAnnotation, field);
                    ruleAdapterPairs.add(ruleAdapterPair);
                }
            }

            viewRulesMap.put(getView(field), ruleAdapterPairs);
        }

        return viewRulesMap;
    }

    private boolean isSaripaarAnnotation(final Annotation annotation) {
        return SARIPAAR_REGISTRY.getRegisteredAnnotations().contains(annotation.annotationType());
    }

    private RuleAdapterPair getRuleAdapterPair(final Annotation saripaarAnnotation,
            final Field viewField) {
        final Class<? extends Annotation> annotationType = saripaarAnnotation.annotationType();
        final Class<?> viewFieldType = viewField.getType();
        final Class<?> ruleDataType = Reflector.getRuleDataType(saripaarAnnotation);

        final ViewDataAdapter dataAdapter = getDataAdapter(annotationType, viewFieldType,
            ruleDataType);

        // If no matching adapter is found, throw.
        if (dataAdapter == null) {
            String message = String.format(
                "To use '%s' on '%s', register a '%s' that returns a '%s'.",
                annotationType.getName(),
                viewFieldType.getName(),
                ViewDataAdapter.class.getName(),
                ruleDataType.getName());
            throw new UnsupportedOperationException(message);
        }

        final Class<? extends AnnotationRule> ruleType = getRuleType(saripaarAnnotation);
        final AnnotationRule rule = Reflector.instantiateRule(ruleType, saripaarAnnotation);

        return new RuleAdapterPair(rule, dataAdapter);
    }

    private ViewDataAdapter getDataAdapter(final Class<? extends Annotation> annotationType,
            final Class<?> viewFieldType,
            final Class<?> adapterDataType) {
        // Get an adapter from the stock registry
        ViewDataAdapter dataAdapter = SARIPAAR_REGISTRY.getDataAdapter(
            annotationType, (Class) viewFieldType);

        // If we are unable to find a Saripaar stock adapter, check the registered adapters
        if (dataAdapter == null) {
            HashMap<Class<?>, ViewDataAdapter> dataTypeAdapterMap =
                REGISTERED_ADAPTERS.get(viewFieldType);
            dataAdapter = dataTypeAdapterMap != null
                ? dataTypeAdapterMap.get(adapterDataType)
                : null;
        }

        return dataAdapter;
    }

    private Class<? extends AnnotationRule> getRuleType(final Annotation ruleAnnotation) {
        ValidateUsing validateUsing = null;

        Annotation[] annotationsOfRuleAnnotation =
            ruleAnnotation.annotationType().getAnnotations();
        for (Annotation annotation : annotationsOfRuleAnnotation) {
            if (ValidateUsing.class.equals(annotation.annotationType())) {
                validateUsing = (ValidateUsing) annotation;
            }
        }

        return validateUsing != null ? validateUsing.value() : null;
    }

    private View getView(final Field field) {
        View view = null;
        try {
            field.setAccessible(true);
            view = (View) field.get(mController);

            if (view == null) {
                String message = String.format("'%s %s' is null.",
                    field.getType().getSimpleName(), field.getName());
                throw new IllegalStateException(message);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return view;
    }

    private ValidationError validateViewWithRule(final View view, final Rule rule,
            final ViewDataAdapter dataAdapter) {

        boolean valid = false;
        if (rule instanceof AnnotationRule) {
            Object data;

            try {
                data = dataAdapter.getData(view);
            } catch (ConversionException e) {
                e.printStackTrace();
                throw new IllegalStateException(e.getMessage());
            }

            valid = ((AnnotationRule) rule).isValid(data);
        } else if (rule instanceof QuickRule) {
            valid = ((QuickRule) rule).isValid(view);
        }

        return !valid ? new ValidationError(view, rule) : null;
    }

    static class RuleAdapterPair {
        Rule rule;
        ViewDataAdapter dataAdapter;

        RuleAdapterPair(Rule rule, ViewDataAdapter dataAdapter) {
            this.rule = rule;
            this.dataAdapter = dataAdapter;
        }
    }

    static class ValidationReport {
        List<ValidationError> errors;
        boolean hasMoreErrors;

        ValidationReport(List<ValidationError> errors, boolean hasMoreErrors) {
            this.errors = errors;
            this.hasMoreErrors = hasMoreErrors;
        }
    }

    static {
        // CheckBoxBooleanAdapter
        SARIPAAR_REGISTRY.register(CheckBox.class, Boolean.class, new CheckBoxBooleanAdapter(),
            AssertFalse.class, AssertTrue.class, Checked.class);

        // RadioButtonBooleanAdapter
        SARIPAAR_REGISTRY.register(RadioButton.class, Boolean.class, new RadioButtonBooleanAdapter(),
            AssertFalse.class, AssertTrue.class, Checked.class);

        // SpinnerIndexAdapter
        SARIPAAR_REGISTRY.register(Spinner.class, Integer.class, new SpinnerIndexAdapter(),
            Select.class);

        // TextViewDoubleAdapter
        SARIPAAR_REGISTRY.register(DecimalMax.class, DecimalMin.class);

        // TextViewIntegerAdapter
        SARIPAAR_REGISTRY.register(Max.class, Min.class);

        // TextViewStringAdapter
        SARIPAAR_REGISTRY.register(ConfirmPassword.class, CreditCard.class, Digits.class,
            Email.class, IpAddress.class, Isbn.class, NotEmpty.class,
            Password.class, Pattern.class, Size.class, Url.class);
//        SARIPAAR_REGISTRY.register(ConfirmPassword.class, CreditCard.class, Digits.class,
//            Email.class, Future.class, IpAddress.class, Isbn.class, NotEmpty.class,
//            Password.class, Past.class, Pattern.class, Size.class, Url.class);
    }

}
