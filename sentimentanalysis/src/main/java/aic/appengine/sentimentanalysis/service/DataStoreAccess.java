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
import com.google.appengine.tools.mapreduce.MapReduceSettings;
import com.google.appengine.tools.mapreduce.MapReduceSpecification;
import com.google.appengine.tools.mapreduce.Marshallers;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.outputs.InMemoryOutput;

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
					
					Entity tweet = new Entity("tweets");
					tweet.setUnindexedProperty("name", obj.get("name"));
					tweet.setUnindexedProperty("text", obj.get("text"));
					tweet.setUnindexedProperty("sentiment", obj.get("sentiment"));
					@SuppressWarnings("unchecked")
					List<String> arr = (List<String>)obj.get("keywords");
					tweet.setProperty("keywords",arr);
			        datastore.put(tweet);
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
        	if(count % 100 == 0){
        		log.info("Inserted: " + count);
        	}
        	count++;
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
		// Call Map/Reduce algorithm here and do some really cool stuff...	
		//TODO specify with parameter
		int mapShardCount=15;
		int reduceShardCount=10;
		InMemoryOutput<KeyValue<String, Double>> output=new InMemoryOutput<KeyValue<String, Double>>(reduceShardCount);
		
		MapReduceJob.start(
		        MapReduceSpecification.of(
		            "MapReduceTest stats",
		            new DatastoreInput("tweets", mapShardCount),
		            new SentimentMapper(name),
		            Marshallers.getStringMarshaller(),
		            new DoubleMarshaller(),
		            new SentimentReducer(),
		            output),
		        getSettings());
		//TODO: does not work yet (no results...)
		List<List<KeyValue<String, Double>>> result=output.finish(output.createWriters());
		
		double total = 0;
		int count = 0;
		for(List<KeyValue<String, Double>> shardlist : result){
			for(KeyValue<String, Double> kv : shardlist){
				total+=kv.getValue();
				count++;
			}
		}
		System.out.println(total);
		System.out.println(count);
		return total / count;
	}
}
