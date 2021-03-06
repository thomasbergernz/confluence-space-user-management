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

import com.opensymphony.xwork.ActionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author Gary S. Weaver
 */
public class SessionUtil {

    private static final Log log = LogFactory.getLog(SessionUtil.class);

    public static Map getSession() {
        Map session = null;
        ActionContext context = ActionContext.getContext();
        if (context != null) {
            session = (Map) context.get("session");
        } else {
            log.warn("ActionContext was null!");
        }
        return session;
    }

    public static Object getSessionProperty(String key) {
        Object value = null;
        Map session = getSession();
        if (session != null) {
            value = session.get(key);
        } else {
            log.warn("Session was null!");
        }

        return value;
    }

    public static void setSessionProperty(String key, Object value) {
        Map session = getSession();
        if (session != null) {
            if (value != null) {
                session.put(key, value);
            } else {
                session.remove(key);
            }
        } else {
            log.warn("Session was null!");
        }
    }

    public static void removeSessionProperty(String key) {
        Map session = getSession();
        if (session != null) {
            session.remove(key);
        } else {
            log.warn("Session was null!");
        }
    }
}
