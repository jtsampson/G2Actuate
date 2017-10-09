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

import io.github.bonigarcia.wdm.ChromeDriverManager
import io.github.bonigarcia.wdm.InternetExplorerDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.ie.InternetExplorerDriver

// http://www.gebish.org/manual/current/configuration.html#waiting_for_base_navigator
baseNavigatorWaiting = true

atCheckWaiting = true

// Use Firefox as the default driver
// See: https://github.com/SeleniumHQ/selenium/wiki/FirefoxDriver
driver = { new FirefoxDriver() }

environments {

    // run as "grails -Dgeb.env=chrome test-app functional:"
    // See: https://sites.google.com/a/chromium.org/chromedriver/
    chrome {
        // Download and configure ChromeDriver using https://github.com/bonigarcia/webdrivermanager
        ChromeDriverManager.getInstance().setup()

        driver = { new ChromeDriver() }
    }

    // run as "grails -Dgeb.env=ie test-app functional:"
    // See: https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver
    ie {
        // Download and configure InternetExplorerDriver using https://github.com/bonigarcia/webdrivermanager
        InternetExplorerDriverManager.getInstance().setup()

        driver = { new InternetExplorerDriver() }
    }
}