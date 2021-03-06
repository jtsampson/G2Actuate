[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/jtsampson/G2Actuate.svg?branch=master)](https://travis-ci.org/jtsampson/G2Actuate)
[![Coverage Status](https://coveralls.io/repos/github/jtsampson/G2Actuate/badge.svg?branch=master)](https://coveralls.io/github/jtsampson/G2Actuate?branch=master)
[![Download](https://api.bintray.com/packages/jtsampson/grails-plugins/G2ActuatePlugin/images/download.svg?version=1.0.2) ](https://bintray.com/jtsampson/grails-plugins/G2ActuatePlugin/1.0.2/link)

<p align="center">
      <h1 style="color:#990000" align="center">G2Actuate</h1>
      <p align="center">
              Providing production ready capabilities to Grails 2 Projects.
            </p>
      <p align="center">
      <a href="https://github.com/jtsampson/g2actuate">
        <img src="G2ActuateSmall.png" width=144 height=144>
      </a>
      </p>
      <hr>
</p>

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Grails Versions](#grails-versions)
- [Run](#run)
- [Use](#use)
- [Credit](#credit)
- [Endpoints](#endpoints)
  - [Customization](#customization)
  - [Hypermedia for actuator endpoints](#hypermedia-for-actuator-endpoints)
  - [CORS support](#cors-support)
  - [Adding Custom Endpoints](#adding-custom-endpoints)
  - [Contributing to Endpoints](#contributing-to-endpoints)
  - [Health information](#health-information)
  - [Security with HealthIndicators](#security-with-healthindicators)
    - [Pre-configured HealthIndicators](#pre-configured-healthindicators)
    - [Writing custom HealthIndicators](#writing-custom-healthindicators)
  - [Application Information](#application-information)
    - [Pre-configured InfoContributors](#pre-configured-infocontributors)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


## Grails Versions

Built and tested with Grails 2.4.4 applications.

## Run

 1. Clone the repository
 1. Run the plugin using the grails wrapper
 
 Windows:
```
grailsw.bat run-app
```
Unix: 
```
./grailsw run-app
```

## Use
The plugin is currently hosted on BinTray.
 
Update your project's ``BuildConfig.groovy``:
```groovy

repositories {
    inherits true 
    // ...
    mavenRepo  "https://bintray.com/jtsampson/grails-plugins"
}


plugins {
    compile 'org.grails.plugins:g2-actuate:1.0.2'
}

```

## Endpoints

G2Actuate endpoints allow you to monitor and interact with your application.
G2Actuate includes a number of built-in endpoints. For example, the ``health``
endpoint provides basic application health information information.

The endpoints are mainly exposed with HTTP where the ID of the endpoint
is mapped to a URL. For example, by default, the ``health`` endpoint
will be mapped to ``/health``.

The following technology are available:

 ID            | Description                                                                       | Sensitive Default | Enabled |
 ------------- | --------------------------------------------------------------------------------- | ----------------- | ------- |
| ``beans``    | Displays a complete list of all the Spring beans in your application.             | true              | true    |
| ``env``      | Exposes properties from the servlet/jvm and os   (TODO application)                | true              | true    |
| ``health``   | Returns application health information.                                           | true              | true    |
| ``heapdump`` | Returns a GZip compressed hprof heap dump file.                                   | true              | true    |
| ``info``     | Displays arbitrary application info.                                              | false             | true    |
| ``loggers``  | Displays logger information                                                       | true              | true    |
| ``mappings`` | Displays a collated list of all URLMappings paths.                                | true              | true    |
| ``metrics``  | Shows 'metrics' information for the current application.                          | true              | true    |
| ``trace``    | Displays trace information (by default the last 100 HTTP requests).               | true              | true    |
| ``shutdown`` | Shuts down the application gracefully (or ungracefully) -not enabled by default). | true              | true    |


> ![Tip](G2ActuateTiny.png ) **(TODO)** Depending on how an endpoint is exposed, the sensitive property may be used as a security 
hint. For example, sensitive endpoints will require a username/password when they are accessed over HTTP
 (or simply disabled if Spring Security is not enabled).

 
### Customization

Endpoints can be customized using ``Config.groovy``, or by using a stand alone configuration file named 
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

Likewise, you can also choose to globally set the ``sensitive`` flag of all endpoints. By default, the sensitive flag 
depends on the type of endpoint (see the table above). For example, to mark all endpoints as sensitive except info:

```groovy
g2actuate.endpoints.sensitive=true
g2actuate.endpoints.info.sensitive=false
```


### Hypermedia for actuator endpoints

Would you find this useful? Please add an enhancement request or contribute.

### CORS support

Would you find this useful? Please add an enhancement request or contribute.

### Adding Custom Endpoints

Would you find this useful? Please add an enhancement request or contribute.

### Contributing to Endpoints

Additional data can be exposed to the Metrics and Info Endpoints by annotating Grails artefacts with the 
``MetricContrib`` or ``InfoContrib`` annotations.  For example, to expose  extra metrics and info from a 
``Controller``:

```groovy
class MyController {
    
    @InfoContrib(key='my.controller')
    def extraInfo(){
        [ author : 'bob']
    }
    
    @MetricContrib(key='my.controller')
    def extraMetrics(){
        [ 'hits' : metric()] // gather some specific metric   
    }
}
```
> ![Tip](G2ActuateTiny.png ) Note: Annnotated methods must return a Map. Annnotated methods be contained in Grails artefacts.
 
### Health information

Health information can be used to check the status of your running application. It is often used by monitoring software 
to alert someone if a production system goes down. The default information exposed by the ``health`` endpoint depends on
how  it is accessed. For an unauthenticated connection in a secure application a simple ‘status’ message is returned, and 
for an authenticated connection additional details are also displayed.

Health information is collected from all ``HealthIndicator`` beans defined in your ``ApplicationContext``. 
G2Actuate includes a ``DataSourceHealthIndicator`` and you can also write your own. By default, the final system state 
is derived by the ``HealthAggregator`` which returns ``Health.Status.UP`` if all ``HealthStatus`` are up. Otherwise it 
returns ``Health.Status.DOWN``. 

> ![Tip](G2ActuateTiny.png ) Note:  The ``HealthAggregator``  in Spring Boot derives the  final system state in a 
different manner by first sorting the statuses from each ``HealthIndicator`` based on an ordered list of statuses. 
The first status in the sorted list is used as the overall health status. If no ``HealthIndicator`` returns a status 
that is known to the ``HealthAggregator``, an ``UNKNOWN`` status is used.

### Security with HealthIndicators

Information returned by HealthIndicators is often somewhat sensitive in nature. For example, you probably don’t want to 
publish details of your database server to the world. For this reason, by default, only the health status is exposed 
over an unauthenticated HTTP connection. If you are happy for complete health information to always be exposed you can 
set ``endpoints.health.sensitive`` to false.

Health responses are also cached to prevent “denial of service” attacks. Use the ``endpoints.health.timeToLive`` property 
if you want to change the default cache period of 1000 milliseconds.

#### Pre-configured HealthIndicators
The following ``HealthIndicators`` are auto-configured by the G2Actuate plugin when appropriate:

 Name                               | Description                                                         |
 ---------------------------------- | ------------------------------------------------------------------- |
| ``DataScourceHealthIndicator``    | Checks that a connection to ``DataSource`` can be obtained.         |

 > ![Tip](G2ActuateTiny.png ) It is possible to disable them all using the ``management.health.defaults.enabled`` property.

#### Writing custom HealthIndicators
To provide custom health information you can register Spring beans that implement the ``HealthIndicator`` interface. 
You need to provide an implementation of the ``health()`` method and return a Health response. The Health response should 
include a status and can optionally include additional details to be displayed.

```groovy
import com.github.jtsampson.actuate.health.Health
import com.github.jtsampson.actuate.health.HealthIndicator

class MyHealthIndicator implements HealthIndicator {

    @Override
    Health health() {
        Health health = new Health([name: 'myIndicator'])
        int errorCode = check(); // perform some specific health check
        if (errorCode != 0) {
            health.status = Health.Status.UP
        }else{
            health.status = Health.Status.DOWN
        }

        health
    }
}
```

Then you can wire this indicator in ``resources.groovy`` using Spring Bean DSL:

```groovy
import my.company.MyHealthIndicator

beans = {
    myHealthIndicator(MyHealthIndicator)
}
```

> ![Tip](G2ActuateTiny.png ) The identifier for a given ''HealthIndicator''' is the name of the bean without the
 ''HealthIndicator''' suffix if it exists. In the example above, the health information will be available in an entry 
 named ''my'''.

 ### Application Information
 Application information exposes various information collected from all ``InfoContributor`` beans defined in your
 ApplicationContext. G2Actuate includes a number of auto-configured ``InfoContributors`` and you can also write your own.

 #### Pre-configured InfoContributors
 | Name                               | Description                                     |
  ---------------------------------- | ------------------------------------------------ |
 | ``TDB``    | Add an enhancement request if you would like this functionality         |

## Bugs or Enhancements
To report any bug, please use the project [Issues](https://github.com/jtsampson/G2Actuate/issues) section on GitHub.

## Credits

Since this plugin is designed to mimic Spring Boot Actuator functionality (but for Grails 2),  the documentation is
patterned after chapter 47 of the [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html)
and should seem familiar to developers who are acquainted with the Spring Boot Actuator.

This project uses:
  * [Grails 2.4.4] (http://docs.grails.org/2.4.4/)
  * [Travis CI](https://travis-ci.org/)
  * [Coveralls](https://coveralls.io/)
  * [Test Code Coverage Plugin](https://grails.org/plugin/code-coverage)
  * [Coveralls.io Grails Plugin](https://grails.org/plugin/coveralls)
  * [Doctoc](https://github.com/thlorenz/doctoc) for TOC generation
