/**
 * Copyright (c) 2007-2015, Custom Space User Management Plugin Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Custom Space User Management Plugin Development Team
 *       nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package csum.confluence.permissionmgmt.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Gary S. Weaver
 */
public class ConfigUtil {

    private static final Log log = LogFactory.getLog(ConfigUtil.class);

    public static String getTrimmedStringOrNull(String value) {
        String result = value;
        if (result != null) {
            result = value.trim();
        }

        return result;
    }

    public static String getTrimmedStringOrUseDefaultIfValueIsNullOrTrimmedValueIsEmpty(String name, String value, String defaultValue) {
        String result = defaultValue;
        if (value != null) {
            result = value.trim();
            if ("".equals(value)) {
                result = defaultValue;
            }
        }

        return result;
    }

    public static int getIntOrUseDefaultIfNullOrTrimmedValueIsEmptyOrNotAnInteger(String name, String value, int defaultValue) {
        int result = defaultValue;
        if (value != null) {
            value = value.trim();
            try {
                result = Integer.parseInt(value);
            }
            catch (NumberFormatException nfe) {
                log.debug("Could not parse " + name + " value of '" + value +
                        "'. Using default value " + defaultValue);
            }
        }

        return result;
    }
    
    public static int getIntOrUseDefaultIfNullOrTrimmedValueIsEmptyOrNotAnIntegerOrUseRangeMinOrMaxIfOutOfRange(String name, String value, int defaultValue, int minValue, int maxValue) {
        int result = defaultValue;
        if (value != null) {
            value = value.trim();
            try {
                result = Integer.parseInt(value);
                if (result < minValue) {
                    log.debug("" + name + " value of " + value + " smaller than minimum " + minValue + ". Using minimum value " +
                        minValue);
                    result = minValue;
                }
                else if (result > maxValue) {
                    log.debug("" + name + " value of " + value + " greater than maximum " + maxValue + ". Using maximum value " +
                        maxValue);
                    result = maxValue;
                }
            }
            catch (NumberFormatException nfe) {
                log.debug("Could not parse " + name + " value of '" + value +
                        "'. Using default value " + defaultValue);
            }
        }

        return result;
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || "".equals(s);
    }

    public static boolean isNotNullAndIsYesOrNo(String s) {
        return s != null && ("YES".equals(s) || "NO".equals(s));
    }

    public static boolean isNotNullAndIsYes(String s) {
        return s != null && "YES".equals(s);
    }

    public static boolean isNotNullAndIsIntGreaterThanZero(String s) {
        boolean result = false;
        if (s != null) {
            try {
                result = (Integer.parseInt(s) > 0);
            }
            catch (NumberFormatException nfe) {
                // invalid
            }
        }
        return result;
    }
    
    public static boolean isNotNullAndIsIntBetween(String s, int min, int max) {
        boolean result = false;
        if (s != null) {
            try {
                int i = Integer.parseInt(s);
                result = (i >= min && i <= max);
            }
            catch (NumberFormatException nfe) {
                // invalid
            }
        }
        return result;
    }
}
