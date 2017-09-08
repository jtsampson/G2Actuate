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

import spock.lang.Specification

/**
 * @author jsampson
 * @since 7/5/2017.
 */
class DataSourceProductsSpec  extends Specification{

    def testProductMatches(){

        expect:
        DataSourceProducts.DERBY.productName == 'Apache Derby'
        DataSourceProducts.DERBY.matches('apache derby')

        DataSourceProducts.DB2.productName == 'DB2'
        DataSourceProducts.DB2.matches('db2')

        DataSourceProducts.FIREBIRD.productName == 'Firebird'
        DataSourceProducts.FIREBIRD.matches('firebird')

        DataSourceProducts.H2.productName == 'H2'
        DataSourceProducts.H2.matches('h2')

        DataSourceProducts.HSQL.productName == 'HSQL Database Engine'
        DataSourceProducts.HSQL.matches('hsql')

        DataSourceProducts.INFORMIX.productName == 'Informix Dynamic Server'
        DataSourceProducts.INFORMIX.matches('informix dynamic server')

        DataSourceProducts.MYSQL.productName == 'MySQL'
        DataSourceProducts.MYSQL.matches('mysql')

        DataSourceProducts.ORACLE.productName == 'Oracle'
        DataSourceProducts.ORACLE.matches('oracle')

        DataSourceProducts.POSTGRESQL.productName == 'PostgreSQL'
        DataSourceProducts.POSTGRESQL.matches('postgresql')

        DataSourceProducts.SQLITE.productName == 'SQLite'
        DataSourceProducts.SQLITE.matches('sqlite')

        DataSourceProducts.SQL_SERVER.productName == 'Microsoft SQL Server'
        DataSourceProducts.SQL_SERVER.matches('microsoft sql server')
    }

}
