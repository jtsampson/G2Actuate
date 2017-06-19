package com.github.jtsampson.actuate

import grails.transaction.Transactional
import org.springframework.util.ClassUtils
import org.springframework.util.ReflectionUtils

import javax.servlet.ServletException
import java.lang.management.ManagementFactory
import java.lang.management.PlatformManagedObject
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * A Service to expose heapdumps.
 *
 * Based on Spring Boot's HeapdumpMvcEndpoint by Lari Hotari and Phillip Webb.
 *
 * @author jsampson
 */
@Transactional
class HeapDumpService {
    private final HeapDumper heapDumper = createHeapDumper();
    private final Lock lock = new ReentrantLock();
    private final long timeout = 2000 // milliseconds to try to acquire the lock, else fail.

    File dumpIt(boolean live)
            throws IOException, ServletException, InterruptedException {
        File file
        try {
            if (this.lock.tryLock(this.timeout, TimeUnit.MILLISECONDS)) {
                try {
                    file = createTempFile(live);
                    this.heapDumper.dumpHeap(file, live);
                }
                finally {
                    this.lock.unlock();
                }
            } else {
                throw new TooManyRequestsException()
            }
        }
        catch (InterruptedException ex) {
            //TODO log?
            Thread.currentThread().interrupt();
        }

        file
    }

    private File createTempFile(boolean live) throws IOException {
        String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
        File file = File.createTempFile("heapdump" + date + (live ? "-live" : ""),
                ".hprof");
        file.delete();
        return file;
    }

    /**
     * Factory method used to create the {@link HeapDumper}.
     * @return the heap dumper to use
     * @throws HeapDumperUnavailableException if the heap dumper cannot be created
     */
    protected HeapDumper createHeapDumper() throws HeapDumperUnavailableException {
        return new HotSpotDiagnosticMXBeanHeapDumper();
    }

    protected interface HeapDumper {

        /**
         * Dump the current heap to the specified file.
         * @param file the file to dump the heap to
         * @param live if only <em>live</em> objects (i.e. objects that are reachable from
         * others) should be dumped
         * @throws IOException on IO error
         * @throws InterruptedException on thread interruption
         */
        void dumpHeap(File file, boolean live) throws IOException, InterruptedException;

    }

/**
 * Factory method used to create the {@link HeapDumper}.
 * @return the heap dumper to use
 * @throws HeapDumperUnavailableException if the heap dumper cannot be created
 */
    protected static class HotSpotDiagnosticMXBeanHeapDumper implements HeapDumper {

        private Object diagnosticMXBean;

        private Method dumpHeapMethod;

        @SuppressWarnings("unchecked")
        protected HotSpotDiagnosticMXBeanHeapDumper() {
            try {
                Class<?> diagnosticMXBeanClass = ClassUtils.resolveClassName(
                        "com.sun.management.HotSpotDiagnosticMXBean", null);
                this.diagnosticMXBean = ManagementFactory.getPlatformMXBean(
                        (Class<PlatformManagedObject>) diagnosticMXBeanClass);
                this.dumpHeapMethod = ReflectionUtils.findMethod(diagnosticMXBeanClass,
                        "dumpHeap", String.class, Boolean.TYPE);
            }
            catch (Throwable ex) {
                throw new HeapDumperUnavailableException(
                        "Unable to locate HotSpotDiagnosticMXBean", ex);
            }
        }


        @Override
        public void dumpHeap(File file, boolean live) {
            ReflectionUtils.invokeMethod(this.dumpHeapMethod, this.diagnosticMXBean,
                    file.getAbsolutePath(), live);
        }
    }

    /**
     * Exception to be thrown if the {@link HeapDumper} cannot be created.
     */
    protected static class HeapDumperUnavailableException extends RuntimeException {

        public HeapDumperUnavailableException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    /**
     * Exception to be thrown if the {@link HeapDumper} cannot be created.
     */
    protected static class TooManyRequestsException extends Exception {
    }


}
