package com.github.jtsampson.actuate

import filters.TraceFilters
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.runtime.FreshRuntime
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.beans.factory.BeanCreationException
import spock.lang.FailsWith
import spock.lang.Specification
import spock.lang.Unroll

@FreshRuntime
// a clean application context and grails application instance is initialized for each test method call.
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
            controller.trace()
        }

        then:
        response.json[0].timestamp // exists
        response.json[0].path == '/G2Actuate/trace'
        response.json[0].method == 'GET'

    }

    void "request.headers.collect default(true)"() {

        when:
        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        response.json[0].headers.request
    }

    void "request.headers.collect (false)"() {

        when:
        configiration.request.headers.collect = false
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

    void "props.collect (false)"() {

        when:
        configiration.props.collect = false
        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        TraceFilters.allPropertyList.each {
            assert !response.json[0].containsKey(it)
        }

    }

    void "props.collect (true) - default"() {

        when:
        withFilters(controller: "trace", action: "trace") { controller.trace() }

        then:
        def json = response.json[0]
        TraceFilters.allPropertyList.each {
            assert json.containsKey(it)
        }

    }

    void "props.list (one property)"() {

        when:
        configiration.props.list = ["pathInfo"]
        withFilters(controller: "trace", action: "trace") { controller.trace() }

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

    // TODO: Probably needs to be rethought.
    @FailsWith(BeanCreationException.class)
    void "props.list (non-existent-property) - assertion error"() {

        setup:
        configiration.props.list = ["non-existent-property"]

        expect:
        withFilters(controller: "trace", action: "trace") { controller.trace() }

    }

//    @Unroll
//    void "response.headers.collect default(true)"() {
//
//        when:
//        def firstResppnse
//        def secondResponse
//        withFilters(controller: "trace", action: "trace") {
//            controller.trace()
//            controller.trace()
//        }
//        withFilters(controller: "trace", action: "trace") {
//            controller.trace()
//            secondResponse = response.json[1]
//        }
//
//
//        then:
//        response.json[1].headers.request
//        response.json[1].headers.response
//
//    }
//
//    @Unroll
//    void "response.headers.collect (false)"() {
//
//        when:
//        configiration.response.headers.capture = false
//        withFilters(controller: "trace", action: "trace") {
//            controller.trace()
//            controller.trace()
//            controller.trace()
//        }
//
//        then:
//        response.json[0].headers.request
//        !response.json[0].headers.response
//        response.json[1].headers.request
//        !response.json[1].headers.response
//
//    }

}