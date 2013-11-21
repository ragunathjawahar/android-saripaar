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

import android.view.View;

/**
 * Abstract class that allows to define validation rules for {@link View}s.
 *
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 *
 * @param <T> The {@link View} this rule is applicable for.
 */
public abstract class Rule<T extends View> {

    private String mFailureMessage;

    /**
     * Creates a new validation Rule.
     *
     * @param failureMessage The failure message associated with the Rule.
     */
    public Rule(String failureMessage) {
        mFailureMessage = failureMessage;
    }

    /**
     * Returns the failure message associated with the rule.
     *
     * @return Returns the failure message associated with the rule
     */
    public String getFailureMessage() {
        return mFailureMessage;
    }

    /**
     * Sets the failure message for the Rule.
     *
     * @param failureMessage The failure message associated with the Rule.
     */
    public void setFailureMessage(String failureMessage) {
        this.mFailureMessage = failureMessage;
    }

    /**
     * Checks whether the Rule is valid for the associated {@link View}.
     *
     * @param view The view associated with this Rule.
     * @return True if validation succeeds, false otherwise.
     */
    public abstract boolean isValid(T view);

}
