package uts.app;

/** Parser/penulis CSV sederhana (mendukung kutip ganda, satu baris per record). */
public final class CsvUtil {
    private CsvUtil() {}

    /** Memecah satu baris CSV menjadi kolom. */
    public static String[] parseLine(String line) {
        String[] out = new String[8];
        int n = 0;
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQuotes = false;
                } else cur.append(c);
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                if (n == out.length) out = grow(out);
                out[n++] = cur.toString();
                cur.setLength(0);
            } else cur.append(c);
        }
        if (inQuotes) throw new IllegalArgumentException("Tanda kutip tidak ditutup");
        if (n == out.length) out = grow(out);
        out[n++] = cur.toString();
        String[] res = new String[n];
        for (int i = 0; i < n; i++) res[i] = out[i];
        return res;
    }

    private static String[] grow(String[] a) {
        String[] b = new String[a.length * 2];
        for (int i = 0; i < a.length; i++) b[i] = a[i];
        return b;
    }

    public static String toLine(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(fields[i]));
        }
        return sb.toString();
    }

    public static String escape(String s) {
        if (s == null) return "";
        if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\n') >= 0)
            return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }
}
