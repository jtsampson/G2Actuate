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

class HealthAggregatorSpec extends Specification {

    def aggregator
    def up
    def down

    def setup() {
        aggregator = new HealthAggregator()
        up = new Health([name: 'up', status: Health.Status.UP, details: [deet1: 'deet1']])
        down = new Health([name: 'up', status: Health.Status.DOWN, details: [deet2: 'deet2']])
    }


    void "aggregate [up] as UP"() {
        expect:
        aggregator.aggregate([up]) == Health.Status.UP
    }

    void "aggregate [down] as DOWN"() {
        expect:
        aggregator.aggregate([down]) == Health.Status.DOWN
    }

    void "agregator aggregates [] (none) as UP"() {
        expect:
        aggregator.aggregate([]) == Health.Status.UP
    }

    void "agregator aggregates [up,up,down] as DOWN"() {
        expect:
        aggregator.aggregate([up, up, down]) == Health.Status.DOWN
    }
}
