package filters

import grails.util.Holders

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

    def filters = {



        //StringBuilder builder = new StringBuilder()
        def sensitivePaths  = []
        //builder.append "("
        endpoints.each{
            if (it.value.enabled){
                sensitivePaths.add("$cp${it.value.path}")
            }
        }

        def pattern  =   "(" + sensitivePaths.collect {Pattern.quote(it)}.join('|') + ")"
        println pattern

        sensitive(regex:true,  find: pattern ) {
            before = {
                println 'before'
            }
            after = { Map model ->
                println 'after'
            }
            afterView = { Exception e ->

            }
        }
    }
}
