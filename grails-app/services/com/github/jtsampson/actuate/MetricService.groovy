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

import com.github.jtsampson.actuate.annotations.MetricContrib

import java.lang.management.ClassLoadingMXBean
import java.lang.management.GarbageCollectorMXBean
import java.lang.management.ManagementFactory
import java.lang.management.MemoryUsage
import java.lang.management.ThreadMXBean

/**
 * A service to expose metrics. Currently:
 *  - basic
 *  - management
 *  - heap
 *  - non-heap
 *  - thread
 *  - class loading
 *  - garbage collection
 *  - developer supplied metrics
 *
 * @author jsampson
 */
class MetricService {
    static final long startTime = System.currentTimeMillis() // application start time (approx)
    def grailsApplication

    def collectMetrics() {
        final TreeMap metrics = [:]
        metrics << basic()
        metrics << management()
        metrics << heapMetrics()
        metrics << nonHeapMetrics()
        metrics << threadMetrics()
        metrics << classLoadingMetrics()
        metrics << garbageCollectionMetrics()
        metrics << contributors()
        metrics
    }


    private def basic() {
        Runtime runtime = Runtime.runtime
        final def basic = [:]
        ignoring(Throwable) {
            basic["mem"] = ManagementFactory.memoryMXBean.nonHeapMemoryUsage.used
        }
        basic["mem.free"] = runtime.freeMemory()
        basic["processors"] = runtime.availableProcessors()
        basic["instance.uptime"] = System.currentTimeMillis() - startTime
        basic
    }


    private def management() {
        final def management = [:]
        ignoring(NoClassDefFoundError) {
            management["uptime"] = ManagementFactory.runtimeMXBean.uptime
            management["systemload.average"] = ManagementFactory.operatingSystemMXBean.systemLoadAverage
        }
        management
    }

    private def heapMetrics() {
        final def heap = [:]
        ignoring(NoClassDefFoundError) {
            MemoryUsage memoryUsage = ManagementFactory.memoryMXBean.heapMemoryUsage
            heap["heap.committed"] = memoryUsage.committed
            heap["heap.init"] = memoryUsage.init
            heap["heap.used"] = memoryUsage.used
            heap["heap"] = memoryUsage.max
        }
        heap
    }

    private def nonHeapMetrics() {
        final def nonHeap = [:]
        ignoring(NoClassDefFoundError) {
            final MemoryUsage memoryUsage = ManagementFactory.memoryMXBean.nonHeapMemoryUsage
            nonHeap["nonheap.committed"] = memoryUsage.committed
            nonHeap["nonheap.init"] = memoryUsage.init
            nonHeap["nonheap.used"] = memoryUsage.used
            nonHeap["nonheap"] = memoryUsage.max
        }
        nonHeap
    }

    private def threadMetrics() {
        final def thread = [:]
        ignoring(NoClassDefFoundError) {
            final ThreadMXBean threadMxBean = ManagementFactory.threadMXBean
            thread["threads.peak"] = threadMxBean.peakThreadCount
            thread["threads.daemon"] = threadMxBean.daemonThreadCount
            thread["threads.totalStarted"] = threadMxBean.totalStartedThreadCount
            thread["threads"] = (long) threadMxBean.threadCount
        }
        thread
    }

    private def classLoadingMetrics() {
        final def classLoading = [:]
        ignoring(NoClassDefFoundError) {
            final ClassLoadingMXBean classLoadingMxBean = ManagementFactory.classLoadingMXBean
            classLoading["classes"] = classLoadingMxBean.loadedClassCount
            classLoading["classes.loaded"] = classLoadingMxBean.totalLoadedClassCount
            classLoading["classes.unloaded"] = classLoadingMxBean.unloadedClassCount
        }
        classLoading
    }

    private def garbageCollectionMetrics() {
        final def garbage = [:]
        ignoring(NoClassDefFoundError) {
            List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory.garbageCollectorMXBeans
            garbageCollectorMxBeans.each {
                String name = it.name.replaceAll(" ", "_").toLowerCase()
                garbage["gc." + name + ".count"] = it.collectionCount
                garbage["gc." + name + ".time"] = it.collectionTime
            }
        }
        garbage
    }

    /**
     * Returns a merged map of contributors, else an empty map.
     * @return the contributor's metric map
     */
    // TODO: consider moving, duplicates code in InfoService
    private def contributors() {
        // TODO: consider refactoring to ContribService
        final def contrib = [:]

        // todo, consider finding/storing  methods at start up then iterating them at runtime.
        grailsApplication.getAllArtefacts().each { artefact ->

            def methods = artefact.methods.findAll {
                it.annotations.find {
                    it.annotationType() == MetricContrib.class
                }
            }

            methods.each { method ->
                MetricContrib annotation = method.annotations.find() {
                    it.annotationType() == MetricContrib.class
                } as MetricContrib
                grailsApplication.mainContext.getBeansOfType(artefact).each { final name, final instance ->

                    final def map = method.invoke(instance)

                    if (map in Map) {
                        contrib = merge(contrib, ["${annotation.key()}": map])
                    } else {
                        // todo alert user.
                    }
                }
            }

        }

        contrib
    }

    @MetricContrib(key = 'book.controller')
    def bob() {
        println this.dump()
       // return [dump: this.dump(), name: 'bob']
        return [name: 'bob']

    }

    @MetricContrib(key = 'book.controller')
    def jane() {
        return [type: 'jane']
    }

    // here's the closure that can ignore things
    def ignoring = { Class<? extends Throwable> catchMe, Closure callMe ->
        try {
            callMe.call()
        } catch (e) {
            if (!e.class.isAssignableFrom(catchMe)) {
                throw e
            }
        }
    }

    //.. straight from https://stackoverflow.com/questions/27475111/merge-maps-with-recursive-nested-maps-in-groovy
    // TODO: consider moving, duplicates code in InfoService
    static Map merge(Map... maps) {
        Map result

        if (maps.length == 0) {
            result = [:]
        } else if (maps.length == 1) {
            result = maps[0]
        } else {
            result = [:]
            maps.each { map ->
                map.each { k, v ->
                    result[k] = result[k] instanceof Map ? merge(result[k], v) : v
                }
            }
        }

        result
    }
}
