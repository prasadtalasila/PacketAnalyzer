/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.bits.protocolanalyzer.mvc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.ac.bits.protocolanalyzer.mvc.model.Experiment;
/**
 *
 * @author crygnus
 */
@Controller
@RequestMapping("/session")
public class SessionController {

	@Autowired
	private Experiment experiment;
	
	@RequestMapping(value = "/analysis", method = RequestMethod.GET)
	public @ResponseBody String analyze(
			@RequestParam("graph") String protocolGraphStr,
			@RequestParam("pcapPath") String pcapPath) throws Exception {
		experiment.initWithPcapFileCheck(pcapPath,protocolGraphStr);
		String result = experiment.analyze();
		return result;
	}
}
