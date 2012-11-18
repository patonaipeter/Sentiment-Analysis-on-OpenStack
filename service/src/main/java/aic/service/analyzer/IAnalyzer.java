package aic.service.analyzer;

public interface IAnalyzer {

	public double analyze(String company);
	
	public double analyze(String company, int split,int index);
}
