package ee.legal.caselaw;

import java.util.Map;
import java.util.Properties;

public class CaselawProperties {
    private Properties prop;

    private static CaselawProperties instance = null;
    protected CaselawProperties() {
        processProperties();
    }
    public static CaselawProperties getInstance() {
        if(instance == null) {
            instance = new CaselawProperties();
        }
        return instance;
    }

    public void processProperties() {
        prop = new Properties();
        prop.setProperty("cwd", System.getProperty("user.dir"));

        Map<String, String> env = System.getenv();
        Integer redisDb = env.containsKey("ENVIRONMENT") && env.get("ENVIRONMENT").equals("jsearch_test") ? 0 : 1;
        prop.setProperty("redisDb", String.valueOf(redisDb));
    }

    public Properties getProp() {
        return prop;
    }
}
