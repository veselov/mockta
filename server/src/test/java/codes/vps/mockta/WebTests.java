/*
 * Copyright (c) 2022 Pawel S. Veselov
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

package codes.vps.mockta;

import codes.vps.mockta.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public abstract class WebTests extends Tests {

    @Autowired
    private MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    protected CookieFilter cookies = new CookieFilter();

    protected ObjectMapper jacksonObjectMapper;

    @BeforeEach
    protected void setUp() {
        jacksonObjectMapper = springMvcJacksonConverter.getObjectMapper();
    }

    protected String mapToJson(Object obj) {
        return Util.reThrow(() -> jacksonObjectMapper.writeValueAsString(obj));
    }

    protected <T> T mapFromJson(String json, Class<T> clazz) throws JsonParseException, IOException {

        return jacksonObjectMapper.readValue(json, clazz);
    }

    protected RequestSpecification inJson(RequestSpecification r) {
        return r.accept("application/json");
    }

    protected RequestSpecification request() {
        return given().when().redirects().follow(false).filter(cookies).port(serverPort).log().ifValidationFails()
                .then().log().ifValidationFails().given();
    }

    RequestSpecification admin() {
        return request().header("Authorization", "SSWS " + app.getApiTokens().get(0));
    }

    RequestSpecification adminJson() {
        return admin().contentType("application/json");
    }

    protected RequestSpecification userHtml() {
        return user().accept("text/html");
    }

    protected RequestSpecification user() {
        // we set the "Accept" header, otherwise we get application/hal+json
        return inJson(request());
    }

    RequestSpecification userJson() {
        return user().contentType("application/json");
    }

}
