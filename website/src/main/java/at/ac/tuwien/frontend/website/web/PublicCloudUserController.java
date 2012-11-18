package at.ac.tuwien.frontend.website.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/public/**")
@Controller
public class PublicCloudUserController {

    @RequestMapping(method = RequestMethod.POST, value = "{id}")
    public void post(@PathVariable Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }

    @RequestMapping
    public String index() {
        return "public/index";
    }
    
	@RequestMapping(method = RequestMethod.GET, value="/brokercall")
	public void updateSentiment(@RequestParam(value = "taskId", required = false) Integer taskId, @RequestParam(value = "sentiment", required = false) Double sentiment ){
		
		//TODO update task with id
		System.out.println("Call Successful with values: taskId: " + taskId + " Sentiment: " + sentiment);
		
	}
}
