package aic.data;

import java.io.FileInputStream;

import aic.data.dto.Tweet;

public class DataLoader {

	public static void usage() {
		System.err.println("Usage: java DataLoader <Tweets-File>");
	}

	public static void main(String[] args) {

		if (args.length != 1) {
			usage();
			System.exit(1);
		}

		try {

			ITweetReader reader = new JsonLikeReader(new FileInputStream(args[0]));
			Tweet t = reader.read();

			ITweetWriter writer = new MongoWriter("tweets");
			int cnt = 0;
			while (t != null) {
				cnt++;
				writer.write(t);
				t = reader.read();
			}
			System.out.println("Written " + cnt + " tweets.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
