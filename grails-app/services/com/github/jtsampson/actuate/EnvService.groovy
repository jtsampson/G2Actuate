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

/**
 * A service to expose the environment properties.
 * Currently:
 * <li> servlet properties
 * <li> jvm properties
 * <li> os properties
 *
 * @author jsampson
 */
@Transactional
class EnvService {

    def grailsApplication

    /**
     * Returns a collection of environment property maps.
     * @return a collection of environment property maps.
     */
    def collectEnvironment() {
        TreeMap env = [:]
        env << servlet()
        env << systemProperties()
        env << systemEnvironment()
        //  env << applicationConfig() // TODO Figure out if we need application config.
        //env << refresh()
    }

    /**
     * Returns the 'servlet' map (the init parameters from the servlet context)
     * @return the 'servlet' map
     */
    def servlet() {
        TreeMap params = [:]
        def context = ServletContextHolder.getServletContext()
        context.initParameterNames.each { name ->
            params[name] = context.getAttribute(name)
        }
        ['servlet': params]
    }

    /**
     * Returns the 'systemProperties' map (Java System properties).
     * @return the 'systemProperties' map
     */
    def systemProperties() {
        TreeMap properties = [:]
        System.properties.each { k, v ->
            properties[k] = v
        }
        ['systemProperties': properties]
    }

    /**
     * Returns the 'systemEnvironment' map (Java System environment properties).
     * @return the 'systemEnvironment' map
     */
    def systemEnvironment() {
        TreeMap properties = [:]
        System.env.each { k, v ->
            properties[k] = v
        }
        ['systemEnvironment': properties]
    }

    /**
     * Returns the 'applicationConfig' map (Flattened map of grails properties).
     * @return the 'applicationConfig' map
     */
    def applicationConfig() {
        ['applicationConfig': grailsApplication.config.flatten()]
    }

    def refresh() {
        // TODO access cache information
    }

    /**
     * Returns the 'applicationConfig' map (Flattened map of grails properties).
     * @return the 'applicationConfig' map
     */
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
