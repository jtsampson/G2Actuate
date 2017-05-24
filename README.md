<p align="center">
  <a href="https://github.com/jtsampson/g2actuate">
    <img src="G2ActuateSmall.png" width=144 height=144>
  </a>

  <h3 align="center">G2Actuate</h3>

  <p align="center">
    Providing the porduction ready capabilities of the Spring Boot Actuator to Grails 2 Projects.

    <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready.html"><strong>Spring Boot Actuator</strong></a>
  </p>
</p>
b2

The Grails 2 Actuator is a Grails 2 plugin providing some of the capabilities of the [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready.html).

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

 * **Endpoints** Grails 2 Actuator endpoints allow you to monitor and interact with your application. Grails 2 Actuator includes a number 
of built-in endpoints For example, the metrics endpoint provides basic application metrics information. Start up your 
application and look at `/metrics` (and see `/mappings` for a list of other HTTP endpoints).
 * **Metrics** TODO...maybe
 * **Audit** TODO...maybe
 * **Process Monitoring** TODO...maybe
 
## <a name="AvailableEndpoints">Available Endpoints</a>
| ID                 | Description                                                                | Sensitive Default |
| ------------------ | -------------------------------------------------------------------------- | ----------------- |
| **/beans**         | Displays a complete list of all the Spring beans in your application.      | TODO              |
| **/env**           | Exposes properties from the servlet/jvm and os   (TODO applicaion)         | TODO              |
| **/heapdump**      | Returns a GZip compressed hprof heap dump file.                            | TODO              |
| **/info**          | Displays arbitrary application info.                                       | TODO              |
| **/metrics**       | Shows 'metrics' information for the current application.                   | TODO              |
| **/mappings**      | Displays a collated list of all URLMapiings paths                          | TODO              |

## <a name="Customizing Endpoints">Customization</a>