package filters

import grails.util.Holders

import javax.servlet.http.HttpServletResponse
import java.util.regex.Pattern

class SensitiveFilters {

    def config = Holders.config.g2actuate
    def endpoints = config.endpoints
    def cp = config.management.'context-path'

    def beans = endpoints.beans
    def env = endpoints.env
    def heapdump = endpoints.heapdump
    def info = endpoints.info
    def loggers = endpoints.loggers
    def mappings = endpoints.mappings
    def metrics = endpoints.metrics
    def trace = endpoints.trace

    def actuateSecurity

    def filters = {



        //StringBuilder builder = new StringBuilder()
        def sensitivePaths  = []
        //builder.append "("
        endpoints.each{
            if (it.value.sensitive ){
                sensitivePaths.add("$cp${it.value.path}")
            }
        }

        def pattern  =   "(" + sensitivePaths.collect {Pattern.quote(it)}.join('|') + ")"
        println pattern

        sensitive(regex:true,  find: pattern ) {
            before = {
                println 'before sensitive filter '
                println  'secure:     ' + request.isSecure()
                println  'authorized: ' + actuateSecurity.isUserAuthorized(request)
                // if HTTP AND sensitive, we need to check if user is authorized
//                if (!request.isSecure() && !actuateSecurity.isUserAuthorized(request)) {
//                    response.status = HttpServletResponse.SC_UNAUTHORIZED
//
//                }
            }
            after = { Map model ->
                println 'after  sensitive filter '
            }
            afterView = { Exception e ->

            }
        }
    }
}
