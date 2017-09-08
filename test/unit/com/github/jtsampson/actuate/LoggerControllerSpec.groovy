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

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

@TestMixin(GrailsUnitTestMixin)
@TestFor(LoggerController)
class LoggerControllerSpec extends Specification {

    def setup() {
        controller.loggerService = Mock(LoggerService)
    }

    @Unroll
    void "test index() handles #mime"() {
        given:
        request.addHeader(HttpHeaders.ACCEPT, mime)

        and:
        controller.loggerService.collectLevelsAndLoggers() >> { ["nothing": "important"] }

        when:
        controller.index()

        then:
        response.header('Content-Type').contains(mime)
        response.status == status

        where:
        mime              | status
        XML_CONTENT_TYPE  | 200
        JSON_CONTENT_TYPE | 200
    }

    @Unroll
    void "test show() handles #mime"() {
        given:
        request.addHeader(HttpHeaders.ACCEPT, mime)
        params.id = 'id'

        and:
        controller.loggerService.collectLoggerConfig(params.id) >> { ["nothing": "important"] }

        when:
        controller.show()

        then:
        response.header('Content-Type').contains(mime)
        response.status == status

        where:
        mime                   | status
        XML_CONTENT_TYPE       | 200
        JSON_CONTENT_TYPE      | 200
    }

    @Unroll
    void "test update() handles #mime"() {
        given:
        request.addHeader(HttpHeaders.ACCEPT, mime)
        params.id = 'id'

        and:
        controller.loggerService.collectLoggerConfig(params.id) >> { ["nothing": "important"] }

        when:
        controller.show()

        then:
        response.header('Content-Type').contains(mime)
        response.status == status

        where:
        mime              | status  | input
        XML_CONTENT_TYPE  | 200     |  '''{ "configuredLevel": "DEBUG"}'''
        JSON_CONTENT_TYPE | 200     |  '''{ "configuredLevel": "DEBUG"}'''
    }
}
