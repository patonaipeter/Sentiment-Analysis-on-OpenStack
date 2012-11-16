package aic.service.analyzer;

import java.util.regex.Pattern;

public interface IAnalyzer {

	public double analyze(Pattern pattern);
	
	public double analyze(Pattern pattern, int split,int index);
}
