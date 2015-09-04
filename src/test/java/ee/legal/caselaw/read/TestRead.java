package ee.legal.caselaw.read;

import ee.legal.caselaw.CaselawProperties;
import ee.legal.caselaw.Signaling;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestRead extends TestCase {

    private Map<String, Object> event;
    private ReadTextExtract read;
    private Signaling signaling;
    private Properties prop = CaselawProperties.getInstance().getProp();

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestRead(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TestRead.class );
    }

    private java.util.List emptyList;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @Before
    public void setUp() {
        read = new ReadTextExtract();
        signaling = Signaling.getInstance();
    }

    /**
     * Tears down the test fixture.
     * (Called after every test case method.)
     */
    @After
    public void tearDown() {
        event = null;
        read = null;
        signaling.clear();
    }

    @org.junit.Test
    public void testProcess() throws IOException {
        event = new HashMap<String, Object>() {{
            put("ref_id", 1);
            put("drink", true);
            put("file", prop.getProperty("cwd") + "/src/test/java/files/1.pdf");
        }};

        read.process(event);

        Collection<Map<String, Object>> tasks = signaling.getSignals().get("read.text.insert").values();
        Map<String, Object> task = tasks.iterator().next();
        String val = (String) task.get("val");
        Integer refId = (Integer) task.get("ref_id");
        String action = (String) task.get("action");
        Boolean drink = (Boolean) task.get("drink");
        String file = (String) task.get("file");

        assertTrue(val.contains("Viru Maakohus"));
        assertEquals(event.get("ref_id"), refId);
        assertEquals("read.text.insert", action);
        assertTrue(drink);
        assertEquals(event.get("file"), file);
    }

    @org.junit.Test
    public void testProcessWhenStringContainsNulls() throws IOException {
        event = new HashMap<String, Object>() {{
            put("ref_id", 2);
            put("drink", true);
            put("file", prop.getProperty("cwd") + "/src/test/java/files/2.pdf");
        }};

        read.process(event);

        Collection<Map<String, Object>> tasks = signaling.getSignals().get("read.text.insert").values();
        Map<String, Object> task = tasks.iterator().next();
        String val = (String) task.get("val");
        Integer refId = (Integer) task.get("ref_id");
        String action = (String) task.get("action");
        Boolean drink = (Boolean) task.get("drink");
        String file = (String) task.get("file");

        assertTrue(val.contains("Viru Maakohus"));
        assertEquals(event.get("ref_id"), refId);
        assertEquals("read.text.insert", action);
        assertTrue(drink);
        assertEquals(event.get("file"), file);
    }

    @org.junit.Test
    public void testProcessWhenPdfMarkedAsSecured() throws IOException {
        event = new HashMap<String, Object>() {{
            put("ref_id", 3);
            put("drink", true);
            put("file", prop.getProperty("cwd") + "/src/test/java/files/3.pdf");
        }};

        read.process(event);

        Collection<Map<String, Object>> tasks = signaling.getSignals().get("read.text.extract.failed").values();
        Map<String, Object> task = tasks.iterator().next();
        Integer refId = (Integer) task.get("ref_id");
        String action = (String) task.get("action");
        Boolean drink = (Boolean) task.get("drink");
        String file = (String) task.get("file");

        assertEquals(event.get("ref_id"), refId);
        assertEquals("read.text.extract.failed", action);
        assertTrue(drink);
        assertEquals(event.get("file"), file);
    }

    @org.junit.Test
    public void testProcessWhenUnknownCompressionMethod() throws IOException {
        event = new HashMap<String, Object>() {{
            put("ref_id", 4);
            put("drink", true);
            put("file", prop.getProperty("cwd") + "/src/test/java/files/4.pdf");
        }};

        read.process(event);

        Collection<Map<String, Object>> tasks = signaling.getSignals().get("read.text.extract.failed").values();
        Map<String, Object> task = tasks.iterator().next();
        Integer refId = (Integer) task.get("ref_id");
        String action = (String) task.get("action");
        Boolean drink = (Boolean) task.get("drink");
        String file = (String) task.get("file");

        assertEquals(event.get("ref_id"), refId);
        assertEquals("read.text.extract.failed", action);
        assertTrue(drink);
        assertEquals(event.get("file"), file);
    }
}
