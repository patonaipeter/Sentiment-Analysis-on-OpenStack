package aic.website.service;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.Mongo;

@Service("sentimentService")
public class ServiceImpl implements IService{

	private String dbHost="localhost";
	private String dbName="tweets";
	private Mongo mongo;
	private DB db;
	private DBCollection tweetsCollection;
	
	@PostConstruct
	public void init() throws UnknownHostException{
		mongo = new Mongo(dbHost);
		db = mongo.getDB(dbName);
		tweetsCollection = db.getCollection("tweets");
	}
	
	@Override
	public double analyseSentiment(String company) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("keywords", company.toLowerCase());
		//dbo.put("text", Pattern.compile(".*"+company+".*"));
		//DBCursor cursor = tweetsCollection.find(dbo);
		
		
        //try out map/reduce
		String map = "function(){emit(null,{sentiment: this.sentiment});};";
		String reduce = "function(key,values){" + "var result = 0.0;"
				+ "values.forEach(function(value) {"
				+ "  result += value.sentiment;" + "});"
				+ "return { sentiment: result/values.length };" + "};";
		MapReduceOutput out = tweetsCollection.mapReduce(map, reduce, null,
				MapReduceCommand.OutputType.INLINE, dbo);
		return (Double) ((DBObject) out.results().iterator().next()
				.get("value")).get("sentiment");
	}
	
}
