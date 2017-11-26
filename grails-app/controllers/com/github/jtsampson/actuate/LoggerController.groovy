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

import grails.validation.Validateable
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.validation.MapBindingResult

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
            respond null, [status: 404]
        } else {

            ConfiguredLevel level = new ConfiguredLevel(request.getJSON())
            level.check()

            if (level.hasErrors()) {
                respond level.errors, [status: 422]
            } else {
                respond loggerService.upsertLoggerConfig(params.id, level.wrapper.configuredLevel)
            }
        }
    }


    @Validateable
    class ConfiguredLevel {
        Map wrapper = [:]

        def ConfiguredLevel(body) {
            wrapper.putAll(body)
        }

        def check() {
            setErrors(new MapBindingResult(wrapper, ConfiguredLevel.class.getName()))
            ConstrainedProperty constrainedProperty = new ConstrainedProperty(ConfiguredLevel, "configuredLevel", String)
            constrainedProperty.applyConstraint("inList", LoggerService.levelList)
            constrainedProperty.validate(this, wrapper.configuredLevel, errors)

            return errors
        }
    }
}
