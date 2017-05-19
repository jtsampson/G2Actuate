class Grails2ActuatorUrlMappings {

    static mappings = {

        "/beans"    (controller: "actuator", action: "beans",    method: 'GET')
        "/heapdump" (controller: "heapDump", action: "heapdump", method: 'GET')
        "/env"      (controller: "actuator", action: "env",      method: 'GET')
        "/info"     (controller: "actuator", action: "info",     method: 'GET')
        "/mappings" (controller: "actuator", action: "mappings", method: 'GET')
        "/metrics"  (controller: "actuator", action: "metrics",  method: 'GET')
    }
}
