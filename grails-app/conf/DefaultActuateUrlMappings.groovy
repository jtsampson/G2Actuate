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
        def trace = endpoints.trace

        def cp = conf.management.'context-path'


        // beans mappings
        if (beans.enabled) {
            enabling(cp, beans)
            "${cp}${beans.path}"(controller: "actuator", action: "beans", method: 'GET')
        } else {
            disabling(cp, beans)
        }

        // environment mappings
        if (env.enabled) {
            enabling(cp, env)
            "${cp}${env.path}"(controller: "actuator", action: "env", method: 'GET')
        } else {
            disabling(cp, env)
        }

        // health mappings
        if (health.enabled) {
            enabling(cp, health)
            "${cp}${health.path}"(controller: "health", action: "health", method: 'GET')
        } else {
            disabling(cp, health)
        }

        // heapdump mappings
        if (heapdump.enabled) {
            enabling(cp, heapdump)
            "${cp}${heapdump.path}"(controller: "heapDump", action: "heapdump", method: 'GET')
        } else {
            disabling(cp, heapdump)
        }

        // info mappings
        if (info.enabled) {
            enabling(cp, info)
            "${cp}${info.path}"(controller: "actuator", action: "info", method: 'GET')
        } else {
            disabling(cp, info)
        }

        // loggers mappings
        if (loggers.enabled) {
            enabling(cp, loggers )
            "${cp}${loggers.path}"(controller: "logger", action: "index", method: 'GET')
            "${cp}${loggers.path}/$id"(controller: "logger", action: "show", method: 'GET')
            "${cp}${loggers.path}/$id"(controller: "logger", action: "update", method: 'PUT')
        } else {
            disabling(cp, loggers )
        }

        // mappings mappings
        if ( mappings.enabled) {
            enabling(cp, mappings)
            "${cp}${mappings.path}"(controller: "actuator", action: "mappings", method: 'GET')
        } else {
            disabling(cp,  mappings)
        }

        // metrics mappings
        if (metrics.enabled) {
            enabling(cp, metrics)
            "${cp}${metrics.path}"(controller: "actuator", action: "metrics", method: 'GET')
        } else {
            disabling(cp, metrics)
        }

        // trace mappings
        if (trace.enabled) {
            enabling(cp, trace)
            "${cp}${trace.path}"(controller: "trace", action: "trace", method: 'GET')
        } else {
            disabling(cp, trace)
        }
        println '-------------------------------'
    }

    static enabling(cp, conf) {
        println "Enabling Actuate endpoint $cp${conf.path} \t\t[sensitive : ${conf.sensitive}]"
    }

    static disabling(cp, conf) {
        println "Disabling Actuate endpoint $cp${conf.path} \t\t[sensitive : ${conf.sensitive}]"
    }
}
