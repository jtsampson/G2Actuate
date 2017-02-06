package org.marley.mae.actuator

class ActuatorController {

    static responseFormats = ['json']
    // static allowedMethods = [info: "GET",
    //                          metrics: 'GET',
    //                         env: 'GET']
    def infoService
    def metricService
    def envService
    def heapService

    def info() {
        respond infoService.collectInfo()
        //        def model = infoService.collectInfo()
//        withFormat {
//            json { render model as JSON }
//            xml { render model as XML }
//        }
    }

    def metrics() {
        respond metricService.collectMetrics()
    }

    def env() {
        respond envService.collectEnvironment()
    }

    def dump() {
        respond heapService.dumpIt()
    }
}
