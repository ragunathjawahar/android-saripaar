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

package com.mobsandgeeks.saripaar.rule;

import android.view.View;

import com.mobsandgeeks.saripaar.ContextualAnnotationRule;
import com.mobsandgeeks.saripaar.ValidationContext;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class ConfirmPasswordRule extends ContextualAnnotationRule<ConfirmPassword, String> {

    protected ConfirmPasswordRule(ValidationContext validationContext,
            ConfirmPassword confirmPassword) {
        super(validationContext, confirmPassword);
    }

    @Override
    public boolean isValid(String confirmPassword) {
        List<View> passwordViews = mValidationContext.getAnnotatedViews(Password.class);
        int nPasswordViews = passwordViews.size();

        if (nPasswordViews == 0) {
            String message = String.format(
                    "You should have at least one view annotated with '%s' to use '%s'.",
                            Password.class.getName(), ConfirmPassword.class.getName());
            throw new IllegalStateException(message);
        } else if (nPasswordViews > 1) {
            String message = String.format(
                    "More than 1 field annotated with '%s'.", Password.class.getName());
            throw new IllegalStateException(message);
        }

        // There's only one, then we're good to go :)
        View view = passwordViews.get(0);
        Object password = mValidationContext.getData(view, Password.class);

        return confirmPassword.equals(password);
    }
}
