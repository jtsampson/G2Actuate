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

class LoggerControllerIntegrationSpec extends IntegrationSpec {

    static String APPLICATION_JSON = MediaType.APPLICATION_JSON.toString()
    static String APPLICATION_XML = MediaType.APPLICATION_XML.toString()
    static String APPLICATION_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8")).toString()
    static String APPLICATION_XML_UTF8 = new MediaType("application", "xml", Charset.forName("UTF-8")).toString()

    @Unroll
    void "/loggers serves #type"() {
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
        format | type             | mime
        'json' | APPLICATION_JSON | APPLICATION_JSON_UTF8
        'xml'  | APPLICATION_XML  | APPLICATION_XML_UTF8
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
