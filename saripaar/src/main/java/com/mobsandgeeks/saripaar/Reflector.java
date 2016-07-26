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

import android.view.View;

import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.annotation.ValidateUsing;
import com.mobsandgeeks.saripaar.exception.SaripaarViolationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Contains reflection methods that are helpful for introspection and retrieval of frequently used
 * methods and attributes.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
final class Reflector {

    /**
     * Retrieves the attribute method of the given {@link java.lang.annotation.Annotation}.
     *
     * @param annotationType  The {@link java.lang.annotation.Annotation}
     *      {@link java.lang.Class} to check.
     * @param attributeName  Attribute name.
     *
     * @return The {@link java.lang.reflect.Method} if the attribute is present,
     *      null otherwise.
     */
    static Method getAttributeMethod(final Class<? extends Annotation> annotationType,
            final String attributeName) {
        Method attributeMethod = null;
        try {
            attributeMethod = annotationType.getMethod(attributeName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return attributeMethod;
    }

    /**
     * Retrieve an attribute value from an {@link java.lang.annotation.Annotation}.
     *
     * @param annotation  An {@link java.lang.annotation.Annotation} instance.
     * @param attributeName  Attribute name.
     * @param attributeDataType  {@link java.lang.Class} representing the attribute data type.
     * @param <T>  Attribute value type.
     *
     * @return The attribute value.
     */
    @SuppressWarnings("unchecked")
    static <T> T getAttributeValue(final Annotation annotation, final String attributeName,
            final Class<T> attributeDataType) {

        T attributeValue = null;
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Method attributeMethod = getAttributeMethod(annotationType, attributeName);

        if (attributeMethod == null) {
            String message = String.format("Cannot find attribute '%s' in annotation '%s'.",
                    attributeName, annotationType.getName());
            throw new IllegalStateException(message);
        } else {
            try {
                Object result = attributeMethod.invoke(annotation);
                attributeValue = attributeDataType.isPrimitive()
                        ? (T) result
                        : attributeDataType.cast(result);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return attributeValue;
    }

    /**
     * Checks if an annotation was annotated with the given annotation.
     *
     * @param inspected  The {@link java.lang.annotation.Annotation} to be checked.
     * @param expected  The {@link java.lang.annotation.Annotation} that we are looking for.
     *
     * @return true if the annotation is present, false otherwise.
     */
    static boolean isAnnotated(final Class<? extends Annotation> inspected,
            final Class<? extends Annotation> expected) {
        boolean isAnnotated = false;
        Annotation[] declaredAnnotations = inspected.getDeclaredAnnotations();
        for (Annotation declaredAnnotation : declaredAnnotations) {
            isAnnotated = expected.equals(declaredAnnotation.annotationType());
            if (isAnnotated) {
                break;
            }
        }
        return isAnnotated;
    }

    /**
     * Finds and returns the correct
     * {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter#getData(android.view.View)}
     * {@link java.lang.reflect.Method}.
     *
     * @param dataAdapterType  The {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}
     *      class whose {@code getData(View)} method is required.
     *
     * @return The correct {@code getData(View)} method.
     */
    static Method findGetDataMethod(final Class<? extends ViewDataAdapter> dataAdapterType) {
        Method getDataMethod = null;
        Method[] declaredMethods = dataAdapterType.getDeclaredMethods();

        for (Method method : declaredMethods) {
            boolean methodNameIsGetData = "getData".equals(method.getName());

            if (methodNameIsGetData) {
                // If we don't declare a throws clause in the method signature, the compiler
                // creates an auto-generated volatile method with the java.lang.Object return type.
                int modifiers = method.getModifiers();
                boolean nonVolatile = !Modifier.isVolatile(modifiers);

                // Single 'View' parameter
                Class<?>[] parameterTypes = method.getParameterTypes();
                boolean hasSingleViewParameter = parameterTypes.length == 1
                        && View.class.isAssignableFrom(parameterTypes[0]);

                if (nonVolatile && hasSingleViewParameter) {
                    getDataMethod = method;
                    break;
                }
            }
        }
        return getDataMethod;
    }

    /**
     * Instantiates a {@link AnnotationRule} object for the given type.
     *
     * @param ruleType  The {@link AnnotationRule} class to be instantiated.
     * @param ruleAnnotation  The rule {@link java.lang.annotation.Annotation} associated with
     *      the {@link AnnotationRule}.
     *
     * @return The instantiated {@link AnnotationRule} object.
     *
     * @throws SaripaarViolationException if {@link AnnotationRule} does not
     *      have a single-argument constructor that accepts a rule
     *      {@link java.lang.annotation.Annotation} instance.
     */
    static AnnotationRule instantiateRule(final Class<? extends AnnotationRule> ruleType,
            final Annotation ruleAnnotation, final ValidationContext validationContext)
                    throws SaripaarViolationException {
        AnnotationRule rule = null;

        try {
            if (ContextualAnnotationRule.class.isAssignableFrom(ruleType)) {
                Constructor<?> constructor = ruleType.getDeclaredConstructor(
                        ruleAnnotation.annotationType(), ValidationContext.class);
                constructor.setAccessible(true);
                rule = (AnnotationRule) constructor.newInstance(ruleAnnotation, validationContext);
            } else if (AnnotationRule.class.isAssignableFrom(ruleType)) {
                Constructor<?> constructor = ruleType.getDeclaredConstructor(
                        ruleAnnotation.annotationType());
                constructor.setAccessible(true);
                rule = (AnnotationRule) constructor.newInstance(ruleAnnotation);
            }
        } catch (NoSuchMethodException e) {
            String message = getMissingConstructorErrorMessage(ruleType,
                    ruleAnnotation.annotationType());
            throw new SaripaarViolationException(message);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return rule;
    }

    /**
     * Method finds the data type of the {@link AnnotationRule} that is tied up to the given rule
     * annotation.
     *
     * @param ruleAnnotation  Rule {@link java.lang.annotation.Annotation}.
     *
     * @return The expected data type for the
     *      {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}s.
     */
    static Class<?> getRuleDataType(final Annotation ruleAnnotation) {
        ValidateUsing validateUsing = getValidateUsingAnnotation(ruleAnnotation.annotationType());
        return getRuleDataType(validateUsing);
    }

    /**
     * Method finds the data type of the {@link AnnotationRule} that is tied up to the given rule
     * annotation.
     *
     * @param validateUsing  The {@link com.mobsandgeeks.saripaar.annotation.ValidateUsing}
     *      instance.
     *
     * @return The expected data type for the
     *      {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}s.
     */
    static Class<?> getRuleDataType(final ValidateUsing validateUsing) {
        Class<? extends AnnotationRule> rule = validateUsing.value();
        Method[] methods = rule.getDeclaredMethods();
        return getRuleTypeFromIsValidMethod(rule, methods);
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *  Private Methods
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static ValidateUsing getValidateUsingAnnotation(
            final Class<? extends Annotation> annotationType) {
        ValidateUsing validateUsing = null;

        Annotation[] declaredAnnotations = annotationType.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            if (ValidateUsing.class.equals(annotation.annotationType())) {
                validateUsing = (ValidateUsing) annotation;
                break;
            }
        }
        return validateUsing;
    }

    private static String getMissingConstructorErrorMessage(
            final Class<? extends AnnotationRule> ruleType,
            final Class<? extends Annotation> annotationType) {

        String message = null;
        if (ContextualAnnotationRule.class.isAssignableFrom(ruleType)) {
            message = String.format("A constructor accepting a '%s' and a '%s' is required for %s.",
                    annotationType.getName(), ValidationContext.class,
                    ruleType.getName());
        } else if (AnnotationRule.class.isAssignableFrom(ruleType)) {
            message = String.format(
                    "'%s' should have a single-argument constructor that accepts a '%s' instance.",
                    ruleType.getName(), annotationType.getName());
        }

        return message;
    }

    private static Class<?> getRuleTypeFromIsValidMethod(final Class<? extends AnnotationRule> rule,
            final Method[] methods) {

        Class<?> returnType = null;
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();

            if (matchesIsValidMethodSignature(method, parameterTypes)) {
                // This will be null, if there are no matching methods
                // in the class with a similar signature.
                if (returnType != null) {
                    String message = String.format(
                            "Found duplicate 'boolean isValid(T)' method signature in '%s'.",
                            rule.getName());
                    throw new SaripaarViolationException(message);
                }
                returnType = parameterTypes[0];
            }
        }
        return returnType;
    }

    private static boolean matchesIsValidMethodSignature(final Method method,
            final Class<?>[] parameterTypes) {
        int modifiers = method.getModifiers();

        boolean isPublic = Modifier.isPublic(modifiers);
        boolean nonVolatile = !Modifier.isVolatile(modifiers);
        boolean returnsBoolean = Boolean.TYPE.equals(method.getReturnType());
        boolean matchesMethodName = "isValid".equals(method.getName());
        boolean hasSingleParameter = parameterTypes.length == 1;

        return isPublic && nonVolatile && returnsBoolean && matchesMethodName && hasSingleParameter;
    }

    private Reflector() {
    }
}
