package aic.appengine.sentimentanalysis.mapper;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;

public class WrapperJob extends Job1<Void,MapReduceSettings>{
	private static final long serialVersionUID = -3988602398941459031L;
	private Long taskId;
	private String query;
	private String email;
	private int mapShardCount;
	private int reduceShardCount;
	

	public WrapperJob(Long taskId, String query, String email,
			int mapShardCount, int reduceShardCount) {
		super();
		this.taskId = taskId;
		this.query = query;
		this.email = email;
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
	            new SentimentMapper(query),
	            Marshallers.getStringMarshaller(),
	            new DoubleMarshaller(),
	            new SentimentReducer(),
	            new InMemoryOutput<KeyValue<String, Double>>(reduceShardCount));
	}
	
	private static class FinalCleanupJob extends Job3<Void, Long,String,MapReduceResult<List<List<KeyValue<String, Double>>>>> {
		private static final long serialVersionUID = -7916067625541463185L;

		@Override
		public Value<Void> run(Long taskId,String email,
				MapReduceResult<List<List<KeyValue<String, Double>>>> result) {
			
			double total = 0;
			int count = 0;
			for(List<KeyValue<String, Double>> shardlist : result.getOutputResult()){
				for(KeyValue<String, Double> kv : shardlist){
					total+=kv.getValue();
					count++;
				}
			}
			
			//insert into datastore
			try {
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				
				Key parentKey = KeyFactory.createKey("user", email);
				Key taskKey = KeyFactory.createKey(parentKey,"task", taskId);

				Entity task = datastore.get(taskKey);
				if(count!=0){
					task.setProperty("status", "COMPLETED");
					task.setProperty("sentiment",total / count);
				}else{
					task.setProperty("status", "NOT FOUND");
				}
				
				Object obj=task.getProperty("date");
				if(obj instanceof Date){
					Date start=(Date)obj;
					Date end=new Date();
					long duration=end.getTime()-start.getTime();
					task.setProperty("duration", duration);
				}
				
				datastore.put(task);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	@Override
	public Value<Void> run(MapReduceSettings settings) {
		FutureValue<MapReduceResult<List<List<KeyValue<String, Double>>>>> result = futureCall(getMapReduceJob(), immediate(getMapReduceSpec()), immediate(settings),Util.jobSettings(settings));
		
		futureCall(new FinalCleanupJob(), immediate(taskId),immediate(email),result,Util.jobSettings(settings));
		return null;
	}

}
