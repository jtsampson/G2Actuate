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

class LoggerControllerIntegrationSpec extends IntegrationSpec {

    def setup() {
    }

    def cleanup() {
    }

    void "index respondes with json"() {
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
