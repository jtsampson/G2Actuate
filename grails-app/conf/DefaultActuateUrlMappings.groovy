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

    static mappings = {
        println "-" * 60
        println 'Reconfiguring G2Actuate Endpoints'.center(60)
        println "-" * 60

        def conf = Holders.config.g2actuate
        def endpoints = Holders.config.g2actuate.endpoints

        // endpoints
        def beans = endpoints.beans
        def env = endpoints.env
        def health = endpoints.health
        def heapdump = endpoints.heapdump
        def info = endpoints.info
        def loggers = endpoints.loggers
        def mappings = endpoints.mappings
        def metrics = endpoints.metrics
        def shutdown = endpoints.shutdown
        def trace = endpoints.trace

        // deal with possibly empty config Object item
        def cp = conf.management."context-path".size() == 0 ? "" : "/" + conf.management."context-path"
        def app = Holders.grailsApplication.metadata['app.name']

        // beans mappings
        if (beans.enabled) {
            enabling(app, cp, beans)
            "${cp}/${beans.id}"(controller: "actuator", action: "beans", method: 'GET')
        } else {
            disabling(app, cp, beans)
        }

        // environment mappings
        if (env.enabled) {
            enabling(app, cp, env)
            "${cp}/${env.id}"(controller: "actuator", action: "env", method: 'GET')
        } else {
            disabling(app, cp, env)
        }

        // health mappings
        if (health.enabled) {
            enabling(app, cp, health)
            "${cp}/${health.id}"(controller: "health", action: "health", method: 'GET')
        } else {
            disabling(app, cp, health)
        }

        // heapdump mappings
        if (heapdump.enabled) {
            enabling(app, cp, heapdump)
            "${cp}/${heapdump.id}"(controller: "heapDump", action: "heapdump", method: 'GET')
        } else {
            disabling(app, cp, heapdump)
        }

        // info mappings
        if (info.enabled) {
            enabling(app, cp, info)
            "${cp}/${info.id}"(controller: "actuator", action: "info", method: 'GET')
        } else {
            disabling(app, cp, info)
        }

        // loggers mappings
        if (loggers.enabled) {
            enabling(app, cp, loggers)
            "${cp}/${loggers.id}"(controller: "logger", action: "index", method: 'GET')
            "${cp}/${loggers.id}/$id"(controller: "logger", action: "show", method: 'GET')
            "${cp}/${loggers.id}/$id"(controller: "logger", action: "update", method: 'PUT')
        } else {
            disabling(app, cp, loggers)
        }

        // mappings mappings
        if (mappings.enabled) {
            enabling(app, cp, mappings)
            "${cp}/${mappings.id}"(controller: "actuator", action: "mappings", method: 'GET')
        } else {
            disabling(app, cp, mappings)
        }

        // metrics mappings
        if (metrics.enabled) {
            enabling(app, cp, metrics)
            "${cp}/${metrics.id}"(controller: "actuator", action: "metrics", method: 'GET')
        } else {
            disabling(app, cp, metrics)
        }

        // shutdown mappings
        if (shutdown.enabled) {
            enabling(app, cp, shutdown)
            "${cp}/${shutdown.id}"(controller: "shutdown", action: "shutdown", method: 'GET')
        } else {
            disabling(app, cp, shutdown)
        }

        // trace mappings
        if (trace.enabled) {
            enabling(app, cp, trace)
            "${cp}/${trace.id}"(controller: "trace", action: "trace", method: 'GET')
        } else {
            disabling(app, cp, trace)
        }
        println "-" * 60
    }

    static enabling(app, cp, conf) {
        def out = new StringBuffer()
        out << "Enabling  $app$cp/${conf.id}".padRight(40, ".")
        out << "[sensitive : ${conf.sensitive}]".padLeft(20, ".")
        println out.toString()
    }

    static disabling(app, cp, conf) {
        def out = new StringBuffer()
        out << "Disabling $app$cp/${conf.id}".padRight(40, ".")
        out << "[sensitive : ${conf.sensitive}]".padLeft(20, ".")
        println out.toString()

    }
}
