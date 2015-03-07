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

import com.mobsandgeeks.saripaar.AnnotationRule;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public class PasswordRule extends AnnotationRule<Password, String> {

    /*
     * http://stackoverflow.com/questions/1559751/
     * regex-to-make-sure-that-the-string-contains-at-least-one-lower-case-char-upper
     */
    private final Map<Password.Scheme, String> SCHEME_PATTERNS =
            new HashMap<Password.Scheme, String>() {{
                put(Password.Scheme.ANY, ".+");
                put(Password.Scheme.ALPHA, "\\w+");
                put(Password.Scheme.ALPHA_MIXED_CASE, "(?=.*[a-z])(?=.*[A-Z]).+");
                put(Password.Scheme.NUMERIC, "\\d+");
                put(Password.Scheme.ALPHA_NUMERIC, "(?=.*[a-zA-Z])(?=.*[\\d]).+");
                put(Password.Scheme.ALPHA_NUMERIC_MIXED_CASE,
                    "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]).+");
                put(Password.Scheme.ALPHA_NUMERIC_SYMBOLS,
                    "(?=.*[a-zA-Z])(?=.*[\\d])(?=.*([^\\w])).+");
                put(Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS,
                    "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*([^\\w])).+");
            }};

    protected PasswordRule(final Password password) {
        super(password);
    }

    @Override
    public boolean isValid(final String password) {
        boolean hasMinChars = password.length() >= mRuleAnnotation.min();
        boolean matchesScheme = password.matches(SCHEME_PATTERNS.get(mRuleAnnotation.scheme()));
        return hasMinChars && matchesScheme;
    }
}
