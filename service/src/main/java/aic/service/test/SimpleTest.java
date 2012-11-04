package aic.service.test;

import java.util.regex.Pattern;

import aic.service.analyzer.IAnalyzer;
import aic.service.analyzer.MongoAnalyzer;

public class SimpleTest {
	public static void main(String[] args) throws Exception {
		IAnalyzer analyzer = new MongoAnalyzer("localhost", "tweets");
		String[] patterns = { ".*apple.*", ".*ibm.*", ".*iphone.*", ".*android.*", ".*windows.*" };
		for (String p : patterns) {
			System.out.println("Rating for " + p + ": " + analyzer.analyze(Pattern.compile(p, Pattern.CASE_INSENSITIVE)));
		}
	}
}
