package org.white5moke;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

public class TheBlob {
    public TheBlob() {
        String someData = "value is soul";
        try {
            Blob blob = new SerialBlob(someData.getBytes(StandardCharsets.UTF_8));
            System.out.println(Base64.getEncoder().encodeToString(blob.getBinaryStream().readAllBytes()));

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TheBlob();
    }
}
