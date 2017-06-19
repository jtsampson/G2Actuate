class Grails2ActuatorUrlMappings {

    static mappings = {

        //"/actuator" (controller: "actuator", action: "actuator", method: 'GET')
        "/beans"          (controller: "actuator", action: "beans",    method: 'GET')
        "/heapdump"       (controller: "heapDump", action: "heapdump", method: 'GET')
        "/env"            (controller: "actuator", action: "env",      method: 'GET')
        "/info"           (controller: "actuator", action: "info",     method: 'GET')

        "/loggers"        (controller: "logger",   action: "index",    method: 'GET')
        "/loggers/$id"    (controller: "logger",   action: "show",     method: 'GET')
        "/loggers/$id"    (controller: "logger",   action: "update",   method: 'PUT')

        "/mappings"       (controller: "actuator", action: "mappings", method: 'GET')
        "/metrics"        (controller: "actuator", action: "metrics",  method: 'GET')
        "/trace"          (controller: "trace",    action: "trace",    method: 'GET')
    }
}
