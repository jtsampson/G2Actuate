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
    def developers = [ [ name: "John Sampson", email: "jtsampson2000+g2actuate@gmail.com" ]]
    def issueManagement = [ system: "Github", url: "https://github.com/jtsampson/g2Actuator/issues" ]
    def scm = [ url: "https://github.com/jtsampson/g2Actuator" ]


    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
