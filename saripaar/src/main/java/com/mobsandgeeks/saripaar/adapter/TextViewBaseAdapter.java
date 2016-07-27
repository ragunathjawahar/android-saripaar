/*
 * Copyright (C) 2016 Mobs & Geeks
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
import android.widget.TextView;

import java.lang.annotation.Annotation;

/**
 * A base class that implements the {@link #containsOptionalValue(View, Annotation)} method for concrete
 * {@link TextView} adapters.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.1.0
 */
abstract class TextViewBaseAdapter<DATA> implements ViewDataAdapter<TextView, DATA> {

    @Override
    public <T extends Annotation> boolean containsOptionalValue(final TextView textView,
            final T annotation) {
        return "".equals(textView.getText().toString());
    }
}
