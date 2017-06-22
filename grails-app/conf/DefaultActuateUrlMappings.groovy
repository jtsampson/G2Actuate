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

import grails.util.Holders

class DefaultActuateUrlMappings {

    // TODO JTS 6/21/2017 use logger?
    static mappings = {
        println ''
        println 'Reconfiguring Actuate Endpoints'
        println '-------------------------------'

        def endpoint = Holders.config.g2actuate.endpoints

        if (endpoint.beans.enabled) {
            enabling("/beans")
            "/beans"(controller: "actuator", action: "beans", method: 'GET')
        } else {
            disabling("/beans")
        }

        if (endpoint.heapdump.enabled) {
            enabling("/heapdump")
            "/heapdump"(controller: "heapDump", action: "heapdump", method: 'GET')
        } else {
            disabling("/heapdump")
        }

        if (endpoint.env.enabled) {
            enabling("/env")
            "/env"(controller: "actuator", action: "env", method: 'GET')
        } else {
            disabling("/env")
        }

        if (endpoint.info.enabled) {
            enabling("/info")
            "/info"(controller: "actuator", action: "info", method: 'GET')
        } else {
            disabling("/info")
        }

        if (endpoint.loggers.enabled) {
            enabling("/loggers")
            "/loggers"(controller: "logger", action: "index", method: 'GET')
            "/loggers/$id"(controller: "logger", action: "show", method: 'GET')
            "/loggers/$id"(controller: "logger", action: "update", method: 'PUT')
        } else {
            disabling("/loggers")
        }

        if (endpoint.mappings.enabled) {
            enabling("/mappings")
            "/mappings"(controller: "actuator", action: "mappings", method: 'GET')
        } else {
            disabling("/mappings")
        }

        if (endpoint.metrics.enabled) {
            enabling("/metrics")
            "/metrics"(controller: "actuator", action: "metrics", method: 'GET')
        } else {
            disabling("/metrics")
        }

        if (endpoint.trace.enabled) {
            enabling("/trace")
            "/trace"(controller: "trace", action: "trace", method: 'GET')
        } else {
            disabling("/trace")
        }
        println '-------------------------------'
    }

    static enabling(endpoint) {
        println "Enabling  Actuate endpoint $endpoint"
    }

    static disabling(endpoint) {
        println "Disabling Actuate endpoint $endpoint"
    }
}
