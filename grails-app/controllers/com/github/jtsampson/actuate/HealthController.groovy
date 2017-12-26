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

import grails.converters.JSON
import grails.converters.XML
import com.github.jtsampson.actuate.security.ActuateAuthorizer

/**
 * Shows application health information (a simple ‘status’ when accessed over an unauthenticated connection or full
 * message details when authenticated). It is not sensitive by default.
 */
class HealthController {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [health: 'GET']
    def healthService
    def actuateAuthorizer


    def health() {

        def health = healthService.collectHealth()

        if (request.isSecure()) {
            if (actuateAuthorizer.isUserAuthorized([request: request])) {
                respondWithFormat health
            } else {
                render(status: 401) // unauthorized
            }
        } else {
            respondWithFormat health.subMap(['status'])
        }
    }

    def respondWithFormat(result) {

        request.withFormat {
            json {
                render result as JSON
            }
            xml {
                render result as XML
            }
        }
    }

}
