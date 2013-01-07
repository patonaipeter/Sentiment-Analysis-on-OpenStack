package aic.appengine.sentimentanalysis.mapper;

import com.google.appengine.tools.mapreduce.KeyValue;
import com.google.appengine.tools.mapreduce.Reducer;
import com.google.appengine.tools.mapreduce.ReducerInput;

import java.util.logging.Logger;


class SentimentReducer extends Reducer<String, Double, KeyValue<String, Double>> {
	private static final long serialVersionUID = 7946646886273610576L;
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SentimentReducer.class
			.getName());

	public SentimentReducer() {
	}

	private void emit(String key, Double outValue) {
		// log.info("emit(" + outValue + ")");
		getContext().emit(KeyValue.of(key, outValue));
	}

	@Override
	public void beginShard() {
	}

	@Override
	public void beginSlice() {
	}

	@Override
	public void reduce(String key, ReducerInput<Double> values) {
		// log.info("reduce(" + key + ", " + values + ")");
		double total = 0;
		int count = 0;
		while (values.hasNext()) {
			double value = values.next();
			total += value;
			count++;
		}
		emit(key, total / count);
	}

	@Override
	public void endShard() {
	}

	@Override
	public void endSlice() {
	}

}
