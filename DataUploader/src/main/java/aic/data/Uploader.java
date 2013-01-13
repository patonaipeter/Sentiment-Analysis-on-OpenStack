package aic.data;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Uploader {

    public static void main(String[] argv) throws FileNotFoundException,
            IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(new FileInputStream("tweets.json.gz")),
                "UTF-8"));

        String line = null;
        int sum = 0;
        do {
            ByteArrayOutputStream bytearr = new ByteArrayOutputStream();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    new GZIPOutputStream(bytearr), "UTF-8"));

            int count = 0;
            while ((line = in.readLine()) != null && count < 500) {
                out.println(line);
                count++;
                sum++;
            }
            out.close();
            byte[] data = bytearr.toByteArray();

            // URL url = new URL("http://localhost:8082/uploaddata");
            URL url = new URL("http://sentimentanalyis.appspot.com/uploaddata");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/octet");
            http.setRequestProperty("Content-Length",
                    String.valueOf(data.length));
            http.getOutputStream().write(data);

            // the request is only sent if we read in the data
            BufferedReader in2 = new BufferedReader(new InputStreamReader(
                    http.getInputStream()));
            String line2 = null;
            while ((line2 = in2.readLine()) != null) {
                // System.out.println(line2);
            }
            in2.close();

            System.out.println("Loaded " + sum);
        } while (line != null);

        in.close();
    }

}
