package filters

import java.util.concurrent.atomic.AtomicLong
import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit;

// TODO ignore trace calls in trace response.
class TraceFilters {

    private static final AtomicLong TRACE_COUNTER = new AtomicLong()
    private static final String TRACE_ID_ATTRIBUTE_NAME = 'Controller__TRACE_NUMBER__'

    def traceService
    def grailsApplication

    // if true, the request property is collected. User provides a set of overrides.
    // TODO cross this with user provided configuration.
    def requestProperties = [pathInfo      : true,
                             cookie        : true,
                             pathTranslated: true,
                             contextPath   : true,
                             userPrincipal : true,
                             parameters    : true,
                             queryString   : true,
                             authType      : true,
                             remoteAddr    : true,
                             sessionId     : true,
                             remoteUser    : true]
    
    boolean enabled
    boolean collectRequestHeaders = true
    boolean collectRequestProperties = true
    boolean collectResponseHeaders = true
    boolean collectCookies = true

    def requestHeaderIncludes
    def responseHeaderIncludes
    def requestHeaderExcludes
    def responseHeaderExcludes

    @PostConstruct
    def init() {
        def endpoints = grailsApplication.config?.g2actuator?.endpoints
        def trace = endpoints?.trace
        assert traceService

        enabled = endpoints?.enabled ?: trace?.enabled ?: true

        collectRequestHeaders = trace?.collectResponseHeaders ?: true
        collectResponseHeaders = trace?.collectResponseHeaders ?: true

        requestHeaderIncludes = trace?.requestHeaderIncludes ?: null
        responseHeaderIncludes = trace?.responseHeaderIncludes ?: null

        requestHeaderExcludes = trace?.requestHeaderExcludes ?: null
        responseHeaderExcludes = trace?.responseHeaderExcludes ?: null
    }


    def filters = {
        all(controller: '*', action: '*') {
            before = {
                if (enabled) {
                    long start = System.nanoTime()
                    long currentRequestNumber = TRACE_COUNTER.incrementAndGet()


                    def trace = [:]

                    trace.timestamp = start
                    trace.path = request.forwardURI // must use  forwardURI not requestURI
                    trace.method = request.method
                    trace.headers = [:]

                    if (collectRequestHeaders) {

                        def headerNameList = request.headerNames.toList()

                        if (requestHeaderIncludes) {
                            headerNameList = headerNameList.collect { requestHeaderIncludes.contsins(it.toLowerCase()) }
                        } else if (requestHeaderExcludes) {
                            headerNameList = headerNameList.removeAll(requestHeaderExcludes.contains(it.toLowerCase()))
                        }

                        def requestHeaders = [:]
                        headerNameList.each { name -> requestHeaders.put(name, request.getHeader(name)) }
                        trace.headers.request = requestHeaders
                    }

                    if (collectRequestProperties) {
                        trace << request.properties.subMap(requestProperties.keySet()).findAll {
                            requestProperties[it.key] && it.value && it.key != 'userPrincipal'
                        }

                        if (requestProperties.userPrincipal && request?.userPrincipal?.name) {
                            trace.userPrincipal = request.userPrincipal.name
                        }

                        if (requestProperties.parameters && request.parameterMap) {
                            trace.parameters = new LinkedHashMap<String, String[]>(request.parameterMap)
                        }

                        if (requestProperties.sessionId && session?.id) {
                            trace.sessionId = session.id
                        }

                    }

//                    if (collectErrors){
//
//                    }


                    request[TRACE_ID_ATTRIBUTE_NAME] = currentRequestNumber

                    traceService.save(currentRequestNumber, trace)
                }
            }

            after = { Map model ->
                if (enabled) {


                    Long id = request[TRACE_ID_ATTRIBUTE_NAME]

                    def trace = traceService.find(id)

                    if (trace) {

                        if (collectResponseHeaders) {
                            def headerNameList = []

                            if (requestHeaderIncludes) {
                                headerNameList = requestHeaderIncludes
                            } else if (requestHeaderExcludes) {
                                headerNameList = request.headerNames.toList().removeAll(requestHeaderExcludes)
                            } else {
                                headerNameList = request.headerNames.toList()
                            }

                            if (!collectCookies) {
                                headerNameList.removeAll { it.toLowerCase() == 'set-cookies' }
                            }


                            def responseHeaders = [:]
                            headerNameList.each { name -> responseHeaders.put(name, request.getHeader(name)) }
                            trace.headers.response = responseHeaders
                        }

                        trace.status = response.status
                        trace.timeTaken = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - trace.timestamp);
                        traceService.save(id, trace)
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
