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


import DefaultActuateUrlMappings
import com.github.jtsampson.actuate.health.HealthAggregator
import com.github.jtsampson.actuate.security.ActuateSecurity
import com.github.jtsampson.actuate.shutdown.ShutdownEmbededServletContainer
import com.github.jtsampson.actuate.shutdown.ShutdownSystemExit
import grails.util.Environment
import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPlugin

class G2ActuateGrailsPlugin {
    // the plugin version
    def version = "0.2-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "G2Actuate Plugin" // Headline display id of the plugin
    def author = "John Sampson"
    def authorEmail = "jtsampson2000+g2actuate@gmail.com"
    def description = '''\
Provides a back port of some of Spring Boot Actuator capabilites for Grails 2 Applications.

The following endpoints:
* /beans Displays a complete list of all the Spring beans in your application.
* /env  Displays properties from the environment.
* /health Displays health information.
* /heapdump Performs a thread dump.
* /info Displays arbitrary application info.
* /loggers Shows and modifies the configuration of loggers in the application.
* /mappings Displays a collated list of all url-mappings paths.
* /metrics Shows ‘metrics’ information for the current application.
* /shutdown (TODO)
* /trace Displays trace information (by default the last 100 HTTP requests).

Call back 'contributor' closures are provided to you can customise the data returned by the endpoints
'''

    def documentation = "http://grails.org/plugin/build-info"
    def license = "APACHE"
    def developers = [[name: "John Sampson", email: "jtsampson2000+g2actuate@gmail.com"]]
    def issueManagement = [system: "Github", url: "https://github.com/jtsampson/g2Actuator/issues"]
    def scm = [url: "https://github.com/jtsampson/G2Actuate"]
    def application = Holders.getGrailsApplication() // is this necessary?

    /**
     * Adds additions to web.xml (optional), this event occurs before (not sure what).
     */
    def doWithWebDescriptor = { webXml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    /**
     * Performs runtime Spring Configuration.
     */
    def doWithSpring = {

        // Define the appropriate shutterDowner
        if (Boolean.getBoolean("TomcatKillSwitch.active")) {
            // See
            println 'Configuring the Shutdown for Tomcat (may be overridden later)'
            shutterDowner(ShutdownEmbededServletContainer)
        } else {
            println 'Configuring the Shutdown for System Exit (may be overridden later)'
            shutterDowner(ShutdownSystemExit)
        }

        // If the application is not using spring security core or shiro, then by default we will try to
        // desensitize the endpoints (as there may be no authentication anyway); as long as
        // autoDesensitize=true
        if (application.config.g2actuate.autoDesensitize) {
            if (!Holders.pluginManager.hasGrailsPlugin('spring-security-core') ||
                    !Holders.pluginManager.hasGrailsPlugin('shiro')) {
                application.config.g2actuate.endpoints.each { endpoint ->
                    endpoint.value.sensitive = false
                }
            }
        }

        // define other beans
        healthAggregator(HealthAggregator)
        actuateSecurity(ActuateSecurity)

        mergeConfig(application)
        // def agregator application.mainContext.getBean( HealthAggregator.class );


    }

    /**
     *  Registers dynamic methods to classes (optional).
     */
    def doWithDynamicMethods = { ctx ->
        // NOOP
    }

    /**
     * Performs post initialization spring configuration.
     */
    def doWithApplicationContext = { ctx ->
        // NOOP
    }

    /**
     * Code that is executed when any artefact that this plugin is watching is modified and reloaded.
     * The event contains: event.source, event.application, event.manager, event.ctx, and event.plugin.
     */
    def onChange = { event ->
        // NOOP
    }

    /**
     * Code that is executed when the project configuration changes.
     * The event contains: event.source, event.application, event.manager, event.ctx, and event.plugin.
     */
    def onConfigChange = { event ->

        mergeConfig(application)

        /* When we merge configs, we start with the DefaultActuateConfig an overwrite that with the user's
         * customizations. The side effect of this is that we generate and extra onConfigChangeEvent. The
         * 'second' event (indicating that the configs have been merged), will conain a 'reloadMarker'
         * and we know it is safe to reload the mappings.
         */
        if (event.source?.g2actuate?.reloadMarker == true) {
            this.reloadMappings()
        }
    }

    /**
     * Code that is executed when the application shuts down (optional).
     */
    def onShutdown = { event ->
        // NOOP
    }

    /**
     * Notifies the 'urlMappings' plugin to reload our DefaultActuateUrlMappings
     */
    void reloadMappings() {
        def pm = application.getParentContext().getBean('pluginManager')
        pm.getGrailsPlugin('urlMappings').notifyOfEvent(GrailsPlugin.EVENT_ON_CHANGE, DefaultActuateUrlMappings.class)
    }


    static void mergeConfig(GrailsApplication app) {

        ConfigObject currentConfig = app.config.g2actuate
        ConfigSlurper slurper = new ConfigSlurper(Environment.getCurrent().getName())
        ConfigObject secondaryConfig = slurper.parse(app.classLoader.loadClass("DefaultActuateConfig"))
        ConfigObject config = new ConfigObject()
        config.putAll(secondaryConfig.g2actuate.merge(currentConfig))
        app.config.g2actuate = config

    }

}
