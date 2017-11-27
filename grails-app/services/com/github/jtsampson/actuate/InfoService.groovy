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

import com.github.jtsampson.actuate.annotations.InfoContrib
import grails.util.Environment
import grails.util.Holders
import grails.util.Metadata
import groovy.json.JsonSlurper

import javax.annotation.PostConstruct

/**
 * A service to expose informational data.
 * currently:
 *  - application
 *  - grails (plugins)
 *  - scm (build info)
 *
 * @author jsampson
 */
class InfoService {

    def grailsApplication
    def infoContributor

    @PostConstruct
    def init(){
        infoContributor = grailsApplication.config?.g2actuate?.infoContributor
    }

    def collectInfo() {
        TreeMap info = [:]

        info << app()
        info << grails()
        info << scm()
        info << infoAnnotations()

        // Allow owning applications to add information.
        if (infoContributor instanceof Closure) {
            info = infoContributor(info)
        }

        info
    }

    private def app() {
        def app = [:]
        def description = Metadata.getCurrent().getProperty('app.description') ?: 'app.description not set'
        app['name'] = Metadata.getCurrent().getApplicationName()
        app['description'] = description
        app['version'] = Metadata.getCurrent().getApplicationVersion()
        [app: app]
    }

    // TODO consider a grails service?
    private def grails() {
        def grails = [:]
        def metadata = grails.util.Metadata.getCurrent()
        //grails['application-version'] = metadata.getApplicationVersion()
        grails['grails-version'] = metadata.getCurrent().getGrailsVersion()
        grails['groovy-version'] = GroovySystem.version
        grails['jvm-version'] = System.getProperty('java.version')
        grails['reloading-active'] = Environment.reloadingAgentEnabled
        grails['controllers'] = grailsApplication.controllerClasses.size()
        grails['domains'] = grailsApplication.domainClasses.size()
        grails['services'] = grailsApplication.serviceClasses.size()
        grails['tag-libraries'] = grailsApplication.tagLibClasses.size()

        // plugins
        TreeMap plugins = [:] // maintain insert order by key
        Holders.pluginManager.allPlugins.each { plugin ->
            plugins[plugin.name] = plugin.version
        }
        grails['plugins'] = plugins
        ['grails': grails]
    }

    /**
     * Returns a merged map of contributors, else an empty map.
     * @return the contributor's metric map
     */
    // TODO: consider moving, duplicates code in InfoService
    private def infoAnnotations() {
        // TODO: consider refactoring to ContribService
        final def contrib = [:]

        // TODO, consider finding/storing  methods at start up then iterating them at runtime.
        grailsApplication.getAllArtefacts().each { artefact ->

            def methods = artefact.methods.findAll {
                it.annotations.find {
                    it.annotationType() == InfoContrib.class
                }
            }

            methods.each { method ->
                InfoContrib annotation = method.annotations.find() {
                    it.annotationType() == InfoContrib.class
                } as InfoContrib
                grailsApplication.mainContext.getBeansOfType(artefact).each { final name, final instance ->

                    final def map = method.invoke(instance)

                    if (map in Map) {
                        contrib = merge(contrib, ["${annotation.key()}": map])
                    } else {
                        // todo alert user.
                    }
                }
            }

        }

        contrib
    }

    private def scm() {
        //TODO read from file scm provider places this in application.properties, but should have probably been added to config.properties or injected
        Properties properties = new Properties()
        File propertiesFile = new File('application.properties')
        propertiesFile.withInputStream {
            properties.load(it)
        }

        //def description = Metadata.getCurrent().getProperty('app.description') ?: 'app.description not set'
        def scm = new JsonSlurper().parseText(properties?.scm ?: "{\"message\":\"Nothing found: did you implement g2actuate.scmInfo in buildConfig.groovy?\"}")

        ['scm': scm]
    }

    //.. straight from https://stackoverflow.com/questions/27475111/merge-maps-with-recursive-nested-maps-in-groovy
    // TODO: consider moving, duplicates code in MetricService
    static Map merge(Map... maps) {
        Map result

        if (maps.length == 0) {
            result = [:]
        } else if (maps.length == 1) {
            result = maps[0]
        } else {
            result = [:]
            maps.each { map ->
                map.each { k, v ->
                    result[k] = result[k] instanceof Map ? merge(result[k], v) : v
                }
            }
        }

        result
    }

}
