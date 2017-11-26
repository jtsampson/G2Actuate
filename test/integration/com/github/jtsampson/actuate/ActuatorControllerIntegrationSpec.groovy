/*
 *  Copyright 2016-2017 John Sampson
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.github.jtsampson.actuate

import grails.test.spock.IntegrationSpec
import org.springframework.http.MediaType
import spock.lang.Unroll

import java.nio.charset.Charset

class ActuatorControllerIntegrationSpec extends IntegrationSpec {

    static String APPLICATION_JSON = MediaType.APPLICATION_JSON.toString()
    static String APPLICATION_XML = MediaType.APPLICATION_XML.toString()
    static String APPLICATION_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8")).toString()
    static String APPLICATION_XML_UTF8 = new MediaType("application", "xml", Charset.forName("UTF-8")).toString()

    @Unroll
    void "/#endpoint supports #type"() {
        given:
        def controller = new ActuatorController()
        controller.request.setContentType(type)

        // mapping specific set up.
        def mappingService = new MappingService()
        mappingService.servletContext = applicationContext.servletContext
        mappingService.grailsApplication = applicationContext.grailsApplication
        controller.mappingService = mappingService

        when:
        controller."$endpoint"()

        then:
        controller.response.status == 200
        controller.response.contentType == mime

        where:
        endpoint   | type             | mime
        "beans"    | APPLICATION_JSON | APPLICATION_JSON_UTF8
        "beans"    | APPLICATION_XML  | APPLICATION_XML_UTF8
        "env"      | APPLICATION_JSON | APPLICATION_JSON_UTF8
        "env"      | APPLICATION_XML  | APPLICATION_XML_UTF8
        "info"     | APPLICATION_JSON | APPLICATION_JSON_UTF8
        "info"     | APPLICATION_XML  | APPLICATION_XML_UTF8
        "mappings" | APPLICATION_JSON | APPLICATION_JSON_UTF8
        "mappings" | APPLICATION_XML  | APPLICATION_XML_UTF8
        "metrics"  | APPLICATION_JSON | APPLICATION_JSON_UTF8
        "metrics"  | APPLICATION_XML  | APPLICATION_XML_UTF8
    }
}
