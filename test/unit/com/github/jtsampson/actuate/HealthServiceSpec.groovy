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

import com.github.jtsampson.actuate.health.HealthAggregator
import com.github.jtsampson.actuate.health.MyHealthIndicator
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(HealthService)
class HealthServiceSpec extends Specification {

    static doWithSpring = {
        healthAggregator(HealthAggregator) // used by the servive
        myHealthIndicator(MyHealthIndicator)
        bob(MyHealthIndicator)
    }

    def cleanup() {
    }

    void "ISSUE-1 HealthIndicator names in /health"() {

        when:
        def result = service.collectHealth()

        then:
        result.containsKey('my')
        result.containsKey('bob')
        !result.containsKey('myHealthIndicator')
    }



    void "ISSUE-2 defaultsEnabled = false"() {
        given:

        when:
        service.defaultsEnabled = false
        def result = service.collectHealth()

        then:
        result.containsKey('application')
        !result.containsKey('bob')
        !result.containsKey('myHealthIndicator')
    }
}
