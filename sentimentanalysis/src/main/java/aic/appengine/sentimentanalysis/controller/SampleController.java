package aic.appengine.sentimentanalysis.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@Controller
public class SampleController extends AbstractController {

	@Override
	@RequestMapping("/sample")
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("SimpleController was called");
		ModelAndView mav = new ModelAndView("index");
		mav.addObject("greeting", "Hello User");
		return mav;
	}
}
