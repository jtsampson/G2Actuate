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

/**
 * @author jsampson
 * @since 7/5/2017.
 */
enum DataSourceProducts {


    DB2('DB2', 'SELECT 1 FROM SYSIBM.SYSDUMMY1'){
        @Override
        boolean matches(String productName) {
            super.matches(productName) || productName.toLowerCase().startsWith("db2/")
        }
    },
    DB2_AS400('DB2', 'SELECT 1 FROM SYSIBM.SYSDUMMY1'){
        @Override
        boolean matches(String productName) {
            super.matches(productName) || productName.toLowerCase().contains("as/400")
        }
    },
    DERBY('Apache Derby', 'SELECT 1 FROM SYSIBM.SYSDUMMY1'),
    FIREBIRD('Firebird', 'SELECT 1 FROM RDB\$DATABASE'){
        @Override
        boolean matches(String productName) {
            super.matches(productName) || productName.toLowerCase().startsWith("firebird")
        }
    },
    H2('H2', 'select 1'),
    HSQL('HSQL Database Engine', 'SELECT COUNT(*) FROM INFORMATION_SCHEMA.SYSTEM_USERS'){
        @Override
        boolean matches(String productName) {
            super.matches(productName) || this.productName?.toLowerCase()?.startsWith('hsql')
        }

    }, // starts with equal ignore case
    INFORMIX('Informix Dynamic Server', 'select count(*) from systables'),
    MYSQL('MySQL', 'SELECT 1'),
    ORACLE('Oracle', "SELECT 'Hello' from DUAL"),
    POSTGRESQL('PostgreSQL', 'SELECT 1'),
    SQLITE('SQLite', ""),
    SQL_SERVER('Microsoft SQL Server', 'SELECT 1'){
        @Override
        boolean matches(String productName) {
            super.matches(productName) || "SQL SERVER".equalsIgnoreCase(productName)
        }
    },
    UNKNOWN(null, null)


    DataSourceProducts(productName, validationQuery) {
        this.productName = productName
        this.validationQuery = validationQuery
    }

    // 'null' , GAE

    final String productName
    final String validationQuery

    boolean matches(String productName) {
        this.productName?.equalsIgnoreCase(productName)
    }
}
