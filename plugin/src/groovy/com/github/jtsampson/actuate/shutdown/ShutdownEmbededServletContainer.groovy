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

package com.github.jtsampson.actuate.shutdown

import grails.util.Holders

/**
 * Shuts down a grails app that is running in an embedded servlet container.
 * Usually
 * @author jsampson
 * @since 7/25/2017.
 */
class ShutdownEmbededServletContainer implements Runnable {


    @Override
    void run() {

        // See RunApp.groovy
        def serverHost = Holders.config.grails.server?.host ?: System.getProperty('server.host') ?: 'localhost'
        def serverPort = Holders.config.grails.server?.port?.http ?: System.getProperty('server.port') ?: 8080
        def url = "http://${serverHost ?: 'localhost'}:${serverPort + 1}"

        println "URL to stop server is $url"

        new File(".kill-run-app").createNewFile() // let the grails

        new URL(url).getText(connectTimeout: 10000, readTimeout: 10000)

    }

}
