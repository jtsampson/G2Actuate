package com.github.jtsampson.actuate

import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.UrlMappingsArtefactHandler
import org.codehaus.groovy.grails.web.mapping.DefaultUrlMappingEvaluator
import org.codehaus.groovy.grails.web.mapping.ResponseCodeMappingData
import org.codehaus.groovy.grails.web.mapping.ResponseCodeUrlMapping
import org.codehaus.groovy.grails.web.mapping.UrlMapping
import org.springframework.util.Assert


class MappingService {

    def grailsApplication
    def servletContext

    def collectMappings() {

        def mappings = [:]

        def urlMappings = getAllMappings()


        for (urlMapping in getAllMappings()) {
            String path = urlMapping.urlData.urlPattern


            def details = [:]
            details['bean'] = urlMapping.controllerName
            details['allows'] = urlMapping.httpMethod

            def artefact = grailsApplication.getArtefactByLogicalPropertyName("Controller", urlMapping.controllerName)
            def bean = grailsApplication.mainContext.getBean(artefact.clazz.name)
            def methods = bean.getClass().methods
            for (def m in methods) {
                if (m.name == urlMapping.actionName) {
                    details['method'] = "public ${bean.getClass()}.${urlMapping.actionName}()"
                }
            }

            // RestControllers use 'responseFormats', lets add them to the results if they exist
            def fields = bean.getClass().declaredFields
            for (def field in fields) {
                if (field.name == 'responseFormats') {
                    field.setAccessible(true) // Avoid IllegalAccessException if not public
                    details['produces'] = field.get(bean)
                }
            }

            mappings."${path}" = details
        }

        mappings

    }

    /**
     * Returns a list of defined UrlMappings.
     * @return
     */
    private def getAllMappings() {
        def mappings = grailsApplication.getArtefacts(UrlMappingsArtefactHandler.TYPE)

        DefaultUrlMappingEvaluator evaluator = new DefaultUrlMappingEvaluator(servletContext)
        List<UrlMapping> allMappings = []

        List<UrlMapping> grailsClassMappings
        for (mapping in mappings) {
            if (Script.isAssignableFrom(mapping.getClazz())) {
                grailsClassMappings = evaluator.evaluateMappings(mapping.getClazz())
            } else {
                grailsClassMappings = evaluator.evaluateMappings(mapping.getMappingsClosure())
            }
            allMappings.addAll(grailsClassMappings)
        }

        allMappings.sort()
    }

    protected String establishUrlPattern(UrlMapping urlMapping) {
        if (urlMapping instanceof ResponseCodeUrlMapping) {
            def errorCode = "ERROR: "+ ((ResponseCodeMappingData)urlMapping.urlData).responseCode

            return errorCode
        }
        final constraints = urlMapping.constraints
        final tokens = urlMapping.urlData.tokens
        StringBuilder urlPattern = new StringBuilder(UrlMapping.SLASH)
        int constraintIndex = 0
        tokens.eachWithIndex { String token, int i ->
            boolean hasTokens = token.contains(UrlMapping.CAPTURED_WILDCARD) || token.contains(UrlMapping.CAPTURED_DOUBLE_WILDCARD)
            if (hasTokens) {
                String finalToken = token
                while(hasTokens) {
                    if (finalToken.contains(UrlMapping.CAPTURED_WILDCARD)) {
                        def constraint = constraints[constraintIndex++]
                        def prop = '\\${' + constraint.propertyName + '}'
                        finalToken = finalToken.replaceFirst(/\(\*\)/, prop)
                    }
                    else if (finalToken.contains(UrlMapping.CAPTURED_DOUBLE_WILDCARD)) {
                        def constraint = constraints[constraintIndex++]
                        def prop = '\\\${' + constraint.propertyName + '}**'
                        finalToken =  finalToken.replaceFirst(/\(\*\*\)/, prop)
                    }
                    hasTokens = finalToken.contains(UrlMapping.CAPTURED_WILDCARD) || finalToken.contains(UrlMapping.CAPTURED_DOUBLE_WILDCARD)
                }
                urlPattern << finalToken
            }
            else {
                urlPattern << token
            }

            if (i < (tokens.length-1)) {
                urlPattern << UrlMapping.SLASH
            }
        }
        if (urlMapping.urlData.hasOptionalExtension()) {
            final allConstraints = urlMapping.constraints
            def lastConstraint = allConstraints[-1]
            urlPattern << "(.\${${lastConstraint.propertyName})?"
        }

        return urlPattern.toString()
    }




}