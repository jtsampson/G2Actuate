import DefaultActuateUrlMappings
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPlugin
import grails.util.Holders

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
* /heapdump Performs a thread dump.
* /env  Displays properties from the environment.
* /health (TODO)
* /info Displays arbitrary application info.
* /loggers Shows and modifies the configuration of loggers in the application.
* /metrics Shows ‘metrics’ information for the current application.
* /mappings Displays a collated list of all url-mappings paths
* /shutdown (TODO)
* /trace (TODO)  Displays trace information (by default the last 100 HTTP requests).

Call back 'contributor' closures are provided to you can customise the data returned by the endpoints
'''

    def documentation = "http://grails.org/plugin/build-info"
    def license = "APACHE"
    def developers = [[name: "John Sampson", email: "jtsampson2000+g2actuate@gmail.com"]]
    def issueManagement = [system: "Github", url: "https://github.com/jtsampson/g2Actuator/issues"]
    def scm = [url: "https://github.com/jtsampson/G2Actuate"]
    def application = Holders.getGrailsApplication() // is this necessary?

    // watch for changes to scaffolding templates...
    def watchedResources = ["file:./grails-app/conf/Config.groovy"]

    /**
     * Adds additions to web.xml (optional), this event occurs before (not sure what).
     */
    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    /**
     * Performs runtime Spring Configuration.
     */
    def doWithSpring = {
        mergeConfig(application)
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

        this.mergeConfig(application)

        /* When we merge configs, we start with the DefaultActuateConfig an overwrite that with the user's
         * customizations. The side effect of this is that we generate and extra onConfigChangeEvent. The
         * 'second' event (indicating that the configs have been merged), will conain a 'reloadMarker'
         * and we know it is safe to reload the mappings.
         */
        if (event.source?.g2actuate?.reloadMarker) {
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
