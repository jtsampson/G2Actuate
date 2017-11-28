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

/**
 * LoggerController integration tests.
 * @author jsampson
 * @since 1.0.2
 */
class LoggerControllerIntegrationSpec extends IntegrationSpec {

    static String APP_JSON = MediaType.APPLICATION_JSON.toString()
    static String APP_XML = MediaType.APPLICATION_XML.toString()
    static String APP_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8")).toString()
    static String APP_XML_UTF8 = new MediaType("application", "xml", Charset.forName("UTF-8")).toString()

    @Unroll
    void "/loggers (index) serves #type"() {
        given:
        def controller = new LoggerController()
        controller.request.setContentType(type)
        controller.response.format = format

        when:
        controller.index()

        then:
        controller.response.status == 200
        controller.response.contentType == mime

        where:
        format | type     | mime
        'json' | APP_JSON | APP_JSON_UTF8
        'xml'  | APP_XML  | APP_XML_UTF8
    }

    void "/loggers (show) "() {
        given:
        def controller = new LoggerController()
        controller.request.setContentType(APP_JSON)
        controller.request.parameters = [id: 'org.codehaus.groovy.grails.commons']

        when:
        controller.show()

        then:
        controller.response.status == 200
        controller.response.contentType == APP_JSON_UTF8
        controller.response.json == [configuredLevel: "ERROR", effectiveLevel: "ERROR"]

    }

    void "/loggers (update) "() {
        given:
        def controller = new LoggerController()
        controller.request.setContentType(APP_JSON)
        controller.request.parameters = [id: 'com.madeup.logger'] // upsert on unknow logger
        controller.request.method = 'put'
        controller.request.json = [configuredLevel: "DEBUG"]
        controller.response.format = 'json'

        when:
        controller.update()

        then:
        controller.response.status == 200
        controller.response.contentType == APP_JSON_UTF8
        controller.response.json == [configuredLevel: "DEBUG", effectiveLevel: "DEBUG"]
    }

    void "/loggers (update) params.id not set...causes 404 "() {
        given:
        def controller = new LoggerController()
        controller.request.method = 'put'

        when:
        controller.update()

        then:
        controller.response.status == 404
    }

    void "/loggers (update) with bad level is 422 "() {
        given:
        def controller = new LoggerController()
        controller.request.method = 'put'
        controller.request.parameters = [id: 'com.madeup.logger'] // upsert on unknow logger
        controller.request.json = [configuredLevel: "BAD_LEVEL"]

        when:
        controller.update()

        then:
        controller.response.status == 422
        controller.response.contentType == APP_JSON_UTF8
    }


    void "Assert message contains proper keys"() {
        given:
        def controller = new LoggerController()

        when:
        controller.index()

        then:
        200 == controller.response.status

        def resource = controller.response.json
        // Assert message contains proper keys
        resource.levels
        LoggerService.levelList.containsAll(resource.levels)
        resource.loggers
        resource.loggers.each { logger ->
            logger.each { loggerName, loggerConfig ->
                assert (loggerName)
                loggerConfig.each {
                    config, configLevel ->
                        ['configuredLevel', 'effectiveLevel'].contains(config)
                        LoggerService.levelList.contains(configLevel)
                }
            }
        }

    }
}
