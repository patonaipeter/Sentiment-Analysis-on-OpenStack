package aic.appengine.sentimentanalysis.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.mapreduce.KeyValue;
import com.google.appengine.tools.mapreduce.MapReduceJob;
import com.google.appengine.tools.mapreduce.MapReduceResult;
import com.google.appengine.tools.mapreduce.MapReduceSettings;
import com.google.appengine.tools.mapreduce.MapReduceSpecification;
import com.google.appengine.tools.mapreduce.Marshallers;
import com.google.appengine.tools.mapreduce.impl.Util;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.outputs.InMemoryOutput;
import com.google.appengine.tools.pipeline.JobInfo;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;

import aic.appengine.sentimentanalysis.mapper.*;

@Service
public class DataStoreAccess {
	private static final Logger log = Logger.getLogger(DataStoreAccess.class
			.getName());
	//private static final boolean USE_BACKENDS = true;
	private static final boolean USE_BACKENDS = false;
	/**
	 * TODO:
	 * 
	 * 1. Initialize data store
	 * 2. write a method that takes a name parameter and returns a sentiment value
	 * @throws IOException 
	 *
	 */
	
	public static void deleteTweets() {
		int deleted_count = 0;
		boolean is_finished = false;
		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < 16384) {
			final DatastoreService dss = DatastoreServiceFactory
					.getDatastoreService();

			final Query query = new Query("tweets");

			query.setKeysOnly();

			final ArrayList<Key> keys = new ArrayList<Key>();

			for (final Entity entity : dss.prepare(query).asIterable(
					FetchOptions.Builder.withLimit(128))) {
				keys.add(entity.getKey());
			}

			keys.trimToSize();

			if (keys.size() == 0) {
				is_finished = true;
				break;
			}

			while (System.currentTimeMillis() - start < 16384) {

				try {

					dss.delete(keys);

					deleted_count += keys.size();

					break;

				} catch (Throwable ignore) {

					continue;

				}

			}

		}
	}
	
	public static void initDatastore(InputStream is) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(is),"UTF-8"));

        String line=null;
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        JSONParser p = new JSONParser();

        int count=0;
        while ((line = in.readLine()) != null){
        	p.reset();
        	try {
				JSONObject obj=(JSONObject)p.parse(line);
				//insert into datastore
				if (obj.get("name") != null && obj.get("text") != null
						&& obj.get("sentiment") != null
						&& obj.get("keywords") != null) {
					
					Object textobj = obj.get("text");
					Object sentimentobj = obj.get("sentiment");
					if (textobj instanceof String && sentimentobj instanceof Double) {
						String text = (String) textobj;
						Double sentiment = (Double) sentimentobj;
						if (text.length() > 0) {
							Entity tweet = new Entity("tweets");
							//tweet.setUnindexedProperty("name", obj.get("name"));
							tweet.setUnindexedProperty("text", text);
							tweet.setUnindexedProperty("sentiment",sentiment);
							//@SuppressWarnings("unchecked")
							//List<String> arr = (List<String>)obj.get("keywords");
							//tweet.setProperty("keywords",arr);
							datastore.put(tweet);
							count++;
				        	if(count % 100 == 0){
				        		log.info("Inserted: " + count);
				        	}
						}
					}
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        in.close();
	}
	
	private static MapReduceSettings getSettings() {
		MapReduceSettings settings = new MapReduceSettings()
				.setWorkerQueueName("mapreduce-workers")
				.setControllerQueueName("default");
		if (USE_BACKENDS) {
			settings.setBackend("worker");
		}
		return settings;
	}
	
	
	private static MapReduceJob<Entity, String, Double, KeyValue<String, Double>, List<List<KeyValue<String, Double>>>>
	getMapReduceJob(){
	    return new MapReduceJob<Entity, String, Double, KeyValue<String, Double>, List<List<KeyValue<String, Double>>>>();
	}
	
	private static MapReduceSpecification<Entity, String, Double, KeyValue<String, Double>, List<List<KeyValue<String, Double>>>>
	getMapReduceSpec(String search, int mapShardCount, int reduceShardCount){
		return MapReduceSpecification.of(
	            "MapReduceTest stats",
	            new DatastoreInput("tweets", mapShardCount),
	            new SentimentMapper(search),
	            Marshallers.getStringMarshaller(),
	            new DoubleMarshaller(),
	            new SentimentReducer(),
	            new InMemoryOutput<KeyValue<String, Double>>(reduceShardCount));
	}

	
	/*
	 * GAE Map/Reduce is experimental you have to compile the jar files first
	 * https://code.google.com/p/appengine-mapreduce/wiki/GettingStartedInJava
	 * 
	 * Check out the MapReduce code:
	 * svn checkout http://appengine-mapreduce.googlecode.com/svn/trunk/java
	 * Then, build the code using ant in the directory you just checked out:
	 *
	 * cd java
	 * ant
	 * 
	 * Then call the following in our project folder:
	 * 
	 * mvn install:install-file -Dfile=../../java/dist/lib/appengine-mapper.jar -Dpackaging=jar -DgroupId=com.google.appengine -DartifactId=appengine-mapper -Dversion=1.7.3
	 */
	public static double getSentiment(String name) {
	    PipelineService service = PipelineServiceFactory.newPipelineService();
	    MapReduceSettings settings = getSettings();

	    String pipelineId=service.startNewPipeline(getMapReduceJob(),
	    		getMapReduceSpec(name,15,5), settings, Util.jobSettings(settings));

		try{
			while(service.getJobInfo(pipelineId).getJobState()!=JobInfo.State.COMPLETED_SUCCESSFULLY){
				Thread.sleep(500);
			}
			JobInfo jobInfo = service.getJobInfo(pipelineId);
			@SuppressWarnings("unchecked")
			MapReduceResult<List<List<KeyValue<String, Double>>>> result=(MapReduceResult<List<List<KeyValue<String, Double>>>>)jobInfo.getOutput();
			
			
			double total = 0;
			int count = 0;
			for(List<KeyValue<String, Double>> shardlist : result.getOutputResult()){
				for(KeyValue<String, Double> kv : shardlist){
					total+=kv.getValue();
					count++;
				}
			}
			System.out.println(total);
			System.out.println(count);
			return total / count;
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	
	public static void startSentimentAnalysis(String name){
		PipelineService service = PipelineServiceFactory.newPipelineService();
		MapReduceSettings settings = getSettings();
		
	    service.startNewPipeline(new WrapperJob("testuser",name,15,10),settings,Util.jobSettings(settings));
	}
}
