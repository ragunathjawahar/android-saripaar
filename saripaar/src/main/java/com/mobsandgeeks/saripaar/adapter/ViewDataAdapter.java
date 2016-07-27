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

package com.mobsandgeeks.saripaar.adapter;

import android.view.View;

import com.mobsandgeeks.saripaar.exception.ConversionException;

import java.lang.annotation.Annotation;

/**
 * {@link com.mobsandgeeks.saripaar.adapter.ViewDataAdapter}s are used to extract data from
 * {@link android.view.View}s. Saripaar provides a set of default adapters for stock Android
 * widgets. Developers can implement their own adapters for custom views or data types they are
 * interested in.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public interface ViewDataAdapter<VIEW extends View, DATA> {

    /**
     * Extract and return the appropriate data from a given {@link android.view.View}.
     *
     * @param view  The {@link android.view.View} from which contains the data that we are
     *      interested in.
     *
     * @return The interested data.
     *
     * @throws ConversionException If the adapter is unable to convert the data to the expected
     *      data type.
     */
    DATA getData(VIEW view) throws ConversionException;

    /**
     * Used to check if the {@link View} contains an optional value. This method is used
     * to cater the {@link com.mobsandgeeks.saripaar.annotation.Optional} annotation.
     *
     * @param view  The view that is being validated.
     * @param <T>  The rule annotation used to validate the view.
     *
     * @return  {@code true} if the value represents an optional value, {@code false} otherwise.
     */
    <T extends Annotation> boolean containsOptionalValue(VIEW view, T ruleAnnotation);
}
