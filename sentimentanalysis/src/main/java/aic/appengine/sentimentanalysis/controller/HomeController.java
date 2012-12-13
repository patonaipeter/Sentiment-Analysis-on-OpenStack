package aic.appengine.sentimentanalysis.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

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
