package aic.service.test;

import java.util.regex.Pattern;

import aic.service.IService;
import aic.service.ServiceImpl;
import aic.service.analyzer.IAnalyzer;
import aic.service.analyzer.MongoAnalyzer;

public class SimpleTest {
	public static void main(String[] args) throws Exception {
		IAnalyzer analyzer = new MongoAnalyzer("localhost", "tweets");
		String[] patterns = { "apple", "ibm", "iphone", "android", "windows" };
		for (String p : patterns) {
			System.out.println("Rating for " + p + ": " + analyzer.analyze(p));
		}
		
		IService s=new ServiceImpl(analyzer);
		double res0=s.analyseSentiment("apple",3,0);
		double res1=s.analyseSentiment("apple",3,1);
		double res2=s.analyseSentiment("apple",3,2);
		double res=(res0+res1+res2)/3;
		System.out.println("Rating for apple: " + res);
		res=s.analyseSentiment("apple");
		System.out.println("Rating for apple: " + res);
	}
}
