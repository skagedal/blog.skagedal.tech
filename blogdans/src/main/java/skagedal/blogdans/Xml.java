package skagedal.blogdans;

public class Xml {
    private Xml() {
    }

    public static String escape(String content) {
        return content
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }
}
