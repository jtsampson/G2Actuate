<p align="center">
  <a href="https://github.com/jtsampson/g2actuate">
    <img src="G2ActuateSmall.png" width=144 height=144>
  </a>
  <h3 style="color:#990000" align="center">G2Actuate</h3>
  <p align="center">
    Providing the production ready capabilities of the Spring Boot Actuator to Grails 2 Projects.
  </p>
</p>

### Contents
 - [Grails Version](#GrailsVersion)
 - [Build](#Build)
 - [Install](#Install)
 - [Features](#Features)
 - [Available Endpoints](#AvailableEndpoints)
 - [Customizing endpoints](#Customization)

## <a name="GrailsVersion">Grails Version</a>

Built and tested with Grails 2.4.4 applications. May Port to 2.5.6 when I feel it is sufficiently complete.

## <a name="Build">Build</a>

 1. Clone the repository
 1. Build and install the plugin using the grails wrapper
 
 Windows:
 ```
grailsw.bat MavenInstall
```
Unix: 
 ```
./grailsw MavenInstall
```

## <a name="Install">Install</a>
Update BuildConfig.groovy:
```groovy

dependencies {
    compile 'org.grails.plugins:g2actuate:0.2-SNAPSHOT'
}

```


## <a name="Features">Features</a>

 * **Endpoints** G2Actuate endpoints allow you to monitor and interact with your application. G2Actuate includes a number 
of built-in endpoints. For example, the metrics endpoint provides basic application metrics information. Start up your 
application and look at `/metrics` (and see `/mappings` for a list of other HTTP endpoints).
 * **Metrics** (Partially complete)
 * **Audit** TODO...maybe
 * **Process Monitoring** TODO...maybe
 
## <a name="AvailableEndpoints">Available Endpoints</a>
 ID            | Description                                                                       | Sensitive Default | Enabled |
 ------------- | --------------------------------------------------------------------------------- | ----------------- | ------- |
| ``beans``    | Displays a complete list of all the Spring beans in your application.             | true              | true    |
| ``env``      | Exposes properties from the servlet/jvm and os   (TODO applicaion)                | true              | true    |
| ``health``   | Returns application health information.                                           | true              | true    |
| ``heapdump`` | Returns a GZip compressed hprof heap dump file.                                   | true              | true    |
| ``info``     | Displays arbitrary application info.                                              | false             | true    |
| ``loggers``  | Displays logger information                                                       | true              | true    |
| ``mappings`` | Displays a collated list of all URLMapiings paths.                                | true              | true    |
| ``metrics``  | Shows 'metrics' information for the current application.                          | true              | true    |
| ``trace``    | Displays trace information (by default the last 100 HTTP requests).               | true              | true    |
| ``shutdown`` | Shuts down the application gracefully (or ungracefully) -not enabled by default). | true              | true    |


> ![Tip](G2ActuateTiny.png ) **(TODO)** Depending on how an endpoint is exposed, the sensitive property may be used as a security 
hint. For example, sensitive endpoints will require a username/password when they are accessed over HTTP
 (or simply disabled if Spring Security is not enabled).

 
## <a name="Customizing Endpoints">Customization</a>

Endpoints can be customized using ''Config.groovy''', or by using a stand alone configuration file named 
``ActuateConfig.groovy``. You can change if an endpoint is ``enabled``, if it is considered ``sensitive`` 
and even its ``id``.

For example, here is an ``ActuateConfig.groovy`` that changes the sensitivity of the beans endpoint and also enables 
``shutdown`` using Groovy's [ConfigSlurper](http://groovy.codehaus.org/ConfigSlurper) syntax.

```groovy
 g2actuate.endpoints.beans.id = "springbeans"
 g2actuate.endpoints.beans.sensitive = false
 g2actuate.endpoints.shutdown.enabled = true
``` 

> ![Tip](G2ActuateTiny.png ) The prefix  ``g2actuate``.``endpoints`` + ``.`` + ``name`` is used to uniquely identify 
the endpoint that is being configured.
 
The above syntax works but it's quite repetitive and verbose. You can remove some of that verbosity by nesting 
properties at the dots:
 
```groovy
g2actuate {
    endpoints {
        beans {
           id = springbeans
           sensitive = false
        }
        shutdown {
            enabled = true
        }
    }
}          
```

By default, all endpoints except for ``shutdown`` are enabled. If you prefer to specifically “opt-in” endpoint 
enablement you can use the ``endpoints.enabled`` property. For example, the following will disable all endpoints 
except for info:

```groovy
g2actuate.endpoints.enabled=false
g2actuate.endpoints.info.enabled=true
```

Likewise, you can also choose to globally set the “sensitive” flag of all endpoints. By default, the sensitive flag 
depends on the type of endpoint (see the table above). For example, to mark all endpoints as sensitive except info: