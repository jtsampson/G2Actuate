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


g2actuate{
    reloadMarker = true
    autoDesensitize = true

    security.user.name='user'
    security.user.password='' // note: '' will be size=0 when accessed.

    management.'context-path'='' // note: '' will be size=0 when accessed.
    management {
        security {
            roles=["ACTUATOR"]
            enabled=true
        }
        health {
            defaults {
                enabled = false
            }
        }
    }

    endpoints {
        beans {
            id = "beans"
            path = "/${endpoints.beans.id}"
            enabled = true
            sensitive = true
            //mappingsClass = Grails2ActuateUrlMappings.BeansUrlMappings.class
        }

//        TODO add '/dump' thread dump
//        dump {
//            path = "/dump"
//            enabled = true
//            sensitive = true
//            // mappingsClass = Grails2ActuateUrlMappings.HeapdumpUrlMappings.class
//        }

        health {
            id = "health"
            path = "/${endpoints.health.id}"
            enabled = true
            sensitive = false
            timeToLive = 1000
            // mappingsClass = Grails2ActuateUrlMappings.HeapdumpUrlMappings.class
        }

        heapdump {
            id = "heapdump"
            path = "/${endpoints.heapdump.id}"
            enabled = true
            sensitive = true
           // mappingsClass = Grails2ActuateUrlMappings.HeapdumpUrlMappings.class
        }
        env {
            id = "env"
            path = "/${endpoints.env.id}"
            enabled = true
            sensitive = true
        }
        info {
            id = "info"
            path = "/${endpoints.info.id}"
            enabled = true
            sensitive = false
        }

//        TODO add logfile
//        logfile {
//            id = "logfile"
//            path = "/${endpoints.logfile.id}"
//            enabled = true
//            sensitive = true
//        }

        loggers {
            id = "loggers"
            path = "/${endpoints.loggers.id}"
            enabled = true
            sensitive = true
        }

        mappings {
            id = "mappings"
            path = "/${endpoints.mappings.id}"
            enabled = true
            sensitive = true
        }
        metrics {
            id = "metrics"
            path = "/${endpoints.metrics.id}"
            enabled = true
            sensitive = true
        }
        shutdown {
            id = "shutdown"
            path = "/${endpoints.shutdown.id}"
            enabled = false
            sensitive = true
            delay = 30
        }
        trace {
            id = "trace"
            path = "/${endpoints.trace.id}"
            enabled = true
            sensitive = true
            props.collect = true
            request {
                cookies.collect = true
                headers {
                    collect = true
                    include = []
                    exclude = []
                }
            }
            response {
                cookies.collect = true
                headers {
                    collect = true
                    include = []
                    exclude = []
                }
            }
        }
    }
}
