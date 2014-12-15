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

import java.util.List;

/**
 * Contains a failed {@link android.view.View} and the corresponding
 * {@link com.mobsandgeeks.saripaar.Rule}.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class ValidationError {
    private final View view;
    private final List<Rule> failedRules;

    /**
     * Constructor.
     *
     * @param view  A failed {@link android.view.View}.
     * @param failedRules  A {@link java.util.List} of failed {@link com.mobsandgeeks.saripaar.Rule}s.
     */
    ValidationError(final View view, final List<Rule> failedRules) {
        this.view = view;
        this.failedRules = failedRules;
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
     * Gets the failed {@link com.mobsandgeeks.saripaar.Rule}s.
     *
     * @return A {@link java.util.List} of failed {@link com.mobsandgeeks.saripaar.Rule}s.
     */
    public List<Rule> getFailedRules() {
        return failedRules;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ValidationError{" +
            "view=" + view +
            ", failedRules=" + failedRules +
            '}';
    }
}
