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

import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

/**
 * A service to expose and mutate the application's logger levels.
 * @author jsampson
 */
class LoggerService {

    // TODO accounts for minimal set of logging levels, but custom levels are a possiblitiy.
    static final def levelList = ['OFF', 'FATAL', 'ERROR', 'WARN', 'INFO', 'DEBUG', 'TRACE', 'ALL']

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
        logger.setLevel(Level.toLevel(configuredLevel))
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
