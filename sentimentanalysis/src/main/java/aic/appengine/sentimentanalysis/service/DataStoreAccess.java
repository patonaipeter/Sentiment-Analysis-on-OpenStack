package aic.appengine.sentimentanalysis.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;


@Service
public class DataStoreAccess {
	
	/**
	 * TODO:
	 * 
	 * 1. Initialize data store
	 * 2. write a method that takes a name parameter and returns a sentiment value
	 * @throws IOException 
	 *
	 */
	public void initDatastore(InputStream is) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(is)));

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
        	System.out.print("Inserted: " + count + "\r");
        	count++;
        }
        in.close();
	}
	
	public double getSentiment(String name) {
		// Call Map/Reduce algorithm here and do some really cool stuff...	
		
		return 0.0;
	}
}
