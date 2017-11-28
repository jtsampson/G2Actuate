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

package com.github.jtsampson.actuate.health

import grails.test.spock.IntegrationSpec

/**
 * @author jsampson
 * @since 6/27/2017.
 */
class DataSourceHealthIndicatorIntegrationSpec extends IntegrationSpec{

    def h2DataSourceHealthIndicator
    def derbyDataSourceHealthIndicator

    void "test H2 HealthIndicator"() {

        when:
        def health = h2DataSourceHealthIndicator.health()

        then:
        health.status == Health.Status.UP
        health.details['database'] == 'H2'
        health.details['version'] == '1.3.176 (2014-04-05)'
        health.details['driver'] == 'org.h2.jdbc.JdbcConnection'
        health.details['validationResult'] == 1
    }

    void "test Derby HealthIndicator"() {

        when:
        def health = derbyDataSourceHealthIndicator.health()

        then:
        health.status == Health.Status.UP
        health.details['database'] == 'Apache Derby'
        health.details['version'] == '10.12.1.1 - (1704137)'
        health.details['driver'] == 'org.apache.derby.impl.jdbc.EmbedConnection'
        health.details['validationResult'] == 1

    }

}
