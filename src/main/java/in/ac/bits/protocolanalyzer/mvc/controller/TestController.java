package in.ac.bits.protocolanalyzer.mvc.controller;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.ac.bits.protocolanalyzer.mvc.model.Experiment;




@Controller
@RequestMapping("/test")
public class TestController {

	@Autowired
	@Qualifier("Experiment")
	private Experiment exp1;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String runExp(@RequestParam("protocolGraphPath") String protocolGraphPath, @RequestParam("pcapPath") String pcapPath){
		
		String experimentResults = null;
		try {
 			exp1.init(pcapPath, protocolGraphPath);
 			experimentResults = exp1.analyze();
 		}
 		catch(Exception e) {
 	    	return (new JSONObject()).put("packetCount", 0).put("status", "failure").put("comment", e.getMessage()).toString();
		}
		
		return experimentResults;
	}
}
