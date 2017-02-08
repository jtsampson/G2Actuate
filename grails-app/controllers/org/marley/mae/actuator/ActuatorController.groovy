package org.marley.mae.actuator

import grails.converters.JSON
import grails.converters.XML

class ActuatorController {

    ActuatorController() {
        JSON.createNamedConfig('PASS-THROUGH-JSON') {
            it.registerObjectMarshaller(String) {  ->
                it.toString()
            }
        }
    }

    //static responseFormats = ['json','xml']
    // static allowedMethods = [info: "GET",
    //                          metrics: 'GET',
    //                         env: 'GET']
    def infoService
    def metricService
    def envService
    def heapService
    def beanService

    def info() {
        respond infoService.collectInfo()
        // TODO: Can I support both XML and JSON for all what about /beans endpoint?
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
        respond heapService.dumpIt(params?.live ?: true)
    }

    def beans(){
        response.setContentType("application/json");
        JSON.use('PASS-THROUGH-JSON'){
            render beanService.collectBeans();
        }
    }
}
