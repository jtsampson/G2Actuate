/**
 * Created by jsampson on 6/20/2017.
 */
g2actuate{
    configMerged = true
    endpoints {
        beans {
            enabled = true
            sensitive = true
            //mappingsClass = Grails2ActuateUrlMappings.BeansUrlMappings.class
        }
        heapdump {
            enabled = true
            sensitive = true
           // mappingsClass = Grails2ActuateUrlMappings.HeapdumpUrlMappings.class
        }
        env {
            enabled = true
            sensitive = true
        }
        info {
            enabled = true
            sensitive = true
        }

        loggers {
            enabled = true
            sensitive = true
        }

        mappings {
            enabled = true
            sensitive = true
        }
        metrics {
            enabled = true
            sensitive = true
        }
        trace {
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
