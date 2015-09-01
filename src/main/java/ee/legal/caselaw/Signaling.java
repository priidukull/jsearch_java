package ee.legal.caselaw;

import ee.legal.caselaw.schema.Deque;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Signaling {
    private static Map<String, Map<String, Map<String, Object>>> signals = new HashMap<String, Map<String, Map<String, Object>>>();

    private static StringWriter sw = new StringWriter();
    private static Signaling instance = null;

    protected Signaling() {
        // Exists only to defeat instantiation.
    }
    public static Signaling getInstance() {
        if(instance == null) {
            instance = new Signaling();
        }
        return instance;
    }
    public void append(String target, HashMap<String, Object> signal) throws IOException {
        Map<String, Object> bareSignal = new TreeMap<String, Object>(signal);
        bareSignal.remove("tracking");
        ObjectMapper mapper = new ObjectMapper();
        String key = mapper.writeValueAsString(bareSignal);
        Map<String, Map<String, Object>> targetSignals = signals.get(target);
        if (targetSignals == null) {
            targetSignals = new HashMap<String, Map<String, Object>>();
        }
        targetSignals.put(key, signal);
        signals.put(target, targetSignals);
    }

    public void commit() throws IOException {
        for (String topic : signals.keySet()) {
            Deque deque = new Deque(topic);
            for (Map<String, Object> signal : signals.get(topic).values()) {
                deque.append(signal);
            }
        }
    }

    public void enqueue(HashMap<String, Object> task) throws IOException {
        if (task.get("tracking") == null) {
            Long tracking = System.currentTimeMillis();
            task.put("tracking", tracking);
        }
        String action = (String) task.get("action");
        append(action, task);
    }

    public static Map<String, Map<String, Map<String, Object>>> getSignals() {
        return signals;
    }
}
