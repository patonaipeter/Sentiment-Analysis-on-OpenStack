package at.ac.tuwien.frontend.website.web;

import at.ac.tuwien.frontend.website.domain.CloudUser;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/cloudusers")
@Controller
@RooWebScaffold(path = "cloudusers", formBackingObject = CloudUser.class)
public class CloudUserController {
}
