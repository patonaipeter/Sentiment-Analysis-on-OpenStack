package aic.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import net.sf.json.JSONException;
import aic.data.dto.Tweet;

public class DataLoader {

	public static void usage() {
		System.err.println("Usage: java DataLoader <Tweets-File>");
	}

	public static void main(String[] args) {

		if (args.length < 1) {
			usage();
			System.exit(1);
		}

		try {

			ITweetReader reader = new JsonLikeReader(new FileInputStream(args[0]));
			Tweet t = reader.read();

			ITweetWriter writer=null;
			if (args.length == 1) {
				writer = new MongoWriter("localhost", "tweets");
			}else{
				//writer = new JSONWriter(new FileOutputStream(args[1]));
				//output gzip
				writer = new JSONWriter(new DeflaterOutputStream(
						new FileOutputStream(args[1]), new Deflater(
								Deflater.BEST_COMPRESSION, false)));
			}
			
			int cnt = 0;
			do {
				try {
					t = reader.read();
					if (t != null) {
						writer.write(t);
						cnt++;
					}
				} catch (JSONException e) {

				}
				System.out.print("Inserted Tweet Number: " + cnt+"\r");
			} while (t != null);
			//add indexes
			writer.close();
			System.out.println("Written " + cnt + " tweets.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
