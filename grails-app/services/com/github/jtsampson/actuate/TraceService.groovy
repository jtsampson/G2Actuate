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

import static java.util.Collections.synchronizedMap

class TraceService {

    def capacity
    def reverse
    def transactional = false

    static Map<Long, Object> traces = synchronizedMap(new LinkedHashMap<Long, Object>() {
        private static final long serialVersionUID = 12345L

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Object> eldest) {
            return size() > 10
        }
    })

    def collectTraces() {
        traces.collect { it.value }.reverse()
    }

    def static synchronized save(Long id, Map value) {
        traces.put(id, value)
        assert traces.size() > 0
    }


    def find(Long id) {
        traces.get(id)
    }

}
