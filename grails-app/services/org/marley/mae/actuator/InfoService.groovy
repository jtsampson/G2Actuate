package org.marley.mae.actuator

import grails.util.Environment
import grails.util.Holders
import grails.util.Metadata
import groovy.json.JsonSlurper

class InfoService {

    def static final random = new Random(new Date().getTime());
    def scm = 'GIT'
    def grailsApplication

    def collectInfo() {
        TreeMap info = [:]

        info << app()
        info << grails()
        info << scm()
        info
    }

    private def app() {
        def app = [:]
        def description = Metadata.getCurrent().getProperty('app.description') ?: 'app.description not set'
        app['name'] = Metadata.getCurrent().getApplicationName()
        app['description'] = description
        app['version'] = Metadata.getCurrent().getApplicationVersion()
        [app: app]
    }

    // TODO consider a grails service?
    private def grails() {
        def grails = [:]
        def metadata = grails.util.Metadata.getCurrent()
        //grails['application-version'] = metadata.getApplicationVersion()
        grails['grails-version'] = metadata.getCurrent().getGrailsVersion()
        grails['groovy-version'] = GroovySystem.version
        grails['jvm-version'] = System.getProperty('java.version')
        grails['reloading-active'] = Environment.reloadingAgentEnabled
        grails['controllers'] = grailsApplication.controllerClasses.size()
        grails['domains'] = grailsApplication.domainClasses.size()
        grails['services'] = grailsApplication.serviceClasses.size()
        grails['tag-libraries'] = grailsApplication.tagLibClasses.size()

        // plugins
        TreeMap plugins = [:] // maintain insert order by key
        Holders.pluginManager.allPlugins.each { plugin ->
            plugins[plugin.name] = plugin.version
        }
        grails['plugins'] = plugins
        ['grails': grails]
    }

    private def scm() {
        //TODO read from file scm provider places this is application.properties, but should have probably been added to config.properties or injected
        Properties properties = new Properties()
        File propertiesFile = new File('application.properties')
        propertiesFile.withInputStream {
            properties.load(it)
        }

        //def description = Metadata.getCurrent().getProperty('app.description') ?: 'app.description not set'
        def scm = new JsonSlurper().parseText(properties?.scm ?: "{\"message\":\"Nothing found: did you implement g2actuator.scmInfo in buildConfig.groovy?\"}")

        ['scm': scm]
    }

    private def build() {
        def build = [:]
        build['version'] = 0
        build['artifact'] = 0
        build['name'] = 0
        build['group']
        build['time']
        ['build': properties]
    }


    private plugins() {

    }

}
