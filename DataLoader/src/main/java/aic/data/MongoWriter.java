package aic.data;

import java.net.UnknownHostException;

import aic.data.dto.Tweet;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class MongoWriter implements ITweetWriter {

	private Mongo mongo;

	private DB db;

	private DBCollection tweetsCollection;

	public MongoWriter(String db) {
		try {
			this.mongo = new Mongo("localhost");
			this.db = mongo.getDB(db);
			this.tweetsCollection = this.db.getCollection("tweets");
			this.tweetsCollection.remove(new BasicDBObject());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void write(Tweet tweet) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("name", tweet.getUsername());
		dbo.put("text", tweet.getText());
		this.tweetsCollection.insert(dbo);
	}
}
