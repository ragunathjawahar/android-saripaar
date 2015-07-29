/*
 * Copyright (C) 2015 Mobs & Geeks
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

/**
 * A collection of the most commonly used date format constants.
 *
 * @author Ragunath Jawahar {@literal <rj@mobsandgeeks.com>}
 * @since 2.0
 */
public final class DateFormats {

    public static final String DMY = "dd-MM-yyyy";
    public static final String YMD = "yyyy-MM-dd";
    public static final String MDY = "MM-dd-yyyy";

    public static final String DMY_TIME_12_HOURS = "dd-MM-yyyy hh:mm aa";
    public static final String YMD_TIME_12_HOURS = "yyyy-MM-dd hh:mm aa";
    public static final String MDY_TIME_12_HOURS = "MM-dd-yyyy hh:mm aa";

    public static final String DMY_TIME_24_HOURS = "dd-MM-yyyy kk:mm";
    public static final String YMD_TIME_24_HOURS = "yyyy-MM-dd kk:mm";
    public static final String MDY_TIME_24_HOURS = "MM-dd-yyyy kk:mm";

    private DateFormats() {
    }
}
