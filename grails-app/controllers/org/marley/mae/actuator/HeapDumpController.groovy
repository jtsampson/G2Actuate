package org.marley.mae.actuator

import org.springframework.util.StreamUtils

import java.util.zip.GZIPOutputStream

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS

class HeapDumpController {

    boolean isEnabled = true  // you could disable in certain environments by setting false
    def heapService


    def dump() {

        if (!isEnabled) {
            render status: NOT_FOUND
        }

        boolean live = params.live ?: true // live objects only if true
        File file = heapService.dumpIt(live)
        streamFile(file);
    }

    /**
     * Handle the heap dump file and respond. By default this method will return the
     * response as a GZip stream.
     * @param heapDumpFile the generated dump file
     */
    private void streamFile(File heapDumpFile) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + (heapDumpFile.getName() + ".gz") + "\"");
        def is = new FileInputStream(heapDumpFile);

        try {
            GZIPOutputStream out = new GZIPOutputStream(response.getOutputStream());
            StreamUtils.copy(is, out);
            out.finish();
        }
        finally {
            is.close();
            heapDumpFile.delete()
        }

    }

    //--------------------------------------------
    // Exception methods:
    //--------------------------------------------

    def tooManyRequestsException(final HeapService.TooManyRequestsException exception) {
        render status: TOO_MANY_REQUESTS
    }

    def exception(final Exception ex) {
        logException ex
        render status: INTERNAL_SERVER_ERROR, model: [exception: ex]
    }

    /** Log exception */
    private void logException(final Exception ex) {
        log.error "Exception occurred. ${ex?.message}", ex
    }
}
