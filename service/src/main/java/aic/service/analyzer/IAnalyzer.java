package aic.service.analyzer;

import java.util.regex.Pattern;

public interface IAnalyzer {

	public double analyze(String company);
	
	public double analyze(String company, int split,int index);
}
