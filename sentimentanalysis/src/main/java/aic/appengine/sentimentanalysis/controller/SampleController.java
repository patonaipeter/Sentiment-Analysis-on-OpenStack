package aic.appengine.sentimentanalysis.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

@Controller
public class SampleController extends AbstractController {

	@Override
	@RequestMapping("/sample")
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView("index");
		mav.addObject("greeting", "Hello User");

		String name = "peter";
		String email = "email";

		Key customerKey = KeyFactory.createKey("Customer", name);

		Date date = new Date();
		Entity customer = new Entity("Customer", customerKey);
		customer.setProperty("name", name);
		customer.setProperty("email", email);
		customer.setProperty("date", date);

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		datastore.put(customer);

		System.out.println(customer.getProperty("name"));

		return mav;
	}

	// get all customers
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listCustomer(ModelMap model) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Query query = new Query("Customer").addSort("date",
				Query.SortDirection.DESCENDING);
		List<Entity> customers = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(10));

		model.addAttribute("name", customers.get(0).getProperty("name"));

		return "list";

	}
}
