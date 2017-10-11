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

import com.github.jtsampson.actuate.security.ActuateSecurity
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.Specification

@TestFor(HealthController)
class HealthControllerSpec extends Specification {


    static def COLLECT_HEALTH_RESPONSE = [status                        : 'UP',
                                          h2DataSourceHealthIndicator   : [status    : 'UP',
                                                                           database  : 'H2',
                                                                           version   : '1.3.176 (2014-04-05)',
                                                                           driver    : 'org.h2.jdbc.JdbcConnection',
                                                                           validation: 'select 1', validationResult: 1],
                                          derbyDataSourceHealthIndicator: [status    : 'UP',
                                                                           database  : 'Apache Derby',
                                                                           version   : '10.12.1.1 -(1704137)',
                                                                           driver    : 'org.apache.derby.impl.jdbc.EmbedConnection',
                                                                           validation: 'SELECT 1 FROM SYSIBM.SYSDUMMY1', validationResult: 1]
    ]

    def mockHealthService

    def setup() {
        mockHealthService = mockFor(HealthService)
    }

    void "request is unsecure"() {
        given:
        mockHealthService.demand.collectHealth() { -> return COLLECT_HEALTH_RESPONSE }

        request.secure = false
        request.addHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE)
        controller.healthService = mockHealthService.createMock()

        when:
        controller.health()

        then:
        response.status == 200
        response.json.status == 'UP'
        !response.json.derbyDataSourceHealthIndicator // excluded from results since request not secure
        !response.json.h2DataSourceHealthIndicator // excluded from results since request not secure
    }

    void "request secure and user authorized"() {
        given:
        mockHealthService.demand.collectHealth() { -> return COLLECT_HEALTH_RESPONSE }

        request.secure = true
        request.addHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE)
        controller.healthService = mockHealthService.createMock()
        controller.actuateSecurity = new Authorized()

        when:
        controller.health()

        then:
        response.status == 200
        response.json.status == 'UP'
        response.json.derbyDataSourceHealthIndicator // included in results since request secure and user authorized
        response.json.h2DataSourceHealthIndicator // included in results since request not and user authorized
    }

    void "request secure and user unauthorized"() {
        given:
        mockHealthService.demand.collectHealth() { -> return COLLECT_HEALTH_RESPONSE }

        request.secure = true
        request.addHeader(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE)
        controller.healthService = mockHealthService.createMock()
        controller.actuateSecurity = new Unauthorized()

        when:
        controller.health()

        then:
        response.status == 401
    }

    class Authorized extends ActuateSecurity {
        @Override
        boolean isUserAuthorized(final Object request) {
            true
        }
    }

    class Unauthorized extends ActuateSecurity {
        @Override
        boolean isUserAuthorized(final Object request) {
            false
        }
    }
}
