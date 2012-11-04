package aic.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.json.JSONObject;
import aic.data.dto.Tweet;

public class JsonLikeReader implements ITweetReader {

	private BufferedReader in;

	public JsonLikeReader(InputStream in) {
		this.in = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public Tweet read() throws IOException {
		String line = in.readLine();
		if (line == null) {
			return null;
		}
		if (line.matches("\\d+")) {
			return read();
		}
		JSONObject objIn = JSONObject.fromObject(line);
		Tweet t = new Tweet(objIn.getJSONObject("user").getString("screen_name"), objIn.getString("text"), null);
		return t;
	}

}
