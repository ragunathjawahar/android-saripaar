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

import android.widget.Spinner;

import com.mobsandgeeks.saripaar.annotation.Select;

import java.lang.annotation.Annotation;

/**
 * Adapter that returns the current selection index from a {@link android.widget.Spinner} using the
 * {@link android.widget.Spinner#getSelectedItemPosition()} method.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class SpinnerIndexAdapter implements ViewDataAdapter<Spinner, Integer> {

    @Override
    public Integer getData(final Spinner spinner) {
        return spinner.getSelectedItemPosition();
    }

    @Override
    public <T extends Annotation> boolean containsOptionalValue(final Spinner spinner,
            final T ruleAnnotation) {
        int selection = spinner.getSelectedItemPosition();

        return ruleAnnotation instanceof Select
                && selection == ((Select) ruleAnnotation).defaultSelection();
    }
}
