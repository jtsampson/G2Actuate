# Grails 2 Actuator (g2actuator)
============
The Grails 2 Actuator is a grails plugin providing some of the capabilities of the [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready.html).

##Grails Version

Built and tested with Grails 2.4.4 applications. May Port to 2.5.6 when I feel it is sufficiently complete.

##Building the Actuator

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

##Install the Actuator
Update BuildConfig.groovy:
```groovy

dependencies {
    compile 'org.grails.plugins:g2actuator:3.3.2:0.2-SNAPSHOT'
}

```


## Features

 * **Endpoints** Grails 2 Actuator endpoints allow you to monitor and interact with your application. Grails 2 Actuator includes a number 
of built-in endpoints For example, the metrics endpoint provides basic application metrics information. Start up your 
application and look at '/metrics' (and see /mappings for a list of other HTTP endpoints).
 * **Metrics** TODO...maybe
 * **Audit** TODO...maybe
 * **Process Monitoring** TODO...maybe
 
## Endpoints
| ID            | Description                                                                | Sensitive Default |
| ------------- | -------------------------------------------------------------------------- | ----------------- |
| beans         | Displays a complete list of all the Spring beans in your application.      | TODO              |
| env           | Exposes properties from the servlet/jvm and os   (TODO applicaion)         | TODO              |
| heapdump      | Returns a GZip compressed hprof heap dump file.                            | TODO              |
| info          | Displays arbitrary application info.                                       | TODO              |
| metrics       | Shows 'metrics' information for the current application.                   | TODO              |
| mappings      | Displays a collated list of all URLMapiings paths                          | TODO              |
