package com.mindtree.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mindtree.serviceImpl.CloudServiceImpl;

@Controller
public class CloudController {
	CloudServiceImpl cloudService;
	@ResponseBody
	@RequestMapping(value = "/sendCommand", method = RequestMethod.POST)
	public void sendCommand(@RequestParam("deviceStatus") String data,@RequestParam("deviceId") String deviceId) throws Exception {
		System.out.println("Sending data to Azure IOT Hub");
		cloudService = new CloudServiceImpl();
		cloudService.sendCommandToDevice(data,deviceId);
	}
	@RequestMapping(value = "/getDeviceList", method = RequestMethod.GET)
	public ModelAndView getDeviceList() throws Exception{
		ModelAndView model = new ModelAndView();
		cloudService=new CloudServiceImpl();
		System.out.println("Getting list of devices running on the Azure IOT Hub");
		List<String> devices=cloudService.getAllDevices();
		model.addObject("devices",devices);
		model.setViewName("switchControl.jsp");
		return model;
	}
}