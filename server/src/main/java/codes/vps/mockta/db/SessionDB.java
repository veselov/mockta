/*
 * Copyright (c) 2021-2022 Pawel S. Veselov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package codes.vps.mockta.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import codes.vps.mockta.util.Util;
import codes.vps.mockta.obj.okta.ErrorObject;
import lombok.NonNull;

public class SessionDB {

    private final static Map<String, OktaSession> sessionsByToken = new ConcurrentHashMap<>();

    // $TODO: These need to be cleaned up some how
    private final static Map<String, OktaSession> sessionsByID = new ConcurrentHashMap<>();

    public static OktaSession createSession(OktaUser who) {

        OktaSession session = new OktaSession(who);
        sessionsByToken.put(session.getToken(), session);
        sessionsByID.put(session.getId(), session);
        return session;

    }

    private static OktaSession foundSession(OktaSession session, String how) {

        if (session == null) {
            throw ErrorObject.notFound(how).boom();
        }
        session.setToken(null);
        if (!session.isValid()) {
            throw ErrorObject.invalidSession("isValid()=false").boom(); // right?
        }
        return session;

    }

    @NonNull
    public static OktaSession getByTokenOnce(String token) {
        return foundSession(sessionsByToken.remove(token), "token " + token);
    }

    @NonNull
    public static OktaSession getByCookie(@NonNull String sid) {
        return foundSession(sessionsByID.get(sid), "session "+sid);
    }


    public static void remove(OktaSession session) {

        Util.whenNotNull(session.getToken(), sessionsByToken::remove);
        sessionsByID.remove(session.getId());

    }
}
