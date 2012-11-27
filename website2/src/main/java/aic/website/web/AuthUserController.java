package aic.website.web;

import aic.website.domain.AuthRole;
import aic.website.domain.AuthUser;
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

@RequestMapping("/authusers")
@Controller
@RooWebScaffold(path = "authusers", formBackingObject = AuthUser.class)
public class AuthUserController {
    
    @RequestMapping(params = "register", produces = "text/html")
    public String createRegisterForm(Model uiModel) {
        populateEditForm(uiModel, new AuthUser());
        return "authusers/register";
    }
    
	@RequestMapping(params = "register",method = RequestMethod.POST, produces = "text/html")
    public String createRegister(@Valid AuthUser authUser, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
		if (authUser.getName() == null || authUser.getName().length() < 3
				|| authUser.getPassword() == null
				|| authUser.getPassword().length() < 3) {
			populateEditForm(uiModel, authUser);
			return "authusers/register";
		}
        uiModel.asMap().clear();
        
        authUser.setPassword(calcHash(authUser.getPassword()));
        
        authUser.setEnabled(true);
        
        authUser.getRoles().clear();
        authUser.getRoles().add(AuthRole.findAuthRolesByNameEquals("ROLE_USER").getSingleResult());
        
        authUser.getTasks().clear();
        
        authUser.persist();
        return "redirect:/tasks";
    }
    
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid AuthUser authUser, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, authUser);
            return "authusers/create";
        }
        uiModel.asMap().clear();
        
        authUser.setPassword(calcHash(authUser.getPassword()));
        
        if(authUser.getRoles().size()==0){
        	authUser.getRoles().clear();
        	authUser.getRoles().add(AuthRole.findAuthRolesByNameEquals("ROLE_USER").getSingleResult());
        }
        
        authUser.getTasks().clear();
        
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
        if(password!=null && password.length()>0){
        	authUser.setPassword(calcHash(password));
        }else{
        	AuthUser old=AuthUser.findAuthUser(authUser.getId());
        	authUser.setPassword(old.getPassword());
        }
        authUser.merge();
        return "redirect:/authusers/" + encodeUrlPathSegment(authUser.getId().toString(), httpServletRequest);
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
