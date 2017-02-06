import grails.test.AbstractCliTestCase

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

        //Properties props = new Properties()
       // props.load(new ZipFile('target/bookstore-0.1.war').getInputStream(new ZipEntry('WEB-INF/classes/application.properties')))

        assert props['scm.version']
        assert props['build.date']

        assert props['who.was.here'] == 'hugo'

    }
}
