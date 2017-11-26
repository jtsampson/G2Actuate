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

import com.github.jtsampson.actuate.security.ActuateSecurity
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import spock.lang.Specification
import spock.lang.Unroll

class ActuateSecuritySpec extends Specification {

    @Unroll
    void "security allows role #role"() {

        given:
        def security = new ActuateSecurity()
        def request = new GrailsMockHttpServletRequest()
        request.addUserRole(role)

        expect:
        security.isUserAuthorized(request) == isAuthed

        where:
        role       | isAuthed
        'ACTUATOR' | true
        'BOB'      | false

    }
}
