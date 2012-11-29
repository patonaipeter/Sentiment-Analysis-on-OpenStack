package aic.website.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
	private Set<String> noiseWords=new HashSet<String>();
	
	@PostConstruct
	public void init() throws UnknownHostException{
		mongo = new Mongo(dbHost);
		db = mongo.getDB(dbName);
		tweetsCollection = db.getCollection("tweets");
		
		//copied from http://drupal.org/node/1202
		String noiseList="about,after,all,also,an,and,another,any,are,as,at,be,because,been,before" + 
						 "being,between,both,but,by,came,can,come,could,did,do,each,for,from,get" + 
						 "got,has,had,he,have,her,here,him,himself,his,how,if,in,into,is,it,like" + 
						 "make,many,me,might,more,most,much,must,my,never,now,of,on,only,or,other" + 
						 "our,out,over,said,same,see,should,since,some,still,such,take,than,that" + 
						 "the,their,them,then,there,these,they,this,those,through,to,too,under,up" + 
						 "very,was,way,we,well,were,what,where,which,while,who,with,would,you,your,a" + 
						 "b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,$,1,2,3,4,5,6,7,8,9,0,_";
		for(String word : noiseList.split(",")){
			noiseWords.add(word);
		}
	}
	
	private String[] getKeywords(String text){
		Set<String> out=new HashSet<String>();
		//split on word boundary
		String[] words=text.split("\\W+");
		for(String word : words){
			word=word.toLowerCase().trim();
			if(word.length()>2 && !"".equals(word) && !noiseWords.contains(word)){
				out.add(word);
			}
		}
		return out.toArray(new String[0]);
	}
	
	@Override
	public double analyseSentiment(String searchString) {
		BasicDBObject dbo = new BasicDBObject();
		
		String[] keywords=getKeywords(searchString);
		if(keywords.length>0){
			if(keywords.length==1){
				dbo.put("keywords", keywords[0]);
			}else{
				ArrayList<BasicDBObject> andList = new ArrayList<BasicDBObject>();
				for(String keyword : keywords){
					andList.add(new BasicDBObject("keywords", keyword));
				}
				dbo.put("$and", andList);
			}
			
	        //try out map/reduce
			String map = "function(){emit(null,{sentiment: this.sentiment});};";
			String reduce = "function(key,values){" + "var result = 0.0;"
					+ "values.forEach(function(value) {"
					+ "  result += value.sentiment;" + "});"
					+ "return { sentiment: result/values.length };" + "};";
			MapReduceOutput out = tweetsCollection.mapReduce(map, reduce, null,
					MapReduceCommand.OutputType.INLINE, dbo);
			if(out!=null){
				Iterable<DBObject> results=out.results();
				if(results!=null){
					Iterator<DBObject> it=results.iterator();
					if(it.hasNext()){
						return (Double) ((DBObject) it.next().get("value")).get("sentiment");
					}
				}
			}

		}
			
		return 0.0;
	}
	
}
