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

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.util.Environment
import spock.lang.Specification

/**
 * DefaultActuateConfig tests
 */
@TestMixin(GrailsUnitTestMixin)
class DefaultActuateConfigSpec extends Specification{

    ConfigObject conf

    def setup(){
        ConfigSlurper slurper = new ConfigSlurper(Environment.getCurrent().getName())
        conf = slurper.parse(grailsApplication.classLoader.loadClass("DefaultActuateConfig"))
    }

    void "test sensitive sefaults"() {

        expect:
        conf.g2actuate.endpoints.beans.sensitive == true
        conf.g2actuate.endpoints.heapdump.sensitive == true
        conf.g2actuate.endpoints.env.sensitive == true
        conf.g2actuate.endpoints.info.sensitive == false
        conf.g2actuate.endpoints.loggers.sensitive == true
        conf.g2actuate.endpoints.mappings.sensitive == true
        conf.g2actuate.endpoints.trace.sensitive == true
        conf.g2actuate.endpoints.beans.sensitive == true

    }

    void "test enabled defaults"() {

        expect:
        conf.g2actuate.endpoints.beans.enabled == true
        conf.g2actuate.endpoints.heapdump.enabled == true
        conf.g2actuate.endpoints.env.enabled == true
        conf.g2actuate.endpoints.info.enabled == true
        conf.g2actuate.endpoints.loggers.enabled== true
        conf.g2actuate.endpoints.mappings.enabled == true
        conf.g2actuate.endpoints.trace.enabled == true
        conf.g2actuate.endpoints.beans.enabled == true

    }


}
