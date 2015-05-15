/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package commons.validator.routines;

import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * <p>Abstract class for Number Validation.</p>
 *
 * <p>This is a <i>base</i> class for building Number
 *    Validators using format parsing.</p>
 *
 * @version $Revision$
 * @since Validator 1.3.0
 */
public abstract class AbstractNumberValidator extends AbstractFormatValidator {

    private static final long serialVersionUID = -3088817875906765463L;

    /** Standard <code>NumberFormat</code> type */
    public static final int STANDARD_FORMAT = 0;

    /** Currency <code>NumberFormat</code> type */
    public static final int CURRENCY_FORMAT = 1;

    /** Percent <code>NumberFormat</code> type */
    public static final int PERCENT_FORMAT  = 2;

    private final boolean allowFractions;
    private final int     formatType;

    /**
     * Construct an instance with specified <i>strict</i>
     * and <i>decimal</i> parameters.
     *
     * @param strict <code>true</code> if strict
     *        <code>Format</code> parsing should be used.
     * @param formatType The <code>NumberFormat</code> type to
     *        create for validation, default is STANDARD_FORMAT.
     * @param allowFractions <code>true</code> if fractions are
     *        allowed or <code>false</code> if integers only.
     */
    public AbstractNumberValidator(boolean strict, int formatType, boolean allowFractions) {
        super(strict);
        this.allowFractions = allowFractions;
        this.formatType = formatType;
    }

    /**
     * <p>Indicates whether the number being validated is
     *    a decimal or integer.</p>
     *
     * @return <code>true</code> if decimals are allowed
     *       or <code>false</code> if the number is an integer.
     */
    public boolean isAllowFractions() {
        return allowFractions;
    }

    /**
     * <p>Indicates the type of <code>NumberFormat</code> created
     *    by this validator instance.</p>
     *
     * @return the format type created.
     */
    public int getFormatType() {
        return formatType;
    }

    /**
     * <p>Validate using the specified <code>Locale</code>.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the <code>Locale</code> if <code>null</code>.
     * @param locale The locale to use for the date format, system default if null.
     * @return <code>true</code> if the value is valid.
     */
    public boolean isValid(String value, String pattern, Locale locale) {
        Object parsedValue = parse(value, pattern, locale);
        return (parsedValue == null ? false : true);
    }

    /**
     * Check if the value is within a specified range.
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return <code>true</code> if the value is within the
     *         specified range.
     */
    public boolean isInRange(Number value, Number min, Number max) {
        return (minValue(value, min) && maxValue(value, max));
    }

    /**
     * Check if the value is greater than or equal to a minimum.
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value.
     * @return <code>true</code> if the value is greater than
     *         or equal to the minimum.
     */
    public boolean minValue(Number value, Number min) {
        if (isAllowFractions()) {
            return (value.doubleValue() >= min.doubleValue());
        }
        return (value.longValue() >= min.longValue());
    }

    /**
     * Check if the value is less than or equal to a maximum.
     *
     * @param value The value validation is being performed on.
     * @param max The maximum value.
     * @return <code>true</code> if the value is less than
     *         or equal to the maximum.
     */
    public boolean maxValue(Number value, Number max) {
        if (isAllowFractions()) {
            return (value.doubleValue() <= max.doubleValue());
        }
        return (value.longValue() <= max.longValue());
    }

    /**
     * <p>Parse the value using the specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the <code>Locale</code> if <code>null</code>.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object parse(String value, String pattern, Locale locale) {

        value = (value == null ? null : value.trim());
        if (value == null || value.length() == 0) {
            return null;
        }
        Format formatter = getFormat(pattern, locale);
        return parse(value, formatter);

    }

    /**
     * <p>Process the parsed value, performing any further validation
     *    and type conversion required.</p>
     *
     * @param value The parsed object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed value converted to the appropriate type
     *         if valid or <code>null</code> if invalid.
     */
    protected abstract Object processParsedValue(Object value, Format formatter);

    /**
     * <p>Returns a <code>NumberFormat</code> for the specified <i>pattern</i>
     *    and/or <code>Locale</code>.</p>
     *
     * @param pattern The pattern used to validate the value against or
     *        <code>null</code> to use the default for the <code>Locale</code>.
     * @param locale The locale to use for the currency format, system default if null.
     * @return The <code>NumberFormat</code> to created.
     */
    protected Format getFormat(String pattern, Locale locale) {

        NumberFormat formatter = null;
        boolean usePattern = (pattern != null && pattern.length() > 0);
        if (!usePattern) {
            formatter = (NumberFormat)getFormat(locale);
        } else if (locale == null) {
            formatter =  new DecimalFormat(pattern);
        } else {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
            formatter = new DecimalFormat(pattern, symbols);
        }

        if (determineScale(formatter) == 0) {
            formatter.setParseIntegerOnly(true);
        }
        return formatter;
    }

    /**
     * <p>Returns the <i>multiplier</i> of the <code>NumberFormat</code>.</p>
     *
     * @param format The <code>NumberFormat</code> to determine the
     *        multiplier of.
     * @return The multiplying factor for the format..
     */
    protected int determineScale(NumberFormat format) {
        if (!isStrict()) {
            return -1;
        }
        if (!isAllowFractions() || format.isParseIntegerOnly()) {
            return 0;
        }
        int minimumFraction = format.getMinimumFractionDigits();
        int maximumFraction = format.getMaximumFractionDigits();
        if (minimumFraction != maximumFraction) {
            return -1;
        }
        int scale = minimumFraction;
        if (format instanceof DecimalFormat) {
            int multiplier = ((DecimalFormat)format).getMultiplier();
            if (multiplier == 100) {
                scale += 2;
            } else if (multiplier == 1000) {
                scale += 3;
            }
        } else if (formatType == PERCENT_FORMAT) {
            scale += 2;
        }
        return scale;
    }

    /**
     * <p>Returns a <code>NumberFormat</code> for the specified Locale.</p>
     *
     * @param locale The locale a <code>NumberFormat</code> is required for,
     *   system default if null.
     * @return The <code>NumberFormat</code> to created.
     */
    protected Format getFormat(Locale locale) {
        NumberFormat formatter = null;
        switch (formatType) {
        case CURRENCY_FORMAT:
            if (locale == null) {
                formatter = NumberFormat.getCurrencyInstance();
            } else {
                formatter = NumberFormat.getCurrencyInstance(locale);
            }
            break;
        case PERCENT_FORMAT:
            if (locale == null) {
                formatter = NumberFormat.getPercentInstance();
            } else {
                formatter = NumberFormat.getPercentInstance(locale);
            }
            break;
        default:
            if (locale == null) {
                formatter = NumberFormat.getInstance();
            } else {
                formatter = NumberFormat.getInstance(locale);
            }
            break;
        }
        return formatter;
    }
}
