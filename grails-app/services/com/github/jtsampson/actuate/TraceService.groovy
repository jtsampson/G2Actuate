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

/**
 * A service providing operations on the trace store.
 *
 * @author jsampson
 */
class TraceService {

    int  capacity
    boolean reverse = false
    def transactional = false

    static Map<Long, Object> traces = synchronizedMap(new LinkedHashMap<Long, Object>() {
        private static final long serialVersionUID = 12345L

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Object> eldest) {
            return size() > 100
        }
    })

    /**
     * Returns trace history from newest to oldest, or the reverse if  the 'reverse' property is true
     */
    def collectTraces() {
        traces.collect { it.value }.reverse()
    }

    /**
     * Saves a trace by id.
     * @return
     */
    def static synchronized save(Long id, Map value) {
        traces.put(id, value)
    }

    /**
     * Find a trace by id.
     * @return the trace or null if it does not exist.
     */
    def find(Long id) {
        traces.get(id)
    }

    /**
     * Clears the trace history.
     */
    def clear() {
        traces.clear()
    }

}
