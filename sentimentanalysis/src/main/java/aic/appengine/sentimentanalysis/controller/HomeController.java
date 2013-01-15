package aic.appengine.sentimentanalysis.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import aic.appengine.sentimentanalysis.service.DataStoreAccess;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

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
        //if (user != null) {
        return new ModelAndView("index");
        //} else {
          //  return new ModelAndView("redirect:/login");
        //}
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