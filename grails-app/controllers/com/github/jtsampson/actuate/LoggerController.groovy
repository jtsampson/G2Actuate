package com.github.jtsampson.actuate

import org.grails.databinding.bindingsource.InvalidRequestBodyException
import org.springframework.http.HttpStatus

class LoggerController {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [index: 'GET', show: 'GET', update: 'PUT']

    def loggerService

    def index() {
        respond loggerService.collectLevelsAndLoggers()
    }

    def show() {
        respond loggerService.collectLoggerConfig(params.id)
    }

    def update() {

        if (params.id == null) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        def body = request.getJSON();
        assert(loggerService.levelList.contains(body.configuredLevel) )
        respond loggerService.upsertLoggerConfig(params.id, body.configuredLevel)
    }

    def handleRuntimeException(InvalidRequestBodyException e) {
        respond 422
    }
}
