package ee.legal.caselaw;

import ee.legal.caselaw.schema.Deque;
import org.slf4j.helpers.MessageFormatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static ee.legal.caselaw.Subscribe.getContext;

public class Logger {
    private static String queueName = "log.read_text_extract";
    private static Deque log = new Deque(queueName);
    private static Map<String, Object> logEntry = new HashMap<String, Object>();

    public static void info(String messagePattern, Object ... args) throws IOException {
        String message = parseMessage(messagePattern, args);
        logEntry.put("time", getTime());
        logEntry.put("message", message);
        logEntry.put("level", "info");
        log.append(logEntry);
    }

    public static void debug(String messagePattern, Object ... args) throws IOException {
        String message = parseMessage(messagePattern, args);
        logEntry.put("time", getTime());
        logEntry.put("message", message);
        logEntry.put("level", "debug");
        log.append(logEntry);
    }

    public static void warning(String messagePattern, Object ... args) throws IOException {
        String message = parseMessage(messagePattern, args);
        logEntry.put("time", getTime());
        logEntry.put("message", message);
        logEntry.put("level", "warning");
        log.append(logEntry);
    }

    public static void error(String messagePattern, Object ... args) throws IOException {
        String message = parseMessage(messagePattern, args);
        logEntry.put("time", getTime());
        logEntry.put("message", message);
        logEntry.put("level", "error");
        log.append(logEntry);
    }

    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static String parseMessage(String messagePattern, Object[] args) {
        Integer lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
        String fileName = Thread.currentThread().getStackTrace()[3].getFileName();
        String message = MessageFormatter.arrayFormat(messagePattern, args).getMessage();
        String context = getContext();
        context = context != null ? context : "";
        Object[] messageArgs = new Object[]{fileName, lineNumber, context, message};
        return MessageFormatter.arrayFormat("{}:{}: [{}] {}", messageArgs).getMessage();
    }

    private static long getTime() {
        return System.currentTimeMillis();
    }

}
