package filters

import java.util.concurrent.atomic.AtomicLong
import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

// TODO ignore trace calls in trace response.
class TraceFilters {

    private static final AtomicLong TRACE_COUNTER = new AtomicLong()
    private static final String TRACE_ID_ATTRIBUTE_NAME = 'Controller__TRACE_NUMBER__'

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
    
    // TODO replace with 'trace config' instead of putting 'configuration logic'  here.
    boolean enabled
    boolean collectCookies
    boolean collectProperties
    boolean collectRequestHeaders
    boolean collectResponseHeaders

    def includeRequestHeaders
    def includeResponseHeaders
    def excludeRequestHeaders
    def excludeResponseHeaders

    @PostConstruct
    def init() {
        assert grailsApplication
        def endpoints = grailsApplication?.config?.g2actuate?.endpoints
        def conf =  endpoints?.trace

        enabled                = conf?.enabled ?: true
        collectCookies         = conf?.cookies?.collect ?: true
        collectProperties      = conf?.properties?.collect ?: true
        collectRequestHeaders  = conf?.request?.headers?.capture ?: true
        collectResponseHeaders = conf?.response?.headers?.collect ?: true


        includeRequestHeaders  = conf?.request?.headers?.include ?: []
        includeResponseHeaders = conf?.response?.headers?.include ?: []


        excludeRequestHeaders  = conf?.request?.headers?.exclude ?: []
        excludeResponseHeaders = conf?.response?.headers?.exclude ?: []

        // TODO Add appropriate Error messages
        assert (includeRequestHeaders.isEmpty() | excludeRequestHeaders.isEmpty()),  "includeRequestHeaders: One, The Other but not Both"
        assert !(!includeResponseHeaders.isEmpty() && !excludeResponseHeaders.isEmpty()),  "excludeRequestHeaders: One, The Other but not Both"
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

                        // TODO case sensitivity?
                        if (includeRequestHeaders) {
                            headerNameList = headerNameList.intersect(includeRequestHeaders)
                        } else if (excludeRequestHeaders) {
                            headerNameList.removeAll(excludeRequestHeaders)
                        }

                        def requestHeaderMap = new TreeMap()
                        headerNameList.each { name -> requestHeaderMap.put(name, request.getHeader(name)) }
                        trace.headers.request = requestHeaderMap
                    }

                    if (collectProperties) {
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

                    // TODO: use auto intection of traceService if you can get the test working
                    def ts = applicationContext.getBean('traceService')
                    ts.save(currentRequestNumber, trace)
                }
            }

            after = { Map model ->
                if (enabled) {

                    Long id = request[TRACE_ID_ATTRIBUTE_NAME]

                    // TODO: use auto intection of traceService if you can get the test working
                    def ts = applicationContext.getBean('traceService')
                    def trace =ts.find(id)

                    if (trace) {

                        if (collectResponseHeaders) {
                            def headerNameList = request.headerNames.toList()

                            if (includeResponseHeaders) {
                                headerNameList = headerNameList.intersect(includeResponseHeaders)
                            } else if (excludeResponseHeaders) {
                                headerNameList.removeAll(excludeResponseHeaders)
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
