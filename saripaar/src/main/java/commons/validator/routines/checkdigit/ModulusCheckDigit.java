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
package commons.validator.routines.checkdigit;

import java.io.Serializable;

/**
 * Abstract <b>Modulus</b> Check digit calculation/validation.
 * <p>
 * Provides a <i>base</i> class for building <i>modulus</i> Check
 * Digit routines.
 * <p>
 * This implementation only handles <i>single-digit numeric</i> codes, such as
 * <b>EAN-13</b>. For <i>alphanumeric</i> codes such as <b>EAN-128</b> you
 * will need to implement/override the <code>toInt()</code> and
 * <code>toChar()</code> methods.
 * <p>
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public abstract class ModulusCheckDigit implements CheckDigit, Serializable {

    private static final long serialVersionUID = 2948962251251528941L;

    // N.B. The modulus can be > 10 provided that the implementing class overrides toCheckDigit and toInt
    // (for example as in ISBN10CheckDigit)
    private final int modulus;

    /**
     * Construct a {@link CheckDigit} routine for a specified modulus.
     *
     * @param modulus The modulus value to use for the check digit calculation
     */
    public ModulusCheckDigit(int modulus) {
        this.modulus = modulus;
    }

    /**
     * Return the modulus value this check digit routine is based on.
     *
     * @return The modulus value this check digit routine is based on
     */
    public int getModulus() {
        return modulus;
    }

    /**
     * Validate a modulus check digit for a code.
     *
     * @param code The code to validate
     * @return <code>true</code> if the check digit is valid, otherwise
     * <code>false</code>
     */
    public boolean isValid(String code) {
        if (code == null || code.length() == 0) {
            return false;
        }
        try {
            int modulusResult = calculateModulus(code, true);
            return (modulusResult == 0);
        } catch (CheckDigitException  ex) {
            return false;
        }
    }

    /**
     * Calculate a modulus <i>Check Digit</i> for a code.
     *
     * @param code The code to calculate the Check Digit for
     * @return The calculated Check Digit
     * @throws CheckDigitException if an error occurs calculating
     * the check digit for the specified code
     */
    public String calculate(String code) throws CheckDigitException {
        if (code == null || code.length() == 0) {
            throw new CheckDigitException("Code is missing");
        }
        int modulusResult = calculateModulus(code, false);
        int charValue = (modulus - modulusResult) % modulus;
        return toCheckDigit(charValue);
    }

    /**
     * Calculate the modulus for a code.
     *
     * @param code The code to calculate the modulus for.
     * @param includesCheckDigit Whether the code includes the Check Digit or not.
     * @return The modulus value
     * @throws CheckDigitException if an error occurs calculating the modulus
     * for the specified code
     */
    protected int calculateModulus(String code, boolean includesCheckDigit) throws CheckDigitException {
        int total = 0;
        for (int i = 0; i < code.length(); i++) {
            int lth = code.length() + (includesCheckDigit ? 0 : 1);
            int leftPos  = i + 1;
            int rightPos = lth - i;
            int charValue = toInt(code.charAt(i), leftPos, rightPos);
            total += weightedValue(charValue, leftPos, rightPos);
        }
        if (total == 0) {
            throw new CheckDigitException("Invalid code, sum is zero");
        }
        return total % modulus;
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     * <p>
     * Some modulus routines weight the value of a character
     * depending on its position in the code (e.g. ISBN-10), while
     * others use different weighting factors for odd/even positions
     * (e.g. EAN or Luhn). Implement the appropriate mechanism
     * required by overriding this method.
     *
     * @param charValue The numeric value of the character
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character
     * @throws CheckDigitException if an error occurs calculating
     * the weighted value
     */
    protected abstract int weightedValue(int charValue, int leftPos, int rightPos)
            throws CheckDigitException;


    /**
     * Convert a character at a specified position to an integer value.
     * <p>
     * <b>Note:</b> this implementation only handlers numeric values
     * For non-numeric characters, override this method to provide
     * character--&gt;integer conversion.
     *
     * @param character The character to convert
     * @param leftPos The position of the character in the code, counting from left to right (for identifiying the position in the string)
     * @param rightPos The position of the character in the code, counting from right to left (not used here)
     * @return The integer value of the character
     * @throws CheckDigitException if character is non-numeric
     */
    protected int toInt(char character, int leftPos, int rightPos)
            throws CheckDigitException {
        if (Character.isDigit(character)) {
            return Character.getNumericValue(character);
        }
        throw new CheckDigitException("Invalid Character[" +
                leftPos + "] = '" + character + "'");
    }

    /**
     * Convert an integer value to a check digit.
     * <p>
     * <b>Note:</b> this implementation only handles single-digit numeric values
     * For non-numeric characters, override this method to provide
     * integer--&gt;character conversion.
     *
     * @param charValue The integer value of the character
     * @return The converted character
     * @throws CheckDigitException if integer character value
     * doesn't represent a numeric character
     */
    protected String toCheckDigit(int charValue)
            throws CheckDigitException {
        if (charValue >= 0 && charValue <= 9) {
            return Integer.toString(charValue);
        }
        throw new CheckDigitException("Invalid Check Digit Value =" +
                + charValue);
    }

    /**
     * Add together the individual digits in a number.
     *
     * @param number The number whose digits are to be added
     * @return The sum of the digits
     */
    public static int sumDigits(int number) {
        int total = 0;
        int todo = number;
        while (todo > 0) {
            total += todo % 10;
            todo  = todo / 10;
        }
        return total;
    }

}
