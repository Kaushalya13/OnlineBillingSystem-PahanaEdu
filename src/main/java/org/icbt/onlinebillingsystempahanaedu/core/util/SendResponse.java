package org.icbt.onlinebillingsystempahanaedu.core.util;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 8/9/2025
 * time : 8:52 PM
 */
public class SendResponse {

    private static final Logger LOGGER = Logger.getLogger(SendResponse.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private SendResponse() {
    }

    public static void sendPlainText(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(message);
        LOGGER.log(Level.INFO, "Sent plain text response (status " + statusCode + "): " + message);
    }

    public static void sendJson(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (PrintWriter out = resp.getWriter()) {
            String json = toJsonString(data);
            out.print(json);
            out.flush();
            LOGGER.log(Level.INFO, "Sent JSON response (status " + statusCode + "): " + json);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send JSON response: " + e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/plain");
            resp.getWriter().write("Internal server error: Failed to serialize response data.");
        }
    }

    // --- JSON serialization helpers ---

    private static String toJsonString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj);
        }
        if (obj instanceof List) {
            return listToJson((List<?>) obj);
        }
        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Timestamp) {
            return "\"" + DATE_FORMAT.format((Timestamp) obj) + "\"";
        }
        if (obj instanceof Date) {
            return "\"" + DATE_FORMAT.format((Date) obj) + "\"";
        }
        // fallback
        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJson(String.valueOf(entry.getKey()))).append("\":");
            sb.append(toJsonString(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) sb.append(",");
            sb.append(toJsonString(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}

