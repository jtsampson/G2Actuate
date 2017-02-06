import groovy.json.JsonOutput
import groovy.json.JsonSlurper

// Based loosley on grails-build-info plugin

eventCreateWarStart = { warname, stagingDir ->

    event("AddScmPropertiesStart", [warname, stagingDir])
    def properties = getScmInfo()
    writeProperties(properties, "${stagingDir}/WEB-INF/classes/application.properties", "Written by g2actuator plugin")

    event("AddScmPropertiesStart", [warname, stagingDir])

}

private void writeProperties(Map properties, String propertyFile, String comment) {
    Ant.propertyfile(file: propertyFile) {
        properties.each { k, v ->
            entry(key: k, value: JsonOutput.toJson(v))
        }
    }
}

def getScmInfo() {

    def scmInfo = [:]
    println "*" * 50
    println "scmInfo()"
    // client provided closure to determine scmInfo
    def scmInfoClosure = buildConfig.g2actuator.scmInfo

    if (scmInfoClosure instanceof Closure) {
        scmInfo = scmInfoClosure()
    }

    // maybe a local git?
    if (!scmInfo) {
        scmInfo = getScmFromGitClient()
    }

    // maybe a local svn?
    if (!scmInfo) {
        scmInfo = getScmFromSvnClient()
    }

    return ['scm': scmInfo]
}

/**
 * Gets the SCM information from the GIT client if it exists.
 * @return Map of scm, or null on any error.
 * TODO: NOT TESTED - install SVN and test.
 */
private String getScmFromSvnClient() {
    def svnSCM = [:]
    try {
        svnSCM['name'] = 'SVN'

        def command = 'svn info --xml'
        def proc = command.execute()
        def out = new ByteArrayOutputStream()
        proc.consumeProcessOutput(out, null) //prevent blocking in Windows due to a full output buffer
        int exitVal = proc.waitFor()
        if (exitVal == 0) {
            def slurper = new XmlSlurper().parseText(out.toString())
            svnSCM['url'] slurper.entry.url
            svnSCM['root'] slurper.entry.reporitory.root
            def commit = [:]
            commit['revision'] slurper.entry.commit.@revision
            commit['author'] = slurper.entry.commit.author
            commit['date'] = slurper.entry.commit.date
            def commits = []
            commits.add(commit)
            svnSCM['commits'] = commits
        }
    } catch (ignore) {
        return null
    }

    svnSCM
}

/**
 * Gets the SCM information from the GIT client if it exists.
 * @return Map of scm, or null on any error.
 */
private Map getScmFromGitClient() {
    def gitSCM = [:]

    // TODO may have to try catch
    try {
        gitSCM['name'] = 'git'

        def command
        def proc

        // Get branch version
        command = """git symbolic-ref --short -q HEAD"""
        proc = command.execute()
        proc.waitFor()
        if (proc.exitValue() == 0) {
            gitSCM['branch'] = proc.in.text
        }

        // Get branch version - this is probably a Porcelain command may need to revisit
        command = """git config --get remote.origin.url"""
        proc = command.execute()
        proc.waitFor()
        if (proc.exitValue() == 0) {
            // get burl of repo - this is probably a Porcelain command
            gitSCM['url'] = proc.in.text
        }

        //TODO test on unix, on windows(at least) you can't nest double quotes in single quotes for this command, so we have to fix it up the resultant json
        // Get minimal commit history
        command = /git log --pretty=format:"{%n  'hash': '%H',%n  'author': '%aN <%aE>',%n  'date': '%ad',%n  'message': '%f'%n}," -2/
        proc = command.execute()
        proc.waitFor()
        if (proc.exitValue() == 0) {
            def badJson = proc.in.text
            def goodJson = badJson.replaceAll("'", "\"") // Hack: find better way
            def slurper = new JsonSlurper()
            def betterJson = /{"commits":[${goodJson.substring(0, goodJson.length() - 1)}]}/
            println betterJson.toString()
            def commits = slurper.parseText( betterJson.toString())
            gitSCM << commits
        }
    } catch (ignore) {
        //TODO log error
        println "*" * 50
        println "Issue " + ignore
        return null
    }

    gitSCM
}