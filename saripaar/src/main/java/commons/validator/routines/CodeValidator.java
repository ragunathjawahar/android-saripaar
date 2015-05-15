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

import java.io.Serializable;

import commons.validator.routines.checkdigit.CheckDigit;

/**
 * Generic <b>Code Validation</b> providing format, minimum/maximum
 * length and {@link CheckDigit} validations.
 * <p>
 * Performs the following validations on a code:
 * <ul>
 *   <li>Check the <i>format</i> of the code using a <i>regular expression.</i> (if specified)</li>
 *   <li>Check the <i>minimum</i> and <i>maximum</i> length  (if specified) of the <i>parsed</i> code
 *      (i.e. parsed by the <i>regular expression</i>).</li>
 *   <li>Performs {@link CheckDigit} validation on the parsed code (if specified).</li>
 * </ul>
 * <p>
 * Configure the validator with the appropriate regular expression, minimum/maximum length
 * and {@link CheckDigit} validator and then call one of the two validation
 * methods provided:</p>
 *    <ul>
 *       <li><code>boolean isValid(code)</code></li>
 *       <li><code>String validate(code)</code></li>
 *    </ul>
 * <p>
 * Codes often include <i>format</i> characters - such as hyphens - to make them
 * more easily human readable. These can be removed prior to length and check digit
 * validation by  specifying them as a <i>non-capturing</i> group in the regular
 * expression (i.e. use the <code>(?:   )</code> notation).
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public final class CodeValidator implements Serializable {

    private static final long serialVersionUID = 446960910870938233L;

    private final RegexValidator regexValidator;
    private final int minLength;
    private final int maxLength;
    private final CheckDigit checkdigit;

    /**
     * Construct a code validator with a specified regular
     * expression and {@link CheckDigit}.
     *
     * @param regex The format regular expression
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(String regex, CheckDigit checkdigit) {
        this(regex, -1, -1, checkdigit);
    }

    /**
     * Construct a code validator with a specified regular
     * expression, length and {@link CheckDigit}.
     *
     * @param regex The format regular expression.
     * @param length The length of the code
     *  (sets the mimimum/maximum to the same)
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(String regex, int length, CheckDigit checkdigit) {
        this(regex, length, length, checkdigit);
    }

    /**
     * Construct a code validator with a specified regular
     * expression, minimum/maximum length and {@link CheckDigit} validation.
     *
     * @param regex The regular expression validator
     * @param minLength The minimum length of the code
     * @param maxLength The maximum length of the code
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(String regex, int minLength, int maxLength,
            CheckDigit checkdigit) {
        if (regex != null && regex.length() > 0) {
            this.regexValidator = new RegexValidator(regex);
        } else {
            this.regexValidator = null;
        }
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.checkdigit = checkdigit;
    }

    /**
     * Construct a code validator with a specified regular expression,
     * validator and {@link CheckDigit} validation.
     *
     * @param regexValidator The format regular expression validator
     * @param checkdigit The check digit validation routine.
     */
    public CodeValidator(RegexValidator regexValidator, CheckDigit checkdigit) {
        this(regexValidator, -1, -1, checkdigit);
    }

    /**
     * Construct a code validator with a specified regular expression,
     * validator, length and {@link CheckDigit} validation.
     *
     * @param regexValidator The format regular expression validator
     * @param length The length of the code
     *  (sets the mimimum/maximum to the same value)
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(RegexValidator regexValidator, int length, CheckDigit checkdigit) {
        this(regexValidator, length, length, checkdigit);
    }

    /**
     * Construct a code validator with a specified regular expression
     * validator, minimum/maximum length and {@link CheckDigit} validation.
     *
     * @param regexValidator The format regular expression validator
     * @param minLength The minimum length of the code
     * @param maxLength The maximum length of the code
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(RegexValidator regexValidator, int minLength, int maxLength,
            CheckDigit checkdigit) {
        this.regexValidator = regexValidator;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.checkdigit = checkdigit;
    }

    /**
     * Return the check digit validation routine.
     * <p>
     * <b>N.B.</b> Optional, if not set no Check Digit
     * validation will be performed on the code.
     *
     * @return The check digit validation routine
     */
    public CheckDigit getCheckDigit() {
        return checkdigit;
    }

    /**
     * Return the minimum length of the code.
     * <p>
     * <b>N.B.</b> Optional, if less than zero the
     * minimum length will not be checked.
     *
     * @return The minimum length of the code or
     * <code>-1</code> if the code has no minimum length
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Return the maximum length of the code.
     * <p>
     * <b>N.B.</b> Optional, if less than zero the
     * maximum length will not be checked.
     *
     * @return The maximum length of the code or
     * <code>-1</code> if the code has no maximum length
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Return the <i>regular expression</i> validator.
     * <p>
     * <b>N.B.</b> Optional, if not set no regular
     * expression validation will be performed on the code.
     *
     * @return The regular expression validator
     */
    public RegexValidator getRegexValidator() {
        return regexValidator;
    }

    /**
     * Validate the code returning either <code>true</code>
     * or <code>false</code>.
     *
     * @param input The code to validate
     * @return <code>true</code> if valid, otherwise
     * <code>false</code>
     */
    public boolean isValid(String input) {
        return (validate(input) != null);
    }

    /**
     * Validate the code returning either the valid code or
     * <code>null</code> if invalid.
     *
     * @param input The code to validate
     * @return The code if valid, otherwise <code>null</code>
     * if invalid
     */
    public Object validate(String input) {

        if (input == null) {
            return null;
        }

        String code = input.trim();
        if (code.length() == 0) {
            return null;
        }

        // validate/reformat using regular expression
        if (regexValidator != null) {
            code = regexValidator.validate(code);
            if (code == null) {
                return null;
            }
        }

        // check the length
        if ((minLength >= 0 && code.length() < minLength) ||
            (maxLength >= 0 && code.length() > maxLength)) {
            return null;
        }

        // validate the check digit
        if (checkdigit != null && !checkdigit.isValid(code)) {
            return null;
        }

        return code;

    }

}
