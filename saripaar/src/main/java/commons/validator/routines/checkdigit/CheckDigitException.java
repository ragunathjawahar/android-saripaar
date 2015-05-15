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
 * Check Digit calculation/validation error.
 *
 * @since Validator 1.4
 */
public class CheckDigitException extends Exception {

    private static final long serialVersionUID = -3519894732624685477L;

    /**
     * Construct an Exception with no message.
     */
    public CheckDigitException() {
    }

    /**
     * Construct an Exception with a message.
     *
     * @param msg The error message.
     */
    public CheckDigitException(String msg) {
        super(msg);
    }

    /**
     * Construct an Exception with a message and
     * the underlying cause.
     *
     * @param msg The error message.
     * @param cause The underlying cause of the error
     */
    public CheckDigitException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
