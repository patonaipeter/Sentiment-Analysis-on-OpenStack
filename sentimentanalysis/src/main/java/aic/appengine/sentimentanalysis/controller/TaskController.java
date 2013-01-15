package aic.appengine.sentimentanalysis.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import aic.appengine.sentimentanalysis.domain.Task;

@Controller
public class TaskController {

    /**
     * TaskService would be a service bean that exposes the task DAO methods
     * (e.g loading tasks from the data store)
     */
    // @Autowired
    // private ITaskService taskService;

    /**
     * Here we should lookup all currently executing tasks and return a list of
     * tasks to the tasks view.
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public ModelAndView getTasks(HttpServletRequest request,
            HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("tasks");
        // mav.addObject(taskService.getTasks());
        return mav;
    }

    public ModelAndView create(@ModelAttribute("taskname") String taskname,
            @ModelAttribute("email") String email,
            @ModelAttribute("query") String query,
            @ModelAttribute("status") String status) {

        Key emailKey = KeyFactory.createKey("user", email);

        Entity task = new Entity("task", emailKey);
        task.setProperty("date", new Date());
        task.setProperty("name", taskname);
        task.setProperty("query", query);
        task.setProperty("status", status);

        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        datastore.put(task);

        ModelAndView mav = new ModelAndView("tasks");
        
        Query dataQuery = new Query("task").addSort("date",
                Query.SortDirection.DESCENDING);
        List<Entity> tasks = datastore.prepare(dataQuery).asList(
                FetchOptions.Builder.withLimit(10));

        mav.addObject("tasks", tasks);

        return mav;

    }
}
