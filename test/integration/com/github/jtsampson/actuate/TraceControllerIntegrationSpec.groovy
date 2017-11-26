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

import java.nio.charset.Charset

class TraceControllerIntegrationSpec extends IntegrationSpec {

    static String APPLICATION_JSON = MediaType.APPLICATION_JSON.toString()
    static String APPLICATION_XML = MediaType.APPLICATION_XML.toString()
    static String APPLICATION_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8")).toString()
    static String APPLICATION_XML_UTF8 = new MediaType("application", "xml", Charset.forName("UTF-8")).toString()

    void "/trace responds with json"() {
        given:
        def controller = new TraceController()
        controller.request.setContentType(APPLICATION_JSON )
        controller.response.format = 'json'

        when:
        controller.trace()

        then:
        controller.response.status == 200
        controller.response.contentType == APPLICATION_JSON_UTF8
    }


    void "/trace responds with xml"() {
        given:
        def controller = new TraceController()
        controller.request.setContentType(APPLICATION_XML )
        controller.response.format = 'xml'

        when:
        controller.trace()

        then:
        controller.response.status == 200
        controller.response.contentType == APPLICATION_XML_UTF8

    }
}
