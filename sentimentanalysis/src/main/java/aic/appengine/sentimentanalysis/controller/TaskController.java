package aic.appengine.sentimentanalysis.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TaskController {

	/**
	 * TaskService would be a service bean that exposes the task DAO methods (e.g loading tasks from the data store)
	 */
	//@Autowired
	//private ITaskService taskService;
	
	/**
	 * Here we should lookup all currently executing tasks and return a list of tasks to the tasks view.
	 */
	@RequestMapping(value="/tasks", method=RequestMethod.GET)
	public ModelAndView getTasks(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("tasks");
		//mav.addObject(taskService.getTasks());
		return mav;
	}
}
