/*
 *  Copyright 2016-2017 John Sampson
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
