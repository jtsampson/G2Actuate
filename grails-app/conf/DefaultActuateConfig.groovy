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

    management.'context-path'=""
    management.security.roles="ACTUATOR"
    management.security.enabled=true

    endpoints {
        beans {
            path = "/beans"
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
            path = "/health"
            enabled = true
            sensitive = true
            // mappingsClass = Grails2ActuateUrlMappings.HeapdumpUrlMappings.class
        }

        heapdump {
            path = "/heapdump"
            enabled = true
            sensitive = true
           // mappingsClass = Grails2ActuateUrlMappings.HeapdumpUrlMappings.class
        }
        env {
            path = "/env"
            enabled = true
            sensitive = true
        }
        info {
            path = "/info"
            enabled = true
            sensitive = false
        }

//        TODO add logfile
//        logfile {
//            path = "/loggers"
//            enabled = true
//            sensitive = true
//        }

        loggers {
            path = "/loggers"
            enabled = true
            sensitive = true
        }

        mappings {
            path = "/mappings"
            enabled = true
            sensitive = true
        }
        metrics {
            path = "/metrics"
            enabled = true
            sensitive = true
        }
//        shutdown {
//            path = "/shutdown"
//            enabled = false
//        }
        trace {
            path = "/trace"
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
