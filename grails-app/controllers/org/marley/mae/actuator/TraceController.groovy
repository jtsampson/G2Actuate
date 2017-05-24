package org.marley.mae.actuator

class TraceController {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [trace : 'GET']

    def traceService

    def trace() {
        respond traceService.collectTraces()
    }
}
