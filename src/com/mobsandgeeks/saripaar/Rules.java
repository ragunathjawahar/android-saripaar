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

import android.inputmethodservice.ExtractEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * A built-in class with a collection of common rules. {@link TextView} references notable direct
 * and indirect subclasses that includes but not limited to {@link EditText},
 * {@link AutoCompleteTextView}, {@link ExtractEditText} and {@link MultiAutoCompleteTextView}.
 * {@link Checkable} references notable implementing classes but not limited to {@link CheckBox},
 * {@link CheckedTextView}, {@link RadioButton} and {@link ToggleButton}.
 *
 * You may use it with any custom {@link View}s you may define that extends or implements the above
 * mentioned classes and interfaces.
 *
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
public final class Rules {
    // Constants
    public static final String EMPTY_STRING = "";

    // Regular Expressions
    private static final String GOOD_IRI_CHAR =
            "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF"; // android.util.Patterns.java
    private static final String TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL =
            "(?:"
            + "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
            + "|(?:biz|b[abdefghijmnorstvwyz])"
            + "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])"
            + "|d[ejkmoz]"
            + "|(?:edu|e[cegrstu])"
            + "|f[ijkmor]"
            + "|(?:gov|g[abdefghilmnpqrstuwy])"
            + "|h[kmnrtu]"
            + "|(?:info|int|i[delmnoqrst])"
            + "|(?:jobs|j[emop])"
            + "|k[eghimnprwyz]"
            + "|l[abcikrstuvy]"
            + "|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])"
            + "|(?:name|net|n[acefgilopruz])"
            + "|(?:org|om)"
            + "|(?:pro|p[aefghklmnrstwy])"
            + "|qa"
            + "|r[eosuw]"
            + "|s[abcdeghijklmnortuvyz]"
            + "|(?:tel|travel|t[cdfghjklmnoprtvwz])"
            + "|u[agksyz]"
            + "|v[aceginu]"
            + "|w[fs]"
            + "|(?:xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-80akhbyknj4f|xn\\-\\-9t4b11yi5a|xn\\-\\-deba0ad|xn\\-\\-g6w251d|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-zckzah)"
            + "|y[etu]"
            + "|z[amw]))"; // android.util.Patterns.java

    public static final String REGEX_INTEGER = "\\d+";

    public static final String REGEX_DECIMAL = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";

    public static final String REGEX_EMAIL =
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
            + "\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"
            + "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"; // android.util.Patterns.java

    public static final String REGEX_IP_ADDRESS =
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
            + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
            + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
            + "|[1-9][0-9]|[0-9]))"; // android.util.Patterns.java

    public static final String REGEX_WEB_URL =
            "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
            + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
            + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
            + "((?:(?:[" + GOOD_IRI_CHAR + "][" + GOOD_IRI_CHAR + "\\-]{0,64}\\.)+"   // named host
            + TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL
            + "|(?:(?:25[0-5]|2[0-4]" // or ip address
            + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
            + "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
            + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
            + "|[1-9][0-9]|[0-9])))"
            + "(?:\\:\\d{1,5})?)" // plus option port number
            + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
            + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
            + "(?:\\b|$)"; // android.util.Patterns.java

    /**
     * The classical required {@link Rule}. Checks if the {@link TextView} or its subclass
     * {@link View}'s displayed text is not empty.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param trimInput Specifies whether to trim the text returned by {@code getText()}.
     *
     * @return True if the {@link View} is not empty, false otherwise. The return value is affected
     * by the {@code trimInput} parameter.
     */
    public static Rule<TextView> required(final String failureMessage, final boolean trimInput) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView textView) {
                return !TextUtils.isEmpty(getText(textView, trimInput));
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text matches the
     * given regular expression.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param regex Regular expression pattern to be matched against the text returned by
     *          {@code getText()}.
     * @param trimInput Specifies whether to trim the text returned by {@code getText()}.
     *
     * @throws IllegalArgumentException If {@code regex} is {@code null}.
     *
     * @return True if the text matches the regular expression. The return value is affected
     * by the {@code trimInput} parameter.
     */
    public static Rule<TextView> regex(final String failureMessage, final String regex, 
            final boolean trimInput) {
        if (regex == null) {
            throw new IllegalArgumentException("\'regex\' cannot be null");
        }

        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView textView) {
                String text = getText(textView, trimInput);
                return text != null ? text.matches(regex) : false;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text has at least the
     * minimum number of characters specified by this {@link Rule}.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param minLength Minimum number of characters required in the text returned by
     *          {@code getText()}. The returned text is affected by the {@code trimInput}
     *          parameter.
     * @param trimInput Specifies whether to trim the text returned by {@code getText()}.
     * @param required Determine if an empty content will be allowed (when {@code false})
     *
     * @return True if the text has the minimum number of characters specified, false otherwise.
     */
    public static Rule<TextView> minLength(final String failureMessage, final int minLength,
            final boolean trimInput, final boolean required) {

        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView view) {
                String text = getText(view, trimInput);

                if (text != null && text.length() > 0) {
                    return text.length() >= minLength;
                }

                // Empty text, return false if the field is required
                return !required;
            }
        };
    }

    /**
     * Specifies the  {@link TextView} should have a min length and is required.
     * @see #minLength(String, int, boolean, boolean)
     */
    public static Rule<TextView> minLength(final String failureMessage, final int minLength,
                                           final boolean trimInput) {
        return minLength(failureMessage, minLength, trimInput, true);
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text's length is less
     * than or equal to the maximum number of characters specified by this {@link Rule}.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param maxLength Maximum number of characters allowed in the text returned by
     *          {@code getText()}. The returned text is affected by the {@code trimInput}
     *          parameter.
     * @param trimInput Specifies whether to trim the text returned by {@code getText()}.
     *
     * @return True if the text length is less than or equal to the maximum number of characters
     *          specified, false otherwise.
     */
    public static Rule<TextView> maxLength(final String failureMessage, final int maxLength,
            final boolean trimInput) {

        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView view) {
                String text = getText(view, trimInput);
                return text != null ? text.length() <= maxLength : false;
            }
        };
    }

    /**
     * Checks if the contents of two {@link TextView}s are equal. Ideal for password and confirm
     * password.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param anotherTextView The {@link TextView} whose contents have to be checked against the
     *          {@link TextView} that is being validated.
     *
     * @throws IllegalArgumentException If {@code anotherTextView} is {@code null}.
     *
     * @return True if both the {@link TextView} contents are equal.
     */
    public static Rule<TextView> eq(final String failureMessage, final TextView anotherTextView) {
        if (anotherTextView == null) {
            throw new IllegalArgumentException("\'anotherTextView\' cannot be null");
        }

        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView view) {
                return view.getText().toString().equals(anotherTextView.getText().toString());
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value equals
     * the given {@link String} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param expectedString {@link String} value to be compared with the text returned by
     *          {@code getText()}. {@code null} is treated as empty {@link String}.
     *
     * @return True if the text matches the {@code expectedString} value, false otherwise.
     */
    public static Rule<TextView> eq(final String failureMessage, final String expectedString) {
        return eq(failureMessage, expectedString, false, false);
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value equals
     * the given {@link String} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param expectedString {@link String} value to be compared with the text returned by
     *          {@code getText()}. {@code null} is treated as empty {@link String}.
     * @param ignoreCase Specifies whether the text's case differences should be ignored.
     * @param trimInput Specifies whether to trim the text returned by {@code getText()}.
     *
     * @return True if the text matches the {@code expectedString} value, false otherwise. The
     *          return value is affected by {@code ignoreCase} and {@code trimInput}
     *          parameters.
     */
    public static Rule<TextView> eq(final String failureMessage, final String expectedString,
            final boolean ignoreCase, final boolean trimInput) {
        final String cleanString = expectedString == null ? EMPTY_STRING : expectedString;

        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView textView) {
                boolean valid = false;
                String actualString = getText(textView, trimInput);
                if (actualString != null) {
                    valid = ignoreCase ?
                            actualString.equalsIgnoreCase(cleanString) :
                                actualString.equals(cleanString);
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value equals
     * the specified {@code int} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param expectedInt {@code int} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is equal to the {@code expectedInt} value.
     */
    public static Rule<TextView> eq(final String failureMessage, final int expectedInt) {
        return eq(failureMessage, (long) expectedInt);
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value is
     * greater than the specified {@code int} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param lesserInt {@code int} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is greater to the {@code expectedInt} value.
     */
    public static Rule<TextView> gt(final String failureMessage, final int lesserInt) {
        return gt(failureMessage, (long) lesserInt);
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value is less 
     * than the specified {@code int} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param greaterInt {@code int} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is less than the {@code expectedInt} value.
     */
    public static Rule<TextView> lt(final String failureMessage, final int greaterInt) {
        return lt(failureMessage, (long) greaterInt);
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value equals
     * the specified {@code long} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param expectedLong {@code long} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is equal to the {@code expectedLong} value.
     */
    public static Rule<TextView> eq(final String failureMessage, final long expectedLong) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView textView) {
                boolean valid = false;
                String actualLong = getText(textView, true);
                if (actualLong != null) {
                    valid = actualLong.matches(REGEX_INTEGER) ?
                            Long.parseLong(actualLong) == expectedLong : false;
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value is
     * greater than the specified {@code long} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param lesserLong {@code long} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is greater than the {@code expectedLong} value.
     */
    public static Rule<TextView> gt(final String failureMessage, final long lesserLong) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView textView) {
                boolean valid = false;
                String actualLong = getText(textView, true);
                if (actualLong != null) {
                    valid = actualLong.matches(REGEX_INTEGER) ?
                            Long.parseLong(actualLong) > lesserLong : false;
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value is less
     * than the specified {@code long} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param greaterLong {@code long} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is less than the {@code expectedLong} value.
     */
    public static Rule<TextView> lt(final String failureMessage, final long greaterLong) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView textView) {
                boolean valid = false;
                String actualLong = getText(textView, true);
                if (actualLong != null) {
                    valid = actualLong.matches(REGEX_INTEGER) ?
                            Long.parseLong(actualLong) < greaterLong : false;
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value equals
     * the specified {@code float} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param expectedFloat {@code float} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is equal to the {@code expectedFloat} value.
     */
    public static Rule<TextView> eq(final String failureMessage, final float expectedFloat) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView view) {
                boolean valid = false;
                String actualFloat = getText(view, true);
                if (actualFloat != null) {
                    valid = actualFloat.matches(REGEX_DECIMAL) ?
                            Float.parseFloat(actualFloat) == expectedFloat : false;
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value is
     * greater than the specified {@code float} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param lesserFloat {@code float} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is equal to the {@code expectedFloat} value.
     */
    public static Rule<TextView> gt(final String failureMessage, final float lesserFloat) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView view) {
                boolean valid = false;
                String actualFloat = getText(view, true);
                if (actualFloat != null) {
                    valid = actualFloat.matches(REGEX_DECIMAL) ?
                            Float.parseFloat(actualFloat) > lesserFloat : false;
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value is
     * less than the specified {@code float} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param greaterFloat {@code float} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is less than the {@code expectedFloat} value.
     */
    public static Rule<TextView> lt(final String failureMessage, final float greaterFloat) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView view) {
                boolean valid = false;
                String actualFloat = getText(view, true);
                if (actualFloat != null) {
                    valid = actualFloat.matches(REGEX_DECIMAL) ?
                            Float.parseFloat(actualFloat) < greaterFloat : false;
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value equals
     * the specified {@code double} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param expectedDouble {@code double} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is equal to the {@code expectedDouble} value.
     */
    public static Rule<TextView> eq(final String failureMessage, final double expectedDouble) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView view) {
                boolean valid = false;
                String actualDouble = getText(view, true);
                if (actualDouble != null) {
                    valid = actualDouble.matches(REGEX_DECIMAL) ?
                            Double.parseDouble(actualDouble) == expectedDouble : false;
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value is
     * greater than the specified {@code double} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param lesserDouble {@code double} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is greater than the {@code expectedDouble} value.
     */
    public static Rule<TextView> gt(final String failureMessage, final double lesserDouble) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView view) {
                boolean valid = false;
                String actualDouble = getText(view, true);
                if (actualDouble != null) {
                    valid = actualDouble.matches(REGEX_DECIMAL) ?
                            Double.parseDouble(actualDouble) > lesserDouble : false;
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link TextView} or its subclass {@link View}'s displayed text value is
     * less than the specified {@code double} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param greaterDouble {@code double} value to be compared with the text returned by
     *          {@code getText()}.
     *
     * @return True if the input text is less than the {@code expectedDouble} value.
     */
    public static Rule<TextView> lt(final String failureMessage, final double greaterDouble) {
        return new Rule<TextView>(failureMessage) {

            @Override
            public boolean isValid(TextView view) {
                boolean valid = false;
                String actualDouble = getText(view, true);
                if (actualDouble != null) {
                    valid = actualDouble.matches(REGEX_DECIMAL) ?
                            Double.parseDouble(actualDouble) < greaterDouble : false;
                }

                return valid;
            }
        };
    }

    /**
     * Checks if the {@link Checkable} or its subclass {@link View}'s state is same as the state
     * specified.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param checked The expected state of the {@link Checkable} widget.
     *
     * @return True if the state is same as {@code checked}.
     */
    public static Rule<Checkable> checked(final String failureMessage,
            final boolean checked) {
        return new Rule<Checkable>(failureMessage) {

            @Override
            public boolean isValid(Checkable view) {
                return view.isChecked() == checked;
            }
        };
    }

    /**
     * Checks if the {@link Spinner}'s selected item's {@link String} value (obtained by calling
     * {@code toString()} on the selected item) equals the expected {@link String} value.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param expectedString {@link String} value to be compared with the text returned by
     *          calling {@code toString()} on the selected {@link Spinner} item.
     * @param ignoreCase Specifies whether the text's case differences should be ignored.
     * @param trimInput Specifies whether to trim the String returned by {@code toString()}
     *          on the selected item.
     *
     * @return True if both the {@link String} values are equal.
     */
    public static Rule<Spinner> spinnerEq(final String failureMessage,
            final String expectedString, final boolean ignoreCase, final boolean trimInput) {

        return new Rule<Spinner>(failureMessage) {

            @Override
            public boolean isValid(Spinner spinner) {
                boolean equals = false;
                Object selectedItem = spinner.getSelectedItem();
                if (expectedString == null && selectedItem == null) {
                    equals = true;
                } else if (expectedString != null && selectedItem != null) {
                    String selectedItemString = selectedItem.toString();
                    selectedItemString = trimInput ? selectedItemString.trim() : selectedItemString;

                    equals = ignoreCase ?
                            selectedItemString.equalsIgnoreCase(expectedString) :
                                selectedItemString.equals(expectedString);
                }

                return equals;
            }
        };
    }

    /**
     * Checks if the {@link Spinner}'s selected item's position (obtained by calling
     * {@code getSelectionItemPosition()}) equals the expected selection index.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param expectedPosition The position to be compared with the position returned by
     *          calling {@code getSelectedItemPosition()} on the {@link Spinner}.
     *
     * @return True if both the {@link String} values are equal.
     */
    public static Rule<Spinner> spinnerEq(final String failureMessage, final int expectedPosition) {

        return new Rule<Spinner>(failureMessage) {

            @Override
            public boolean isValid(Spinner spinner) {
                return spinner.getSelectedItemPosition() == expectedPosition;
            }
        };
    }

    /**
     * Checks if the {@link Spinner}'s item selection (obtained by calling
     * {@code getSelectionItemPosition()}) is not equal to the specified selection.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param selection The unexpected selection on the {@link Spinner}.
     *
     * @return {@code true} if the selection is not equal to the given position, 
     *         {@code false} otherwise. 
     */
    public static Rule<Spinner> spinnerNotEq(final String failureMessage, final int selection) {

        return new Rule<Spinner>(failureMessage) {

            @Override
            public boolean isValid(Spinner spinner) {
                return spinner.getSelectedItemPosition() != selection;
            }
        };
    }

    /**
     * Performs an '&&' (and) operation on the given array of {@link Rules}.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param rules An array of {@link Rule}s on which the '&&' (and) operation is to be performed.
     *
     * @return True if all {@link Rule}s are valid.
     */
    public static Rule<View> and(final String failureMessage, final Rule<?>... rules) {
        return new Rule<View>(failureMessage) {

            @Override
            public boolean isValid(View view) {
                boolean valid = true;
                for(Rule rule : rules) {
                    if (rule != null) valid &= rule.isValid(view);
                    if (!valid) break;
                }

                return valid;
            }
        };
    }

    /**
     * Performs a '||' (or) operation on the given array of {@link Rules}.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param rules An array of {@link Rule}s on which the '||' (or) operation is to be performed.
     *
     * @return True if at least one of the {@link Rule}s is valid.
     */
    public static Rule<View> or(final String failureMessage, final Rule<?>... rules) {
        return new Rule<View>(failureMessage) {

            @Override
            public boolean isValid(View view) {
                boolean valid = false;
                for(Rule rule : rules) {
                    if (rule != null) valid |= rule.isValid(view);
                    if (valid) break;
                }

                return valid;
            }
        };
    }

    /**
     * Unlike the other rules, this one performs an '&&' (and) operation on several
     * {@link View}s.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param viewsAndRules A {@link LinkedHashMap} containing rules for different {@link View}s.
     *
     * @return True if all {@link Rule}s are valid.
     */
    public static Rule<View> compositeAnd(final String failureMessage,
            final LinkedHashMap<View, Rule<?>> viewsAndRules) {

        return new Rule<View>(failureMessage) {

            @Override
            public boolean isValid(View view) {
                boolean valid = true;

                Set<View> keySet = viewsAndRules.keySet();
                for (View viewKey : keySet) {
                    Rule rule = viewsAndRules.get(viewKey);
                    valid &= rule.isValid(view);
                    if (!valid) break;
                }

                return valid;
            }
        };
    }

    /**
     * Unlike the other rules, this one performs a '||' (or) operation on several
     * {@link View}s.
     *
     * @param failureMessage The failure message for this {@link Rule}.
     * @param viewsAndRules A {@link LinkedHashMap} containing rules for different {@link View}s.
     *
     * @return True if at least one of the {@link Rule}s is valid.
     */
    public static Rule<View> compositeOr(final String failureMessage,
            final LinkedHashMap<View, Rule<?>> viewsAndRules) {

        return new Rule<View>(failureMessage) {

            @Override
            public boolean isValid(View view) {
                boolean valid = false;

                Set<View> keySet = viewsAndRules.keySet();
                for (View viewKey : keySet) {
                    Rule rule = viewsAndRules.get(viewKey);
                    valid |= rule.isValid(viewKey);
                    if (valid) break;
                }

                return valid;
            }
        };
    }

    private static String getText(final TextView textView, final boolean trim) {
        CharSequence text = null;
        if (textView != null) {
            text = textView.getText();
            text = trim ? text.toString().trim() : text;
        }

        return text != null ? text.toString() : null;
    }

}
