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

/**
 * Contains a failed {@link android.view.View} and the corresponding
 * {@link com.mobsandgeeks.saripaar.Rule}.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 */
public class ValidationError {
    private final View view;
    private final Rule failedRule;

    /**
     * Constructor.
     *
     * @param view  A failed {@link View}.
     * @param failedRule  A failed {@link com.mobsandgeeks.saripaar.Rule}.
     */
    ValidationError(final View view, final Rule failedRule) {
        this.view = view;
        this.failedRule = failedRule;
    }

    /**
     * Gets the failed {@link android.view.View}.
     *
     * @return The failed view.
     */
    public View getView() {
        return view;
    }

    /**
     * Gets the failed {@link com.mobsandgeeks.saripaar.Rule}.
     *
     * @return The failed rule.
     */
    public Rule getFailedRule() {
        return failedRule;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ValidationError{" +
            "view=" + view +
            ", failedRule=" + failedRule +
            '}';
    }
}
