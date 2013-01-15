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
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import aic.appengine.sentimentanalysis.service.DataStoreAccess;

@Controller
public class HomeController {

    /**
     * This is the service bean that exposes the sentiment lookup functionality.
     * In the POST method below, we will have to access it somehow like this:
     * double query = sentimentQuery.getSentiment(query);
     */
    // @Autowired
    // private ISentimentQuery sentiementQuery;

    /**
     * This serves the index page, that shows the basic information about our
     * sentiment analysis and displays a form to enter the task name and query.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView showIndexPage(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user != null) {
            return new ModelAndView("index");
        } else {
            return new ModelAndView("redirect:/login");
        }
    }

    /**
     * This is the method that receives the task name and query information from
     * the form on the index page, when the form has been submitted.
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView showIndexPage(
            @RequestParam("taskname") String taskName,
            @RequestParam("query") String query, HttpServletRequest request,
            HttpServletResponse response) {
        System.out.println("Taskname: " + taskName);
        System.out.println("Query: " + query);
        System.out.println("Result: " + DataStoreAccess.getSentiment(query));

        return new ModelAndView("index");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView doLogin(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response)
    {
        if(username == "" || password == "") {
            return new ModelAndView("login");
        }
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            return new ModelAndView("redirect:/");
        } else {
            return new ModelAndView("redirect:" + userService.createLoginURL("/"));
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String doLogin(HttpServletRequest request, HttpServletResponse response) {
        return "login";
    }

    /**
	 * 
	 */
    @RequestMapping(value = "/deletedata", method = RequestMethod.GET)
    public ModelAndView deleteData(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // TODO not tested
        // upload should be done in java with raw POST request
        System.out.println("Deleting Data");
        DataStoreAccess.deleteTweets();

        return new ModelAndView("index");
    }

    /**
     * This method is used by the DataUploader (Upload.java) to upload the
     * tweets to the data store
     */
    @RequestMapping(value = "/uploaddata", method = RequestMethod.POST)
    public ModelAndView uploadData(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // TODO not tested
        // upload should be done in java with raw POST request
        System.out.println("Uploading Data");
        InputStream is = request.getInputStream();

        DataStoreAccess.initDatastore(is);

        return new ModelAndView("index");
    }
}