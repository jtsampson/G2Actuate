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

import grails.converters.JSON
import grails.converters.XML

class ActuatorController {

    ActuatorController() {
        JSON.createNamedConfig('PASS-THROUGH-JSON') {
            it.registerObjectMarshaller(String) { ->
                it.toString()
            }
        }
    }

    static responseFormats = ['json', 'xml']

    static allowedMethods = [actuator   : 'GET',
                             beans      : 'GET',
                             configprops: 'GET',
                             env        : 'GET',
                             heapdump   : 'GET',
                             info       : 'GET',
                             mappings   : 'GET',
                             metrics    : 'GET',
    ]

    def beanService
    def envService
    def infoService
    def mappingService
    def metricService

    // TODO enabling/disabling should be handled in plugin set up.

    def actuator() {
        //TODO
    }

    def beans() {
        def result = beanService.collectBeans()

        request.withFormat {
            json {
                render result as JSON
            }
            xml {
                render result as XML
            }
        }
    }

    def configprops() {
        //TODO
    }

    def env() {
        def result = envService.collectEnvironment()

        request.withFormat {
            json {
                render result as JSON
            }
            xml {
                render result as XML
            }
        }
    }

    def info() {
        def result = infoService.collectInfo()

        request.withFormat {
            json {
                render result as JSON
            }
            xml {
                render result as XML
            }
        }
    }

    def notAuthorized() {
        render(status: 401)
    }

    def mappings() {
        def result = mappingService.collectMappings()

        request.withFormat {
            json {
                render result as JSON
            }
            xml {
                render result as XML
            }
        }
    }

    def metrics() {
        def result = metricService.collectMetrics()
        request.withFormat {
            json {
                render result as JSON
            }
            xml {
                render result as XML
            }
        }
    }
}