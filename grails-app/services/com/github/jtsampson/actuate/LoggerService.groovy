package com.github.jtsampson.actuate

import grails.util.GrailsUtil
import org.apache.log4j.LogManager
import org.codehaus.groovy.grails.commons.GrailsClass


class LoggerService {

    def grailsApplication

    def collectLoggers() {

        def result = [:]

        result << [levels: levels()]
        result << [loggers: artifactLoggers() + nonArtifactLoggers()]
        result
    }

    def levels() {

        ['OFF', 'FATAL', 'ERROR', 'WARN', 'INFO', 'DEBUG', 'TRACE', 'ALL']
    }

    def nonArtifactLoggers() {
        LogManager.getCurrentLoggers().findAll {
            it.level != null
        }.collect {
            [(it.name): [configuredLevel: it?.level.toString() ?: 'OFF', effectiveLevel: it?.effectiveLevel.toString()]]
        }
    }

    // Adapted from https://github.com/tombdean/grails-runtime-logging/blob/master/grails-app/controllers/grails/plugin/runtimelogging/RuntimeLoggingController.groovy
    def artifactLoggers() {
        def logMapList = []

        // TODO Cache this call?
        // Get set of all registered artefact types.
        def artefactTypeSet = grailsApplication.getAllArtefacts().collect {
            grailsApplication.getArtefactType(it)?.type
        } as Set

        boolean grails1 = GrailsUtil.getGrailsVersion().startsWith('1')
        for (artefactType in artefactTypeSet.sort()) {
            for (GrailsClass gc in grailsApplication.getArtefacts(artefactType).sort { it.fullName }) {
                try {
                    def logger = grails1 ?
                            "grails.app.${artefactType.toLowerCase()}.${calculateLoggerName(gc.logicalPropertyName, artefactType)}" :
                            gc.clazz.log.name
                    def l = LogManager.getLogger(logger)
                    logMapList << [(logger.toString()): [configuredLevel: l.level?.toString() ?: 'OFF', effectiveLevel: l.getEffectiveLevel().toString()]]
                }
                catch (MissingPropertyException e) {
                    // ignore it and go on
                }
            }
        }
        logMapList
    }

    private def calculateLoggerName(name, artefactType) {
        // Domains just use the artefact name, controllers/services etc need "Controller"/"Service" etc appended
        artefactType.toLowerCase() == "domain" ? "${classCase(name)}" : "${classCase(name)}${artefactType}"
    }

}
