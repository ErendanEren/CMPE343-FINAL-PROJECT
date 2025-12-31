package tools;

import java.nio.file.*;
import java.sql.*;
import java.util.Locale;

public class ImageSeeder {

    private static String baseNameLower(Path p) {
        String file = p.getFileName().toString();
        int dot = file.lastIndexOf('.');
        String base = (dot >= 0) ? file.substring(0, dot) : file;
        return base.toLowerCase(Locale.ROOT);
    }

    private static String mimeFromExt(Path p) {
        String file = p.getFileName().toString().toLowerCase(Locale.ROOT);
        if (file.endsWith(".png")) return "image/png";
        if (file.endsWith(".jpg") || file.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.out.println("Kullanim: java ImageSeeder <imageFolderPath>");
            return;
        }

        String folder = args[0];

        // String url  = "jdbc:mysql://localhost:3306/group09_greengrocer?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        // String user = "myuser";
        // String pass = "1234";

        String sql = """
            UPDATE product_info
            SET image_blob = ?, image_mime = ?
            WHERE LOWER(name) = ?
        """;

        try (Connection conn = Database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int updated = 0;
            int skipped = 0;

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folder))) {
                for (Path p : stream) {
                    String fn = p.getFileName().toString().toLowerCase(Locale.ROOT);
                    if (!(fn.endsWith(".jpg") || fn.endsWith(".jpeg") || fn.endsWith(".png"))) continue;

                    byte[] bytes = Files.readAllBytes(p);
                    String mime = mimeFromExt(p);
                    String nameKey = baseNameLower(p); // "broccoli"

                    ps.setBytes(1, bytes);
                    ps.setString(2, mime);
                    ps.setString(3, nameKey);

                    int count = ps.executeUpdate();
                    if (count == 1) updated++;
                    else skipped++; // DB'de name eşleşmedi
                }
            }

            System.out.println("Updated: " + updated);
            System.out.println("Skipped: " + skipped);
        }

        System.out.println("For Control: SELECT name, OCTET_LENGTH(image_blob) bytes FROM product_info;");
    }
}
