package aic.website.web;

import aic.website.domain.AuthUser;
import aic.website.domain.Task;
import aic.website.service.IService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/tasks")
@Controller
@RooWebScaffold(path = "tasks", formBackingObject = Task.class)
public class TaskController {
	
	@Autowired
	private IService sentimentService;

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Task task, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		
		if(task.getName()==null || task.getSearchTerm()==null || task.getSearchTerm().length()<3){
            populateEditForm(uiModel, task);
            return "tasks/create";
		}
		
		task.setOwner(getCurrentUser());
		task.setAnalysisResult(sentimentService.analyseSentiment(task.getSearchTerm()));
		
        uiModel.asMap().clear();
        task.persist();
        return "redirect:/tasks/" + encodeUrlPathSegment(task.getId().toString(), httpServletRequest);
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Task());
        List<String[]> dependencies = new ArrayList<String[]>();
        if (AuthUser.countAuthUsers() == 0) {
            dependencies.add(new String[] { "authuser", "authusers" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "tasks/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
		Task task=Task.findTask(id);
		if(task==null || task.getOwner().getId()!=getCurrentUser().getId()){
			return "redirect:/";
		}
        uiModel.addAttribute("task", Task.findTask(id));
        uiModel.addAttribute("itemId", id);
        return "tasks/show";
    }

	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            
            List<Task> tasks=Task.findTasksByOwner(getCurrentUser()).getResultList();
            
            float nrOfPages = (float) tasks.size() / sizeNo;
            
            if(firstResult<tasks.size() && firstResult+sizeNo<tasks.size()){
            	tasks = tasks.subList(firstResult, sizeNo);
            }
            
            uiModel.addAttribute("tasks", tasks);
            
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("tasks", Task.findTasksByOwner(getCurrentUser()).getResultList());
        }
        return "tasks/list";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Task task, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if(task.getName()==null || task.getSearchTerm()==null || task.getSearchTerm().length()<3){
            populateEditForm(uiModel, task);
            return "tasks/create";
		}
		
		task.setOwner(getCurrentUser());
		task.setAnalysisResult(sentimentService.analyseSentiment(task.getSearchTerm()));
		
        uiModel.asMap().clear();
        task.merge();
        return "redirect:/tasks/" + encodeUrlPathSegment(task.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Task.findTask(id));
        return "tasks/update";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Task task = Task.findTask(id);
        task.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/tasks";
    }

	void populateEditForm(Model uiModel, Task task) {
        uiModel.addAttribute("task", task);
        uiModel.addAttribute("authusers", AuthUser.findAllAuthUsers());
    }

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
	
	private AuthUser getCurrentUser(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return AuthUser.findAuthUsersByNameEquals(username).getSingleResult();
	}
}
