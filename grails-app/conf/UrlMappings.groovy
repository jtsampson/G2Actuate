class UrlMappings {

    static mappings = {


        "/beans"(controller: "actuator", action: "beans", method: 'GET')
        "/env"(controller: "actuator", action: "env", method: 'GET')
        "/info"(controller: "actuator", action: "info", method: 'GET')
        "/metrics"(controller: "actuator", action: "metrics", method: 'GET')

        "/dump"(controller: "heapDump", action: "dump", method: 'GET')
    }
}
