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

import org.springframework.util.StreamUtils

import java.util.zip.GZIPOutputStream

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS

class HeapDumpController {
    static responseFormats = ['octet-stream', 'xml']
    // TODO work this into config on per endpoint basis.
    boolean isEnabled = true  // you could disable in certain environments by setting false
    def heapDumpService


    def heapdump() {

        if (!isEnabled) {
            render status: NOT_FOUND
        }

        boolean live = params.live ?: true // live objects only if true
        File file = heapDumpService.dumpIt(live)
        streamFile(file)
    }

    /**
     * Handle the heap dump file and respond. By default this method will return the
     * response as a GZip stream.
     * @param heapDumpFile the generated dump file
     */
    private void streamFile(File heapDumpFile) {
        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + (heapDumpFile.getName() + ".gz") + "\"")
        def is = new FileInputStream(heapDumpFile)

        try {
            GZIPOutputStream out = new GZIPOutputStream(response.getOutputStream())
            StreamUtils.copy(is, out)
            out.finish()
        }
        finally {
            is.close()
            heapDumpFile.delete()
        }

    }

    //--------------------------------------------
    // Exception methods:
    //--------------------------------------------

    def tooManyRequestsException(final HeapDumpService.TooManyRequestsException exception) {
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
