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

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.apache.http.cookie.SetCookie
import org.codehaus.groovy.grails.web.mime.MimeType
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.http.MediaType

/**
 * Created by jsampson on 9/8/2017.
 */
class TraceControllerFunctionalSpec extends GebSpec {


    def static baseUrl

    def setupSpec() {

        baseUrl = "http://localhost:8080/G2Actuate"

        // call loggers so there will be at  least once trace in memory.
        RestBuilder rest = new RestBuilder()
        def cookies = ['APPNAME=ABCD', 'ENVNAME=EFGH']

        RestResponse response = rest.get("${baseUrl}/trace") {
            header HttpHeaders.ACCEPT, MimeType.JSON.name
            header 'one', 'one'
            header 'two', 'two'
            header 'tre', 'tre'
            header 'Cookies', cookies.join(';')
        }
        assert response.status == 200
    }

    def setup() {
        // def grailsApplication = new DefaultGrailsApplication()

    }

    void "supports json"() {
        given:
        RestBuilder rest = new RestBuilder()
        SetCookie

        when:
        RestResponse response = rest.get("${baseUrl}/trace") {
            header HttpHeaders.ACCEPT, MimeType.JSON.name
        }

        then:
        response.status == 200
        response.headers.getContentType().toString().startsWith(MediaType.APPLICATION_JSON_VALUE)

    }

    void "supports xml"() {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse response = rest.get("${baseUrl}/trace") {
            header HttpHeaders.ACCEPT, MimeType.XML.name
        }

        then:
        response.status == 200
        response.headers.getContentType().toString().startsWith(MediaType.APPLICATION_XML_VALUE)

    }

    void "timestamp path method"() {
        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse info = rest.get("${baseUrl}/info") {
            header HttpHeaders.ACCEPT, MimeType.JSON.name
        } // prime the traces cache with info call

        RestResponse trace = rest.get("${baseUrl}/trace") {
            header HttpHeaders.ACCEPT, MimeType.JSON.name
        } // get traces

        then:
        trace.status == 200
        trace.json[0].timestamp // exists
        trace.json[0].path == '/G2Actuate/info' // as trace isn't recorded.
        trace.json[0].method == 'GET'

    }

    void "request.headers.collect default(true)"() {

        given:
        RestBuilder rest = new RestBuilder()

        when:
        RestResponse info = rest.get("${baseUrl}/info") {
            header HttpHeaders.ACCEPT, MimeType.JSON.name
            header 'one', 'oneValue'
            header 'two', 'twoValue'
            header 'tre', 'treValue'
        } // prime the traces cache with info call


        RestResponse trace = rest.get("${baseUrl}/trace") {
            header HttpHeaders.ACCEPT, MimeType.JSON.name
        }

        then:
        def rqh = trace.json[0].headers.request
        rqh.containsKey('one')
        rqh.containsKey('two')
        rqh.containsKey('tre')
    }


}
