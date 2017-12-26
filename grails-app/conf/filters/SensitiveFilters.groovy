package filters

import grails.util.Holders

import javax.servlet.http.HttpServletResponse

class SensitiveFilters {

    def config = Holders.config.g2actuate
    def endpoints = config.endpoints
    def cp = config.management.'context-path'
    def securityIsEnabled = config.management.security.enabled

    def beans = endpoints.beans
    def env = endpoints.env
    def health = endpoints.health
    def heapdump = endpoints.heapdump
    def info = endpoints.info
    def loggers = endpoints.loggers
    def mappings = endpoints.mappings
    def metrics = endpoints.metrics
    def shutdown = endpoints.shutdown
    def trace = endpoints.trace

    def authorizer

    def filters = {

        if (securityIsEnabled) {

            filterBeans(regex: true, find: true, uri: "${cp}${beans.path}") {
                before = {
                    if (beans.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }

//        filterDump(regex: true, find: true, uri: "${cp}${dump.path) {
//            before = {
//                if (dump.sensitive) {
//                    this.checkAuthorization(request, response)
//                }
//            }
//        }


            filterHealth(regex: true, find: true, uri: "${cp}${health.path}") {
                before = {
                    if (health.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }

            filterHeapdump(regex: true, find: true, uri: "${cp}${heapdump.path}") {
                before = {
                    if (heapdump.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }

            filterEnv(regex: true, find: true, uri: "${cp}${env.path}") {
                before = {
                    if (env.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }

            filterInfo(regex: true, find: true, uri: "${cp}${info.path}") {
                before = {
                    if (info.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }

// TODO add logfile
//        filterInfo(regex: true, find: true, uri: "${cp}${logfile.path}") {
//            before = {
//                if (logfile.sensitive) {
//                    this.checkAuthorization(request, response)
//                }
//            }
//        }

            filterLoggers(regex: true, find: true, "${cp}${loggers.path}") {
                before = {
                    if (loggers.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }

            filterMapings(regex: true, find: true, uri: "${cp}${mappings.path}") {
                before = {
                    if (mappings.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }

            filterMetrics(regex: true, find: true, uri: "${cp}${metrics.path}") {
                before = {
                    if (metrics.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }

            filterShutdown(regex: true, find: true, uri: "${cp}${shutdown.path}") {
                before = {
                    if (shutdown.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }

            filterTrace(regex: true, find: true, uri: "${cp}${trace.path}") {
                before = {
                    if (shutdown.sensitive) {
                        this.checkAuthorization(request, response)
                    }
                }
            }
        }
    }

    /**
     * Delegates to actuateAuthorizer Bean
     * @param request
     * @param response
     * @return 401 (unauthorized if unauthorized
     */
    def checkAuthorization(request, response) {

        // If the endpoint requested is sensitive...
        // ...then when the request comes over HTTP, we need to checkAuthorization if user is authorized
        if (!request.isSecure() && !authorizer.isUserAuthorized([request:request]) && response) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return false // stop the filter chain
        }
    }

}