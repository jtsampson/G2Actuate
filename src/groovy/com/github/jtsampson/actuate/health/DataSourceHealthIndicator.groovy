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

import groovy.sql.Sql
import org.apache.commons.logging.LogFactory

import javax.sql.DataSource

/**
 * @author jsampson
 * @since 6/27/2017.
 */
@groovy.transform.InheritConstructors // TODO remember why I did this?
class DataSourceHealthIndicator implements HealthIndicator {

    private static final log = LogFactory.getLog(this)

    def dataSource
    def validationQuery

    DataSourceHealthIndicator(DataSource ds) {
        this.dataSource = ds
    }

    DataSourceHealthIndicator(DataSource ds, String query) {
        this.dataSource = ds
        this.validationQuery = query
    }

    @Override
    Health health() {
        Sql sql = new Sql(dataSource)

        Health health = new Health([name: 'dataSource', status: Health.Status.UP])


        try {
            def connection = sql.getDataSource()?.connection
            def driver = connection?.metaData?.connection?.class?.name
            def productName = connection?.metaData?.databaseProductName
            def productVersion = connection?.metaData?.databaseProductVersion
            def product = DataSourceProducts.values().find { it.matches(productName) }
            def query = this.validationQuery ?: product.validationQuery


            health.details << [database: productName]
            health.details << [version: productVersion]
            health.details << [driver: driver]
            health.details << [validation: query]


            if (!product) {
                // TODO a few drivers return null for product name.
                log.warn("Unable to locate a suitable validation query, please manually set the validationQuery on the DataSourceHealthIndicator")
                health.status = Health.Status.UNKNOWN

            }else{
                health.details << [validationResult: (sql.firstRow(query))[0]]
                health.status = Health.Status.UP
            }

        } catch (all) {
            log.error("Unable to validate datasource connection due to ${all.getMessage()}")
            health.status = Health.Status.DOWN
            health.details << ['exception': all.getMessage()]
        } finally {
            sql.close()
        }

        health

    }
}
