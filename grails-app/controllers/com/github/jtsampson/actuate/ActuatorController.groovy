package com.github.jtsampson.actuate

import grails.converters.JSON

class ActuatorController {

    ActuatorController() {
        JSON.createNamedConfig('PASS-THROUGH-JSON') {
            it.registerObjectMarshaller(String) {  ->
                it.toString()
            }
        }
    }

    static responseFormats = ['json', 'xml']

    static allowedMethods = [actuator   : 'GET',
                             beans      : 'GET',
                             configprops: 'GET',
                             env        : 'GET',
                             heapdump   : 'GET',
                             info       : 'GET',
                             loggers    : 'GET',
                             mappings   : 'GET',
                             metrics    : 'GET',
                             trace      : 'GET'
    ]

    def beanService
    def envService
    def heapDumpService
    def infoService
    def loggerService
    def mappingService
    def metricService


    // TODO enabling/disabling should be handled in plugin set up.

    def actuator() {
        //TODO
    }

    def beans(){
        respond beanService.collectBeans()
    }

    def configprops(){
        //TODO
    }

    def env() {
        respond envService.collectEnvironment()
    }

    def heapdump() {
        respond heapDumpService.dumpIt(params?.live ?: true)
    }

    def info() {
        respond infoService.collectInfo()
    }

    def loggers(){
        respond loggerService.collectLoggers()
    }

    def mappings() {
        respond mappingService.collectMappings()
    }

    def metrics() {
        respond metricService.collectMetrics()
    }
}
