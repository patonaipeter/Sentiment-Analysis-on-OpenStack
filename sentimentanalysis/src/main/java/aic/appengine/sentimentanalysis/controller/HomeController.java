package aic.appengine.sentimentanalysis.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@Controller
public class HomeController{

	/**
	 * This is the service bean that exposes the sentiment lookup functionality.
	 * In the POST method below, we will have to access it somehow like this:
	 * double query = sentimentQuery.getSentiment(query);  
	 */
	//@Autowired
	//private ISentimentQuery sentiementQuery;
	
	/**
	 * This serves the index page, that shows the basic information about our sentiment analysis and
	 * displays a form to enter the taskname and query.
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public ModelAndView showIndexPage(HttpServletRequest request, HttpServletResponse response) throws IOException{
		return new ModelAndView("index");
	}
	
	@RequestMapping(value="/uploaddata", method=RequestMethod.POST)
	public ModelAndView uploadData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//TODO not tested
		//upload should be done in java with raw POST request
        InputStream is = request.getInputStream();
        
        
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(is)));

        String line=null;
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        JSONParser p = new JSONParser();

        int count=0;
        while ((line = in.readLine()) != null){
        	
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
        
		return new ModelAndView("index");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/insertdata", method=RequestMethod.GET)
	public ModelAndView insertData(HttpServletRequest request, HttpServletResponse response) throws IOException{
        /*
		//TODO: Problem
        //does not work because GAE does not allow streaming. The call to
        //getInputStream downloads the entire file before returning
        //additionally there is a 1MB size limit.
		
		//180mb contains 2.6 million tweets
		//URL tweets = new URL("http://web.student.tuwien.ac.at/~e0502196/tweets.json.gz");
		//contains only 12000 tweets
		URL tweets = new URL("http://web.student.tuwien.ac.at/~e0502196/smalltweets.json.gz");
        URLConnection con = tweets.openConnection();
        //default timeout is 5 seconds
        con.setConnectTimeout(60000);
        con.setReadTimeout(60000);

        //InputStream is=con.getInputStream();*/
        
        //this works but blows up the war file to 200mb
        ServletContext context = request.getSession().getServletContext();
        InputStream is = context.getResourceAsStream("/WEB-INF/tweets.json.gz");
        
        
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(is)));

        String line=null;
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        JSONParser p = new JSONParser();

        int count=0;
        while ((line = in.readLine()) != null){
        	
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
		return new ModelAndView("index");
	}

	/**
	 * This is the method that receives the task name and query information from the form on the index page,
	 * when the form has been submitted.
	 */
	@RequestMapping(value="/", method=RequestMethod.POST)
	public ModelAndView showIndexPage(@RequestParam("taskname") String taskName, @RequestParam("query") String query, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("Taskname: " + taskName);
		System.out.println("Query: " + query);
		
		return new ModelAndView("index");
	}

}
