package aic.appengine.sentimentanalysis.mapper;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.KeyValue;
import com.google.appengine.tools.mapreduce.MapReduceJob;
import com.google.appengine.tools.mapreduce.MapReduceResult;
import com.google.appengine.tools.mapreduce.MapReduceSettings;
import com.google.appengine.tools.mapreduce.MapReduceSpecification;
import com.google.appengine.tools.mapreduce.Marshallers;
import com.google.appengine.tools.mapreduce.impl.Util;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.outputs.InMemoryOutput;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

public class WrapperJob extends Job1<Void,MapReduceSettings>{
	private static final long serialVersionUID = -3988602398941459031L;
	private String user;
	private String search;
	private int mapShardCount;
	private int reduceShardCount;
	
	public WrapperJob(String user, String search, int mapShardCount,
			int reduceShardCount) {
		super();
		this.user = user;
		this.search = search;
		this.mapShardCount = mapShardCount;
		this.reduceShardCount = reduceShardCount;
	}

	private MapReduceJob<Entity, String, Double, KeyValue<String, Double>, List<List<KeyValue<String, Double>>>>
	getMapReduceJob(){
	    return new MapReduceJob<Entity, String, Double, KeyValue<String, Double>, List<List<KeyValue<String, Double>>>>();
	}
	
	private MapReduceSpecification<Entity, String, Double, KeyValue<String, Double>, List<List<KeyValue<String, Double>>>>
	getMapReduceSpec(){
		return MapReduceSpecification.of(
	            "MapReduceTest stats",
	            new DatastoreInput("tweets", mapShardCount),
	            new SentimentMapper(search),
	            Marshallers.getStringMarshaller(),
	            new DoubleMarshaller(),
	            new SentimentReducer(),
	            new InMemoryOutput<KeyValue<String, Double>>(reduceShardCount));
	}
	
	private static class FinalCleanupJob extends Job2<Void, String,MapReduceResult<List<List<KeyValue<String, Double>>>>> {
		private static final long serialVersionUID = -7916067625541463185L;

		@Override
		public Value<Void> run(String user,
				MapReduceResult<List<List<KeyValue<String, Double>>>> result) {
			
			double total = 0;
			int count = 0;
			for(List<KeyValue<String, Double>> shardlist : result.getOutputResult()){
				for(KeyValue<String, Double> kv : shardlist){
					total+=kv.getValue();
					count++;
				}
			}
			
			if(count!=0){
				//insert into datastore
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Entity task = new Entity("tasks");
				task.setProperty("user", user);
				task.setProperty("sentiment",total / count);
				datastore.put(task);
			}
			
			return null;
		}
	}
	
	@Override
	public Value<Void> run(MapReduceSettings settings) {
		FutureValue<MapReduceResult<List<List<KeyValue<String, Double>>>>> result = futureCall(getMapReduceJob(), immediate(getMapReduceSpec()), immediate(settings),Util.jobSettings(settings));
		
		futureCall(new FinalCleanupJob(), immediate(user),result,Util.jobSettings(settings));
		return null;
	}

}
