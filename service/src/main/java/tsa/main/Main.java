package tsa.main;

import java.util.LinkedList;

import tsa.classifier.ClassifierBuilder;
import tsa.classifier.Invoker;
import tsa.commands.CalculateWmPrecisionCommand;
import tsa.commands.ConstructCommand;
import tsa.commands.ConstructWmCommand;
import tsa.commands.PrepareTrainCommand;
import tsa.util.Options;


public class Main {

	public static void main(String[] args) throws Exception {

		Options opt = new Options();
		ClassifierBuilder clb = new ClassifierBuilder();
		PrepareTrainCommand ptc = new PrepareTrainCommand(clb);
		ConstructCommand cc = new ConstructCommand(clb);
		ConstructWmCommand cwmc = new ConstructWmCommand(clb);
		CalculateWmPrecisionCommand calcPrec = new CalculateWmPrecisionCommand(clb);
		clb.setOpt(opt);
		Invoker inv = new Invoker(ptc, cc, cwmc, calcPrec);

		if (args[0].equals("prepareTrain")) {
			if (args.length > 1 && (args[1].equals("-sf") || args[1].equals("-re")))
				opt.setSelectedFeaturesByFrequency(true);
			if (args.length > 2 && (args[2].equals("-re") || args[2].equals("-sf")))
				opt.setRemoveEmoticons(true);
			inv.prepareTrain();
		} else if (args[0].equals("prepareTest")) {

		} else if (args[0].equals("construct")) {
			opt.setClassifierName(args[1]);
			if (args.length > 2)
				opt.setNumFeatures(Integer.parseInt(args[2]));
			inv.construct();
		} else if (args[0].equals("weightedMajority")) {
			int i = 1;
			opt.setWmClassifiersName(new LinkedList<String>());
			while (i < args.length) {
				opt.getWmClassifiersName().add(args[i]);
				i++;
			}
			inv.constructWm();
		} else if (args[0].equals("evaluateWm")) {
			int i = 1;
			opt.setWmClassifiersName(new LinkedList<String>());
			while (i < args.length) {
				opt.getWmClassifiersName().add(args[i]);
				i++;
			}
			inv.calculateWmPrecision();
		}
	}
}
