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

import grails.test.spock.IntegrationSpec
import org.springframework.http.MediaType

class HeapDumpControllerIntegrationSpec extends IntegrationSpec {

    void "/heapdump serves octest-stream"() {
        given:
        def controller = new HeapDumpController()
        controller.request.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString())

        when:
        controller.heapdump()

        then:
        controller.response.status == 200
        controller.response.contentType == MediaType.APPLICATION_OCTET_STREAM.toString()
        controller.response.header("Content-disposition").startsWith("attachment; filename=\"heapdump")
    }

    void "/heapdump serves live objects"() {
        given:
        def controller = new HeapDumpController()
        controller.request.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString())
        controller.request.live = true

        when:
        controller.heapdump()

        then:
        controller.response.status == 200
        controller.response.contentType == MediaType.APPLICATION_OCTET_STREAM.toString()
        controller.response.header("Content-disposition").startsWith("attachment; filename=\"heapdump")
    }

//    void "/heapdump limits requests"() {
//        given:
//        def controller = new HeapDumpController()
//        controller.request.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString())
//
//
//        when:
//        controller.heapdump()
//        controller.response.reset()
//        controller.heapdump()
//
//        then:
//        controller.response.status == 429
//    }
}
