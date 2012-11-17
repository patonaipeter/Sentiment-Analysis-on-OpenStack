package aic.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import tsa.classifier.ClassifierBuilder;
import tsa.classifier.WekaClassifier;
import tsa.util.Options;
import weka.classifiers.bayes.NaiveBayes;

import net.sf.json.JSONObject;
import aic.data.dto.Tweet;

public class JsonLikeReader implements ITweetReader {

	private BufferedReader in;
	private WekaClassifier wc;

	public JsonLikeReader(InputStream in) {
		this.in = new BufferedReader(new InputStreamReader(in));
		
		ClassifierBuilder cb = new ClassifierBuilder();
		Options opts = new Options();
		cb.setOpt(opts);
		opts.setSelectedFeaturesByFrequency(true);
		opts.setNumFeatures(20);
		opts.setRemoveEmoticons(true);

		try {
			cb.prepareTrain();
			cb.prepareTest();
		} catch (IOException e) {
			System.err.println("Error initializing analyzer.");
			throw new RuntimeException(e);
		}

		NaiveBayes nb = new NaiveBayes();
		wc = null;
		try {
			wc = cb.constructClassifier(nb);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
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
		double sentiment;
		try {
			sentiment = wc.classifyDouble(objIn.getString("text"));
		} catch (Exception e) {
			System.err.println("Input: " + objIn.getString("text"));
			e.printStackTrace();
			
			//ignore stupid exceptions, because there are bugs in the 
			//sentiment analysis framework 0.5 should not influence the result
			sentiment=0.5;
			//throw new IOException(e);
		}
		Tweet t = new Tweet(objIn.getJSONObject("user").getString("screen_name"), objIn.getString("text"), null,sentiment);
		return t;
	}

}
