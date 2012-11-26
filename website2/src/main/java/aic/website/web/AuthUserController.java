package aic.website.web;

import aic.website.domain.AuthRole;
import aic.website.domain.AuthUser;
import aic.website.domain.Task;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/authusers")
@Controller
@RooWebScaffold(path = "authusers", formBackingObject = AuthUser.class)
public class AuthUserController {

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid AuthUser authUser, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, authUser);
            return "authusers/create";
        }
        uiModel.asMap().clear();
        
        authUser.setPassword(calcHash(authUser.getPassword()));
        
        authUser.persist();
        return "redirect:/authusers/" + encodeUrlPathSegment(authUser.getId().toString(), httpServletRequest);
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid AuthUser authUser, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, authUser);
            return "authusers/update";
        }
        uiModel.asMap().clear();
        String password=authUser.getPassword();
        if(password!=null){
        	authUser.setPassword(calcHash(password));
        }
        authUser.merge();
        return "redirect:/authusers/" + encodeUrlPathSegment(authUser.getId().toString(), httpServletRequest);
    }

	void populateEditForm(Model uiModel, AuthUser authUser) {
        uiModel.addAttribute("authUser", authUser);
        uiModel.addAttribute("authroles", AuthRole.findAllAuthRoles());
        uiModel.addAttribute("tasks", Task.findAllTasks());
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
	
	private String calcHash(String password) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());

        StringBuffer sb = new StringBuffer();
        for(byte b : hash) {
        	int v=b & 0xff;
        	if(v<16)
        		sb.append('0');
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
	}
}
