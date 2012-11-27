package aic.website.web;

import aic.website.domain.AuthUser;
import aic.website.domain.Task;
import aic.website.service.IService;

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
		
		long startTime = System.currentTimeMillis();
		task.setAnalysisResult(sentimentService.analyseSentiment(task.getSearchTerm()));
		long endTime   = System.currentTimeMillis();
		task.setRuntime(endTime - startTime);
		
        uiModel.asMap().clear();
        task.persist();
        return "redirect:/tasks/" + encodeUrlPathSegment(task.getId().toString(), httpServletRequest);
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
            return "tasks/update";
		}
		
		task.setOwner(getCurrentUser());
		
		Task old=Task.findTask(task.getId());
		task.setCreated(old.getCreated());
		
		if(!old.getSearchTerm().equals(task.getSearchTerm())){
			long startTime = System.currentTimeMillis();
			task.setAnalysisResult(sentimentService.analyseSentiment(task.getSearchTerm()));
			long endTime   = System.currentTimeMillis();
			task.setRuntime(endTime - startTime);
		}else{
			task.setAnalysisResult(old.getAnalysisResult());
			task.setRuntime(old.getRuntime());
		}
		
        uiModel.asMap().clear();
        task.merge();
        return "redirect:/tasks/" + encodeUrlPathSegment(task.getId().toString(), httpServletRequest);
    }

	
	private AuthUser getCurrentUser(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return AuthUser.findAuthUsersByNameEquals(username).getSingleResult();
	}
}
