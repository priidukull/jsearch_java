package ee.legal.caselaw.schema;


import ee.legal.caselaw.CaselawProperties;
import org.codehaus.jackson.map.ObjectMapper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Deque {
    private static ObjectMapper mapper = new ObjectMapper();
    private static Properties prop = CaselawProperties.getInstance().getProp();

    private String name = null;
    private Jedis jedis;

    public Deque(String name) {
        this.name = name;
        this.jedis = new Jedis("localhost");
        this.jedis.select(Integer.parseInt((String) prop.get("redisDb")));
    }

    public Map popLeft() throws IOException {
        while (true) {
            String v = jedis.lpop(this.name);
            if (v != null) {
                return mapper.readValue(v, Map.class);
            }
        }
    }

    public void appendLeft(Map<String, Object> value) throws IOException {
        jedis.lpush(this.name, mapper.writeValueAsString(value));
    }

    public void append(Map<String, Object> value) throws IOException {
        jedis.rpush(this.name, mapper.writeValueAsString(value));
    }
}
