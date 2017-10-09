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

package filters

import grails.util.Holders

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

// TODO ignore trace calls in trace response.
class TraceFilters {

    private static final AtomicLong TRACE_COUNTER = new AtomicLong()
    private static final String TRACE_ID_ATTRIBUTE_NAME = 'Controller__TRACE_NUMBER__'

    def traceService
    //def dependsOn = [SensitiveFilters] // depending on other filters may vause issues with mocking in test.

    // if true, the request property is collected. User provides a set of overrides.
    // TODO cross this with user provided configuration
    static def allPropertyList = ['pathInfo',
                                  'pathTranslated',
                                  'contextPath',
                                  'userPrincipal',
                                  'parameters',
                                  'queryString',
                                  'authType',
                                  'remoteAddr',
                                  'sessionId',
                                  'remoteUser']


    boolean enabled
    boolean collectProperties
    boolean collectRequestCookies
    boolean collectRequestHeaders
    boolean collectResponseCookies
    boolean collectResponseHeaders

    def propertyList
    def includeRequestHeaders
    def includeResponseHeaders
    def excludeRequestHeaders
    def excludeResponseHeaders

    // TODO Think about using a 'TraceConfig' object so we can put config validation there.
    @PostConstruct
    def init() {
        def  grailsApplication = Holders
        def endpoints = grailsApplication?.config?.g2actuate?.endpoints
        def conf = endpoints?.trace

        enabled = conf.enabled == false ? conf.enabled : true

        collectProperties = conf.props.collect == false ? conf.props.collect : true
        collectRequestCookies = conf.request.cookies.collect == false ? conf.request.cookies.collect : true
        collectResponseCookies = conf.response.cookies.collect == false ? conf.response.cookies.collect : true
        collectRequestHeaders = conf.request.headers.collect == false ? conf.request.headers.collect : true
        collectResponseHeaders = conf.response.headers.capture == false ? conf.response.headers.capture : true


        propertyList = conf.props.list

        if (propertyList) {
            assert propertyList in List
            propertyList.each {
                assert it in String
                assert allPropertyList.contains(it)
            }
        } else {
            propertyList = allPropertyList
        }

        includeRequestHeaders = conf.request.headers.include ?: []
        includeResponseHeaders = conf.response.headers.include ?: []

        excludeRequestHeaders = conf.request.headers.exclude ?: []
        excludeResponseHeaders = conf.response.headers.exclude ?: []

        // TODO Add appropriate Error messages
        assert (includeRequestHeaders.isEmpty() | excludeRequestHeaders.isEmpty()), "include/exclude RequestHeaders: One, The Other but not Both"
        assert (includeResponseHeaders.isEmpty() | excludeResponseHeaders.isEmpty()), "include/exclude  ResponseHeaders: One, The Other but not Both"
    }


    def filters = {
        allButTrace(controller: 'trace', invert: true) {
            //recordTrace(controller:'*', action:'*') { // TODO...do you want to trace traces?
            //init()
            before = {

                if (enabled) {
                    long start = System.nanoTime()
                    long currentRequestNumber = TRACE_COUNTER.incrementAndGet()


                    def trace = [:]

                    trace.timestamp = start
                    trace.path = request.forwardURI // must use  forwardURI not requestURI
                    trace.method = request.method
                    trace.headers = [:]
                    trace.cookies = [:]

                    if (collectRequestHeaders) {

                        def headerNameList = request.headerNames.toList()

                        // TODO case sensitivity?
                        if (includeRequestHeaders) {
                            headerNameList = headerNameList.intersect(includeRequestHeaders)
                        } else if (excludeRequestHeaders) {
                            headerNameList.removeAll(excludeRequestHeaders)
                        }

                        headerNameList.remove('cookies') // we'll handle cookies separately

                        TreeMap requestHeaderMap = [:]
                        headerNameList.each { name -> requestHeaderMap.put(name, request.getHeader(name)) }
                        trace.headers.request = requestHeaderMap
                    }


                    if (collectRequestCookies) {
                        trace.cookies.request = request.cookies?.sort { it.name }
                    }

                    if (collectProperties) {

                        TreeMap propertyMap = [:]
                        def keys = propertyList.findAll {
                            !['userPrincipal', 'parameters', 'sessionId'].contains(it)
                        }.collect { it }

                        propertyMap = request.properties.subMap(keys)

                        if (propertyList.any { it == 'userPrincipal' }) {
                            propertyMap.userPrincipal = request?.userPrincipal?.name
                        }

                        if (propertyList.any { it == 'parameters' }) {
                            if (request.parameterMap) {
                                propertyMap.parameters = new LinkedHashMap<String, String[]>(request.getParameterMap())
                            } else {
                                propertyMap.parameters = null
                            }

                        }

                        if (propertyList.any { it == 'sessionId' }) {
                            propertyMap.sessionId = session.id
                        }

                        trace << propertyMap

                    }

//                    if (collectErrors){
//
//                    }


                    request[TRACE_ID_ATTRIBUTE_NAME] = currentRequestNumber

                    // TODO: use auto injection of traceService if you can get the test working
                    def ts = applicationContext.getBean('traceService')
                    //def ts = traceService
                    ts.save(currentRequestNumber, trace)
                }
            }

            after = { Map model ->
                if (enabled) {

                    Long id = request[TRACE_ID_ATTRIBUTE_NAME]

                    // TODO: use auto intection of traceService if you can get the test working
                    def ts = applicationContext.getBean('traceService')
                    //def ts = traceService
                    def trace = ts.find(id)

                    if (trace) {

                        if (collectResponseHeaders) {
                            def headerNameList = response.headerNames.toList()

                            if (includeResponseHeaders) {
                                headerNameList = headerNameList.intersect(includeResponseHeaders)
                            } else if (excludeResponseHeaders) {
                                headerNameList.removeAll(excludeResponseHeaders)
                            }

                            // maybe need to remove 'Set-Cookies' here.

                            def responseHeaders = [:]
                            headerNameList.each { name -> responseHeaders.put(name, response.getHeader(name)) }
                            trace.headers.response = responseHeaders

                        }

                        if (collectResponseCookies) {
                            def cookieList = response.getHeaders('Set-Cookie')
                            trace.cookies.response = cookieList.sort { it.name }
                        }

                        trace.status = response.status
                        trace.timeTaken = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - trace.timestamp)
                        ts.save(id, trace)
                    }
                }
            }


            afterView = { Exception e ->
                if (enabled) {
// TOD do
                }
            }
        }
    }

}
