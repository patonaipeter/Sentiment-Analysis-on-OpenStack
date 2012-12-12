package aic.appengine.sentimentanalysis.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	@RequestMapping(value="/", method=RequestMethod.GET)
	public ModelAndView showIndexPage(HttpServletRequest request, HttpServletResponse response) throws IOException{
		return new ModelAndView("index");
	}

	/**
	 * This is the method that receives the task name and query information from the form on the root page.
	 */
	@RequestMapping(value="/", method=RequestMethod.POST)
	public ModelAndView showIndexPage(@RequestParam("taskname") String taskName, @RequestParam("query") String query, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("Taskname: " + taskName);
		System.out.println("Query: " + query);
		return new ModelAndView("index");
	}

}
