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

import grails.transaction.Transactional
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * A service to allow programmatic shut down.
 */
@Transactional
class ShutdownService {

    def shutterDowner
    def scheduler;

    /**
     * A service to execute the configured shutterDowner after a 5 second delay.
     *
     * @author jsampson
     */
    def shutdown() {

        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduler = new ConcurrentTaskScheduler(localExecutor);

        Date now = new Date()
        Date lag = new Date(now.getTime() + 5000)

        scheduler.schedule(shutterDowner, lag)

        [shutdown: "attempting in ${(lag.getTime() - now.getTime()) / 1000} seconds"]
    }

}
