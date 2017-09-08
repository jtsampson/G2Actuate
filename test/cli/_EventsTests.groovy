import grails.test.AbstractCliTestCase
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class _EventsTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCreateWar(){
        execute (['war', '-non-interactive'])
        assert waitForProcess() == 0
        verifyHeader()

        Properties props = new Properties()
        props.load(new ZipFile('target/g2actuate.war').getInputStream(new ZipEntry('WEB-INF/classes/application.properties')))

        assert props['app.grails.version']
        assert props['app.name']
        assert props['scm']

    }
}
