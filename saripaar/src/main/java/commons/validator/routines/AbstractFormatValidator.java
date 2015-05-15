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

import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;
import java.io.Serializable;

/**
 * <p>Abstract class for <i>Format</i> based Validation.</p>
 *
 * <p>This is a <i>base</i> class for building Date and Number
 *    Validators using format parsing.</p>
 *
 * @version $Revision$
 * @since Validator 1.3.0
 */
public abstract class AbstractFormatValidator implements Serializable {

    private static final long serialVersionUID = -4690687565200568258L;

    private final boolean strict;

    /**
     * Construct an instance with the specified strict setting.
     *
     * @param strict <code>true</code> if strict
     *        <code>Format</code> parsing should be used.
     */
    public AbstractFormatValidator(boolean strict) {
        this.strict = strict;
    }

    /**
     * <p>Indicates whether validated values should adhere
     *    strictly to the <code>Format</code> used.</p>
     *
     * <p>Typically implementations of <code>Format</code>
     *    ignore invalid characters at the end of the value
     *    and just stop parsing. For example parsing a date
     *    value of <code>01/01/20x0</code> using a pattern
     *    of <code>dd/MM/yyyy</code> will result in a year
     *    of <code>20</code> if <code>strict</code> is set
     *    to <code>false</code>, whereas setting <code>strict</code>
     *    to <code>true</code> will cause this value to fail
     *    validation.</p>
     *
     * @return <code>true</code> if strict <code>Format</code>
     *         parsing should be used.
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * <p>Validate using the default <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @return <code>true</code> if the value is valid.
     */
    public boolean isValid(String value) {
        return isValid(value, (String)null, (Locale)null);
    }

    /**
     * <p>Validate using the specified <i>pattern</i>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return <code>true</code> if the value is valid.
     */
    public boolean isValid(String value, String pattern) {
        return isValid(value, pattern, (Locale)null);
    }

    /**
     * <p>Validate using the specified <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the Format, defaults to the default
     * @return <code>true</code> if the value is valid.
     */
    public boolean isValid(String value, Locale locale) {
        return isValid(value, (String)null, locale);
    }

    /**
     * <p>Validate using the specified pattern and/or <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param locale The locale to use for the Format, defaults to the default
     * @return <code>true</code> if the value is valid.
     */
    public abstract boolean isValid(String value, String pattern, Locale locale);

    /**
     * <p>Format an object into a <code>String</code> using
     * the default Locale.</p>
     *
     * @param value The value validation is being performed on.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(Object value) {
        return format(value, (String)null, (Locale)null);
    }

    /**
     * <p>Format an object into a <code>String</code> using
     * the specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(Object value, String pattern) {
        return format(value, pattern, (Locale)null);
    }

    /**
     * <p>Format an object into a <code>String</code> using
     * the specified Locale.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the Format.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(Object value, Locale locale) {
        return format(value, (String)null, locale);
    }

    /**
     * <p>Format an object using the specified pattern and/or
     *    <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param locale The locale to use for the Format.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(Object value, String pattern, Locale locale) {
        Format formatter = getFormat(pattern, locale);
        return format(value, formatter);
    }

    /**
     * <p>Format a value with the specified <code>Format</code>.</p>
     *
     * @param value The value to be formatted.
     * @param formatter The Format to use.
     * @return The formatted value.
     */
    protected String format(Object value, Format formatter) {
        return formatter.format(value);
    }

    /**
     * <p>Parse the value with the specified <code>Format</code>.</p>
     *
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object parse(String value, Format formatter) {

        ParsePosition pos = new ParsePosition(0);
        Object parsedValue = formatter.parseObject(value, pos);
        if (pos.getErrorIndex() > -1) {
            return null;
        }

        if (isStrict() && pos.getIndex() < value.length()) {
            return null;
        }

        if (parsedValue != null) {
            parsedValue = processParsedValue(parsedValue, formatter);
        }

        return parsedValue;

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
     * <p>Returns a <code>Format</code> for the specified <i>pattern</i>
     *    and/or <code>Locale</code>.</p>
     *
     * @param pattern The pattern used to validate the value against or
     *        <code>null</code> to use the default for the <code>Locale</code>.
     * @param locale The locale to use for the currency format, system default if null.
     * @return The <code>NumberFormat</code> to created.
     */
    protected abstract Format getFormat(String pattern, Locale locale);

}
