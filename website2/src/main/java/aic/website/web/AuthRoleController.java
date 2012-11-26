package aic.website.web;

import aic.website.domain.AuthRole;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/authroles")
@Controller
@RooWebScaffold(path = "authroles", formBackingObject = AuthRole.class)
public class AuthRoleController {
}
