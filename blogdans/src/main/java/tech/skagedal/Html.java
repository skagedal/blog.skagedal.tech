package tech.skagedal;

public class Html {
    private Html() {
    }

    public static String escapeHtml(String content) {
        return content
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}
