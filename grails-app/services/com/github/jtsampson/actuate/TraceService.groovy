package com.github.jtsampson.actuate


import grails.transaction.Transactional

import static java.util.Collections.synchronizedMap




class TraceService {

    def capacity
    def reverse
    def transactional = false

     Map<Long, Object> traces = synchronizedMap(new LinkedHashMap<Long, Object>() {
        private static final long serialVersionUID = 12345L

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Object> eldest) {
            return size() > 10
        }
    })

    def collectTraces() {
        traces.collect{it.value}.reverse()
    }

    def synchronized save(Long id, Map value){
        traces.put(id, value)
        assert traces.size() > 0
    }


    def find(Long id){
        traces.get(id)
    }

}
