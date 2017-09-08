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

import filters.TraceFilters

/**
 * Created by jsampson on 9/8/2017.
 */
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(TraceController)
@Mock([TraceFilters, DummyController])
class TraceFiltersTests extends Specification {

    static doWithSpring = {
        traceService TraceService
    }



    def setup() {


    }

    void "test filter injection"() {


        when:
        def tc =
        withFilters(controller: "any", action: "index") {
            mocks.index()
        }


        then:
        response.text == 'Nothing Special'
    }
}
