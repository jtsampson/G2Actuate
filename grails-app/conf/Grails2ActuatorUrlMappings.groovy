class Grails2ActuatorUrlMappings {

    static mappings = {
        "/info"(controller: "actuator", action: "info", method: 'GET')
        "/metrics"(controller: "actuator", action: "metrics", method: 'GET')
        "/env"(controller: "actuator", action: "env", method: 'GET')
        "/dump"(controller: "heapDump", action: "dump", method: 'GET')
    }
}
