package com.github.jtsampson.actuate

import java.lang.management.ClassLoadingMXBean
import java.lang.management.GarbageCollectorMXBean
import java.lang.management.ManagementFactory
import java.lang.management.MemoryUsage
import java.lang.management.ThreadMXBean

/**
 * A service to expose metrics. Currently only
 *  - basic
 *  - management
 *  - heap
 *  - non-heap
 *  - thread
 *  - class loading
 *  - garbage collection
 *  TODO: user supplied metrics
 *
 *  @author jsampson
 */
class MetricService {
    static final long timestamp = System.currentTimeMillis()

    def collectMetrics() {
        TreeMap metrics = [:]
        metrics << basic()
        metrics << management()
        metrics << heapMetrics()
        metrics << nonHeapMetrics()
        metrics << threadMetrics()
        metrics << classLoadingMetrics()
        metrics << garbageCollectionMetrics()
        metrics
    }


    private def basic() {
        Runtime runtime = Runtime.getRuntime()
        def basic = [:]
        ignoring(Throwable) {
            basic["mem"] = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed()
        }
        basic["mem.free"] = runtime.freeMemory()
        basic["processors"] = runtime.availableProcessors()
        basic["instance.uptime"] = System.currentTimeMillis() - timestamp
        basic
    }


    private def management() {
        def management = [:]
        ignoring(NoClassDefFoundError) {
            management["uptime"] = ManagementFactory.getRuntimeMXBean().getUptime()
            management["systemload.average"] = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()
        }
        management
    }

    private def heapMetrics() {
        def heap = [:]
        ignoring(NoClassDefFoundError) {
            MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
            heap["heap.committed"] = memoryUsage.getCommitted()
            heap["heap.init"] = memoryUsage.getInit()
            heap["heap.used"] = memoryUsage.getUsed()
            heap["heap"] = memoryUsage.getMax()
        }
        heap
    }

    private def nonHeapMetrics() {
        def nonHeap = [:]
        ignoring(NoClassDefFoundError) {
            MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()
            nonHeap["nonheap.committed"] = memoryUsage.getCommitted()
            nonHeap["nonheap.init"] = memoryUsage.getInit()
            nonHeap["nonheap.used"] = memoryUsage.getUsed()
            nonHeap["nonheap"] = memoryUsage.getMax()
        }
        nonHeap
    }

    private def threadMetrics() {
        def thread = [:]
        ignoring(NoClassDefFoundError) {
            ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean()
            thread["threads.peak"] = threadMxBean.getPeakThreadCount()
            thread["threads.daemon"] = threadMxBean.getDaemonThreadCount()
            thread["threads.totalStarted"] = threadMxBean.getTotalStartedThreadCount()
            thread["threads"] = (long) threadMxBean.getThreadCount()
        }
        thread
    }

    private def classLoadingMetrics() {
        def classLoading = [:]
        ignoring(NoClassDefFoundError) {
            ClassLoadingMXBean classLoadingMxBean = ManagementFactory.getClassLoadingMXBean()
            classLoading["classes"] = classLoadingMxBean.getLoadedClassCount()
            classLoading["classes.loaded"] = classLoadingMxBean.getTotalLoadedClassCount()
            classLoading["classes.unloaded"] = classLoadingMxBean.getUnloadedClassCount()
        }
        classLoading
    }

    private def garbageCollectionMetrics() {
        def garbage = [:]
        ignoring(NoClassDefFoundError) {
            List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory
                    .getGarbageCollectorMXBeans()
            garbageCollectorMxBeans.each {
                String name = it.name.replaceAll(" ", "_").toLowerCase()
                garbage["gc." + name + ".count"] = it.getCollectionCount()
                garbage["gc." + name + ".time"] = it.getCollectionTime()
            }
        }
        garbage
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
}
