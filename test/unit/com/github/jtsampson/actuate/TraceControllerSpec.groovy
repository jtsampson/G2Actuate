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

import filters.TraceFilters
import grails.artefact.Artefact
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.runtime.FreshRuntime
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.beans.factory.BeanCreationException
import spock.lang.FailsWith
import spock.lang.Specification

import javax.servlet.http.Cookie

@FreshRuntime
@TestFor(TraceController)
@Mock([TraceService, TraceFilters])
class TraceControllerSpec extends Specification {

    static doWithSpring = {
        traceService(TraceService)
    }

    def anyController = new AnyController()

    def setup() {

        applicationContext.getBean('traceService')
        controller.traceService = applicationContext.getBean('traceService')

        def cookie1 = new Cookie("APPNAME", "ABCD")
        def cookie2 = new Cookie("ENVNAME", "EFGH")

        def cookie3 = new Cookie("RESP-APPNAME", "ABCD")
        def cookie4 = new Cookie("RESP-ENVNAME", "EFGH")

        request.cookies = [cookie1,cookie2].toArray()

        request.addHeader('one', 'one')
        request.addHeader('two', 'two')
        request.addHeader('tre', 'tre')

        response.addCookie(cookie3)
        response.addCookie(cookie4)

        response.addHeader('four', 'four')
        response.addHeader('five', 'five')
        response.addHeader('six', 'six')
    }

    void "supports json"() {

        when:
        request.addHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE)
        withFilters(controller: "any", action: "index") { anyController.index() }
        controller.trace()

        then:
        response.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(JSON_CONTENT_TYPE)
    }

    void "supports xml"() {

        when:
        request.addHeader(HttpHeaders.ACCEPT, XML_CONTENT_TYPE)
        withFilters(controller: "any", action: "index") { anyController.index() }
        controller.trace()

        then:
        response.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(XML_CONTENT_TYPE)
    }

    void "timestamp path method"() {

        when:
        request.addHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE)
        request.forwardURI = '/G2Actuate/trace'

        withFilters(controller: "any", action: "index") { anyController.index() }
        controller.trace()

        then:
        response.json[0].timestamp // exists
        response.json[0].path == '/G2Actuate/trace'
        response.json[0].method == 'GET'

    }

    void "request.headers.collect default(true)"() {

        when:
        withFilters(controller: "any", action: "index") { anyController.index() }
        controller.trace()

        then:
        response.json[0].headers.request
    }

    void "request.headers.collect (false)"() {
        setup:
        config.g2actuate.endpoints.trace.request.headers.collect = false
        withFilters(controller: "any", action: "index") { anyController.index() }

        when:
        controller.trace()

        then:
        !response.json[0].headers.keySet().contains('reqest')
    }

    void "request.headers defaults"() {

        setup:
        withFilters(controller: "any", action: "index") { anyController.index()}

        when:
        controller.trace()

        then:
        def rqh = response.json[0].headers.request
        rqh.containsKey('one')
        rqh.containsKey('two')
        rqh.containsKey('tre')
    }

    void "request.headers.include"() {
        setup:
        config.g2actuate.endpoints.trace.request.headers.include = ["one"]
        withFilters(controller: "any", action: "index") { anyController.index() }

        when:
        controller.trace()

        then:
        def rqh = response.json[0].headers.request
        rqh.containsKey('one')
        !rqh.containsKey('two')
        !rqh.containsKey('tre')
    }

    void "request.headers.exclude"() {

        setup:
        config.g2actuate.endpoints.trace.request.headers.exclude = ["one"]
        withFilters(controller: "any", action: "index") { anyController.index() }

        when:
        controller.trace()

        then:
        def rqh = response.json[0].headers.request
        !rqh.containsKey('one')
        rqh.containsKey('two')
        rqh.containsKey('tre')
    }

    void "props.collect (false)"() {

        setup:
        config.g2actuate.endpoints.trace.props.collect = false
        withFilters(controller: "any", action: "index") { anyController.index() }

        when:
        controller.trace()

        then:
        TraceFilters.allPropertyList.each {
            assert !response.json[0].containsKey(it)
        }

    }

    void "props.collect (true) - default"() {

        setup:
        withFilters(controller: "any", action: "index") { anyController.index() }

        when:
        controller.trace()

        then:
        def json = response.json[0]
        TraceFilters.allPropertyList.each {
            assert json.containsKey(it)
        }

    }

    void "props.list (one property)"() {

        setup:
        config.g2actuate.endpoints.trace.props.list = ["pathInfo"]
        withFilters(controller: "any", action: "index") { anyController.index() }

        when:
        controller.trace()

        then:
        def json = response.json[0]
        TraceFilters.allPropertyList.each {
            if (it == 'pathInfo') {
                assert json.containsKey(it)
            } else {
                assert !json.containsKey(it)
            }
        }

    }

    @FailsWith(BeanCreationException.class)
    void "props.list (non-existent-property) - assertion error"() {

        setup:
        config.g2actuate.endpoints.trace.props.list = ["non-existent-property"]

        expect:
        withFilters(controller: "any", action: "index") { anyController.index() }
        controller.trace()

    }

    void "response.headers.collect (false)"() {

        setup:
        config.g2actuate.endpoints.trace.response.headers.capture = false
        withFilters(controller: "any", action: "index") { anyController.index() }

        when:
        controller.trace()

        then:
        !response.json[0].headers.response
        !response.json[0].headers.keySet().contains('response')
    }

    void "response.headers.include"() {

        setup:
        config.g2actuate.endpoints.trace.response.headers.include = ["four"]
        withFilters(controller: "any", action: "index") { anyController.index() }

        when:
        controller.trace()

        then:
        def rqh = response.json[0].headers.response
        rqh.containsKey('four')
        !rqh.containsKey('five')
        !rqh.containsKey('six')
    }

    void "response.headers.exclude"() {

        setup:
        config.g2actuate.endpoints.trace.response.headers.exclude = ["four"]
        withFilters(controller: "any", action: "index") { anyController.index() }

        when:
        controller.trace()

        then:
        def rqh = response.json[0].headers.response
        !rqh.containsKey('four')
        rqh.containsKey('five')
        rqh.containsKey('six')
    }

//        TODOD test response cookies, may requite injecting a response wrapper to collect response cookies.
//        void "request.cookies.collect and response.cookies.collect (unset)"() {
//        setup:
//        withFilters(controller: "any", action: "index") { anyController.index() }
//
//        when:
//        controller.trace()
//
//        then:
//        def cookies = response.json[0].cookies
//        cookies.request.any{ it.id == 'APPNAME'}
//        cookies.request.any{ it.id == 'ENVNAME'}
//        cookies.response.any{ it.id == 'RESP-APPNAME'}
//        cookies.response.any{ it.id == 'RESP-ENVNAME'}
//    }
//

//        TODOD test response cookies, may requite injecting a response wrapper to collect response cookies.
//        void "request.cookies.collect and response.cookies.collect (true)"() {
//
//        setup:
//        config.g2actuate.endpoints.trace.request.cookies.collect = true
//        config.g2actuate.endpoints.trace.response.cookies.collect = true
//        withFilters(controller: "any", action: "index") { anyController.index() }
//
//        when:
//        controller.trace()
//
//        then:
//        response.json[0].headers.request.containsKey('cookies')
//        response.json[0].headers.response.containsKey('cookies')
//    }

//    TODOD test response cookies, may requite injecting a response wrapper to collect response cookies.
//    void "request.cookies.collect and response.cookies.collect (false)"() {
//
//        setup:
//        config.g2actuate.endpoints.trace.request.cookies.collect = false
//        config.g2actuate.endpoints.trace.response.cookies.collect = false
//        withFilters(controller: "any", action: "index") { anyController.index() }
//
//        when:
//        controller.trace()
//
//        then:
//        response.json[0].headers.request.containsKey('cookies')
//        response.json[0].headers.response.containsKey('cookies')
//    }

    /**
     * Simple controller for tests.
     */
    @Artefact("Controller")
    class AnyController {
        static responseFormats = ['json', 'xml']

        def index() {
            ['nothing': 'special']
        }
    }
}