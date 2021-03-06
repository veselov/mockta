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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import codes.vps.mockta.model.SignOnMode;
import codes.vps.mockta.util.Util;
import codes.vps.mockta.model.App;
import codes.vps.mockta.model.AppSettings;
import codes.vps.mockta.model.AppUser;
import codes.vps.mockta.obj.okta.ErrorObject;
import codes.vps.mockta.model.OAuthClient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OktaApp extends DBObject {

	private final String id = Util.randomId();
	private final Date created = new Date();
	private String name; // unique
	private String savedName;
	private Date lastUpdated;
	private String label;
	private String profile;
	private SignOnMode signOnMode;
	private final Map<String, OktaAppUser> users = new ConcurrentHashMap<>();
	private final List<String> redirectUris = new ArrayList<>();

	public OktaApp(App app) {

		if (app.getName() == null) {
			throw ErrorObject.illegalArgument("need name").boom();
		}

		updateFrom(app);
		savedName = name;

	}

	public void updateFrom(App app) {

		name = app.getName();

		label = app.getLabel();
		lastUpdated = new Date();
		profile = app.getProfile();
		signOnMode = app.getSignOnMode();

		Util.whenNotNull(app.getSettings(), settings -> Util.whenNotNull(settings.getOauthClient(),
				oAuthClient -> Util.whenNotNull(oAuthClient.getRedirectUris(), redirectUris::addAll)));

	}

	public App represent() {

		try {

			Map<String, AppUser> users = new HashMap<>();
			for (Map.Entry<String, OktaAppUser> me : this.users.entrySet()) {
				users.put(me.getKey(), me.getValue().represent());
			}

			return App.builder()
					.created(created)
					.id(id)
					.signOnMode(signOnMode)
					.label(label)
					.lastUpdated(lastUpdated)
					.name(name)
					.profile(profile)
					.users(users)
					.settings(new AppSettings(new OAuthClient(redirectUris)))
					.build();

		} finally {
			close();
		}

	}


}
