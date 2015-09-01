package ee.legal.caselaw;

import ee.legal.caselaw.read.ReadTextExtract;
import ee.legal.caselaw.schema.Deque;

import java.io.IOException;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Subscribe {
    private static Signaling signaling = Signaling.getInstance();

    private static String queueName = "read.text.extract";
    private static Deque queue = new Deque(queueName);
    private static ReadTextExtract target = new ReadTextExtract();
    private static Map<String, Object> event = null;
    private static int retrySeconds = 300;
    private static String context;

    private static void start() throws IOException {
        Logger.info("Subscribed to {}", queueName);
        while (true) {
            event = queue.popLeft();
            context = String.valueOf(event.get("ref_id"));
            process();
            signaling.commit();
        }
    }

    public static void process() throws IOException {
        Long t = System.currentTimeMillis();
        Boolean success = true;
        try {
            target.process(event);
        } catch (Exception e) {
            success = false;
            Logger.error(
                    "Processing {} at {} failed, retrying in {} seconds: {}",
                    event, queueName, retrySeconds, Logger.getStackTrace(e)
            );
            queue.appendLeft(event);
            try {
                sleep(retrySeconds * 1000);
            } catch (InterruptedException e1) {
                Logger.error("Sleep interrupted: {}", Logger.getStackTrace(e));
            }
            retrySeconds *= 2;
        }
        finally {
            if (success) {
                Float procTime = Float.valueOf(System.currentTimeMillis() - t) / 1000;
                Logger.info("Processed {} at {} in {} seconds",
                        event, queueName, procTime);
                retrySeconds = 300;
            }
            context = null;
        }
    }

    public static String getContext() {
        return context;
    }

    public static void main( String[] args ) throws IOException {
        start();
    }
}
