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

/**
 * Modulus 10 <b>Luhn</b> Check Digit calculation/validation.
 *
 * Luhn check digits are used, for example, by:
 * <ul>
 *    <li><a href="http://en.wikipedia.org/wiki/Credit_card">Credit Card Numbers</a></li>
 *    <li><a href="http://en.wikipedia.org/wiki/IMEI">IMEI Numbers</a> - International
 *        Mobile Equipment Identity Numbers</li>
 * </ul>
 * Check digit calculation is based on <i>modulus 10</i> with digits in
 * an <i>odd</i> position (from right to left) being weighted 1 and <i>even</i>
 * position digits being weighted 2 (weighted values greater than 9 have 9 subtracted).
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Luhn_algorithm">Wikipedia</a>
 * for more details.
 * </p>
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public final class LuhnCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -2976900113942875999L;

    /** Singleton Luhn Check Digit instance */
    public static final CheckDigit LUHN_CHECK_DIGIT = new LuhnCheckDigit();

    /** weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = new int[] {2, 1};

    /**
     * Construct a modulus 10 Luhn Check Digit routine.
     */
    public LuhnCheckDigit() {
        super(10);
    }

    /**
     * <p>Calculates the <i>weighted</i> value of a charcter in the
     * code at a specified position.</p>
     *
     * <p>For Luhn (from right to left) <b>odd</b> digits are weighted
     * with a factor of <b>one</b> and <b>even</b> digits with a factor
     * of <b>two</b>. Weighted values &gt; 9, have 9 subtracted</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int weight = POSITION_WEIGHT[rightPos % 2];
        int weightedValue = charValue * weight;
        return weightedValue > 9 ? (weightedValue - 9) : weightedValue;
    }
}
