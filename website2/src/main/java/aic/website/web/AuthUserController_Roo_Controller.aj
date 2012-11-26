// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package aic.website.web;

import aic.website.domain.AuthUser;
import aic.website.web.AuthUserController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect AuthUserController_Roo_Controller {
    
    @RequestMapping(params = "form", produces = "text/html")
    public String AuthUserController.createForm(Model uiModel) {
        populateEditForm(uiModel, new AuthUser());
        return "authusers/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String AuthUserController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("authuser", AuthUser.findAuthUser(id));
        uiModel.addAttribute("itemId", id);
        return "authusers/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String AuthUserController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("authusers", AuthUser.findAuthUserEntries(firstResult, sizeNo));
            float nrOfPages = (float) AuthUser.countAuthUsers() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("authusers", AuthUser.findAllAuthUsers());
        }
        return "authusers/list";
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String AuthUserController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, AuthUser.findAuthUser(id));
        return "authusers/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String AuthUserController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        AuthUser authUser = AuthUser.findAuthUser(id);
        authUser.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/authusers";
    }
    
}
