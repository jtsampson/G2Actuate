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

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.context.ServletContextHolder

@Transactional
class EnvService {

    def grailsApplication

    def collectEnvironment() {
        TreeMap env = [:]
        env << servlet()
        env << jvmProperties()
        env << osProperties()
        //  env << applicationConfig() // TODO Figure out if we need application config.
        //env << refresh()
    }

    def servlet() {
        TreeMap params = [:]
        def context = ServletContextHolder.getServletContext()
        context.initParameterNames.each { name ->
            params[name] = context.getAttribute(name)
        }
        ['servlet': params]
    }

    def jvmProperties() {
        TreeMap properties = [:]
        System.properties.each { k, v ->
            properties[k] = v
        }
        ['systemProperties': properties]
    }

    def osProperties() {
        TreeMap properties = [:]
        System.env.each { k, v ->
            properties[k] = v
        }
        ['systemEnvironment': properties]
    }

    def applicationConfig() {
        ['applicationConfig': grailsApplication.config.flatten()]
    }

    def refresh() {
        // TODO access cache information
    }

    def classPathResources() {
        printClassPath(this.class.classLoader)
    }

    def printClassPath(classLoader) {
        println "$classLoader"
        classLoader.getURLs().each { url ->
            println "- ${url.toString()}"
        }
        if (classLoader.parent) {
            printClassPath(classLoader.parent)
        }
    }


}
