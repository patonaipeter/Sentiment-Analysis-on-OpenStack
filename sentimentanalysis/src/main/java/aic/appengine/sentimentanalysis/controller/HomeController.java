package aic.appengine.sentimentanalysis.controller;

import java.io.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import aic.appengine.sentimentanalysis.service.DataStoreAccess;

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
	
	@RequestMapping(value="/tasks/insertdata", method=RequestMethod.GET)
	public ModelAndView insertData(HttpServletRequest request, HttpServletResponse response) throws IOException{

        //this works but blows up the war file to 200mb
		ServletContext context = request.getSession().getServletContext();
        InputStream is = context.getResourceAsStream("/WEB-INF/tweets.json.gz");
        
        DataStoreAccess.initDatastore(is);
        
		return new ModelAndView("index");
	}
	
	/*
	 * use this url to init data!
	 * it starts a background task that can run for up
	 * to 10 minutes
	 * 
	 * if you start /insertdata directly it will be killed after 60 seconds
	 */
	@RequestMapping(value="/init", method=RequestMethod.GET)
	public ModelAndView initData(HttpServletRequest request, HttpServletResponse response) throws IOException{

        Queue queue = QueueFactory.getDefaultQueue();
        //starts background task with url /insertdata
        queue.add(withUrl("/tasks/insertdata").method(Method.GET));
        
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
		System.out.println("Result: " + DataStoreAccess.getSentiment(query));
		
		return new ModelAndView("index");
	}
	
	
	/*@RequestMapping(value="/uploaddata", method=RequestMethod.POST)
	public ModelAndView uploadData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//TODO not tested
		//upload should be done in java with raw POST request
        InputStream is = request.getInputStream();
        
        DataStoreAccess.initDatastore(is);
        
		return new ModelAndView("index");
	}
	
	@RequestMapping(value="/downloaddata", method=RequestMethod.GET)
	public ModelAndView downloadData(HttpServletRequest request, HttpServletResponse response) throws IOException{
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

        InputStream is=con.getInputStream();
        
        DataStoreAccess.initDatastore(is);
        
		return new ModelAndView("index");
	}*/

}
