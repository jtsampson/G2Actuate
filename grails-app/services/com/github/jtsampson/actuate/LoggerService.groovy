package com.github.jtsampson.actuate

import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

class LoggerService {

    // TODO accounts for minimal set of logging levels, but custom levels are a possiblitiy.
    static def levelList = ['OFF', 'FATAL', 'ERROR', 'WARN', 'INFO', 'DEBUG', 'TRACE', 'ALL']

    /**
     * Returns a resource consisting of a
     *  levels : list of available logger levels
     *  loggers : list of available logger configurations
     * @return the loggersAsList
     */
    def collectLevelsAndLoggers() {

        def resource = [:]
        resource << [levels: levelList]
        resource << asLoggersMap(findAllLoggers())
        resource
    }

    /**
     * Returns the  asLogger if loggerName exits, else null.
     * @param id (id of logger)
     * @return the asLogger if loggerName exits, else null.
     */
    def collectLoggerConfig(String id) {
        def result
        if (Logger.exists(id)) {
            result = asLoggerConfig(LogManager.getLogger(id))
        } else {
            result = null
        }
        result
    }

    /**
     * Returns the updated config. Assumes logger with id exists, else creates a new one.
     * @param id (id of logger)
     * @return the updated config
     */
    def upsertLoggerConfig(String id, String configuredLevel) {

        def logger = LogManager.getLogger(id)
        logger.setLevel(Level.toLevel(configuredLevel));
        asLoggerConfig(logger)
    }

    def findAllLoggers() {
        LogManager.getCurrentLoggers().findAll {
            it.level != null
        }.sort { it.name }
    }

    /**
    * Builds the logger-map resource.
    * @param loggers
    * @return the logger-map resource.
    */
    def asLoggersMap(loggers) {
        [loggers: asLoggersList(loggers)]
    }

    /**
     * Builds the logger list resource.
     * @param list of loggers
     * @return the logger list resource.
     */
    def asLoggersList(loggers) {
        loggers.collect {
            asLogger(it)
        }
    }

    /**
     * Builds  a logger resource.
     * @param logger
     * @return a logger resource.
     */
    def asLogger(logger) {
        [(logger.name): asLoggerConfig(logger)

        ]
    }

    /**
     * Builds  a logger-config resource.
     * @param logger
     * @return a logger-config resource.
     */
    def asLoggerConfig(logger) {
        [configuredLevel: logger?.level?.toString(),
         effectiveLevel : logger?.effectiveLevel?.toString()]
    }


}
