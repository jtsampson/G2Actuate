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

coverage {

    exclusions = ['*'] //('*') The Single asterik excludes top-level groovy classes/scripts, such as BootStrap, changelog (Database Migration), ApplicationResources, etc
    appendCoverageResults = true
    sourceInclusions = ['src/groovy', "src/java", "scripts"]
}

grails.project.class.dir = "target/classes"

// To deploy to bintray.....
// grails maven-deploy  -Drepository=bintray-jtsampson-grails-plugins  --dryRun --noScm --verbose --allowOverwrite
grails.project.repos.default = "bintray-jtsampson-grails-plugins"
grails.project.repos."bintray-jtsampson-grails-plugins".url = "https://api.bintray.com/maven/jtsampson/grails-plugins/G2ActuatePlugin/;publish=1"

grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

def gebVersion = "0.13.1"
def webdriverVersion = "2.53.1"

grails.project.fork = [
        // configure settings for the test-app JVM, uses the daemon by default, test disables due to coverage needs
        test: false,
        run    : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
        war    : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        mavenRepo "https://mvnrepository.com"
    }
    dependencies {
        // Latest httpcore and httpmime for Coveralls plugin
        build 'org.apache.httpcomponents:httpcore:4.3.2'
        build 'org.apache.httpcomponents:httpclient:4.3.2'
        build 'org.apache.httpcomponents:httpmime:4.3.3'

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        compile 'org.apache.derby:derby:10.12.1.1'
        compile 'com.google.guava:guava:18.0'

        test "org.gebish:geb-junit3:${gebVersion}"
        test "org.gebish:geb-junit4:${gebVersion}"
        test "org.gebish:geb-spock:${gebVersion}"

        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"

        test "org.seleniumhq.selenium:selenium-support:${webdriverVersion}"
        test "org.seleniumhq.selenium:selenium-chrome-driver:${webdriverVersion}"
        test "org.seleniumhq.selenium:selenium-firefox-driver:${webdriverVersion}"
        test "org.seleniumhq.selenium:selenium-ie-driver:${webdriverVersion}"

        // For downloading browser-specific drivers that browsers like Chrome and IE require
        test "io.github.bonigarcia:webdrivermanager:1.3.1"
    }

    plugins {
        build(":tomcat:7.0.55") {
            export = false
        }
        build(":release:3.1.2", ":rest-client-builder:2.1.0")   { export = false }
        build(':coveralls:0.1.4', ':rest-client-builder:1.0.3') { export = false }

        test ":geb:${gebVersion}"
        test ":rest-client-builder:2.1.0"
        test (":code-coverage:2.0.3-3" ){ export = false }
    }

    // Remove the jar before the war is bundled
    //grails.war.resources = { stagingDir ->
    //    delete(file:"${stagingDir}/WEB-INF/lib/DisableOptimizationsTransformation-0.1-SNAPSHOT.jar")
    //}
}