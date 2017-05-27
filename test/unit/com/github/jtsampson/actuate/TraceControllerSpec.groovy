package com.github.jtsampson.actuate

import filters.TraceFilters
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.runtime.FreshRuntime
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.Specification

@FreshRuntime  // a clean application context and grails application instance is initialized for each test method call.
@TestFor(TraceController)
@Mock([TraceService, TraceFilters])
class TraceControllerSpec extends Specification {

    static doWithSpring = {
        traceService(TraceService)
    }

    def configiration

    def setup() {
        configiration = grailsApplication.config.g2actuate.endpoints.trace
        controller.traceService = applicationContext.getBean('traceService')

        request.addHeader('one', 'one')
        request.addHeader('two', 'two')
        request.addHeader('tre', 'tre')
    }


    void "supports json"() {

        when:
        request.addHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE)
        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        response.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(JSON_CONTENT_TYPE)
    }

    void "supports xml"() {

        when:
        request.addHeader(HttpHeaders.ACCEPT, XML_CONTENT_TYPE)
        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        response.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(XML_CONTENT_TYPE)
    }

    void "timestamp path method"() {

        when:
        request.addHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE)
        request.forwardURI = '/G2Actuate/trace'

        withFilters(controller: "trace", action: "trace") {
            controller.trace() }

        then:
        response.json[0].timestamp // exists
        response.json[0].path == '/G2Actuate/trace'
        response.json[0].method == 'GET'

    }

    void "request.headers.collect default(true)"() {

        when:
        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        response.json[0].headers
    }

    void "request.headers.collect false"() {

        when:
        configiration.request.headers.capture = false
        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        !response.json[0].headers.keySet().contains('reqest')
    }

    void "request.headers defaults"() {

        when:
        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        def rqh = response.json[0].headers.request
        rqh.containsKey('one')
        rqh.containsKey('two')
        rqh.containsKey('tre')
    }

    void "request.headers.include"() {

        when:
        configiration.request.headers.include = ["one"]
        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        def rqh = response.json[0].headers.request
        rqh.containsKey('one')
        !rqh.containsKey('two')
        !rqh.containsKey('tre')
    }

    void "request.headers.exclude"() {

        when:
        configiration.request.headers.exclude = ["one"]

        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        def rqh = response.json[0].headers.request
        !rqh.containsKey('one')
        rqh.containsKey('two')
        rqh.containsKey('tre')
    }


}