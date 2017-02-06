package org.marley.mae.actuator

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
