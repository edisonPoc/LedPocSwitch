package com.mindtree.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.mindtree.serviceImpl.CloudServiceImpl;

@Controller
public class CloudController {
	CloudServiceImpl cloudService;
	@ResponseBody
	@RequestMapping(value = "/sendCommand", method = RequestMethod.POST)
	public void sendCommand(@RequestParam("deviceStatus") String data,@RequestParam("deviceId") String deviceId,@RequestParam("gladiusChildFlag") boolean gladiusChildFlag) throws Exception {
		System.out.println("Sending data to Azure IOT Hub");
		cloudService = new CloudServiceImpl();
		cloudService.sendCommandToDevice(data,deviceId,gladiusChildFlag);
	}
	@RequestMapping(value = "/getDeviceList", method = RequestMethod.GET)
	public ModelAndView getDeviceList() throws Exception{
		ModelAndView model = new ModelAndView();
		cloudService=new CloudServiceImpl();
		System.out.println("Getting list of devices running on the Azure IOT Hub");
		List<Object> deviceList=cloudService.getAllDevices();
		HashMap<String,String> devices=(HashMap<String, String>) deviceList.get(0);
		HashMap<String,String> gladiusChildDevices=(HashMap<String,String>) deviceList.get(1);
		model.addObject("devices",devices);
		model.addObject("gladiusChildDevices",gladiusChildDevices);
		model.setViewName("switchControl.jsp");
		return model;
	}
	@ResponseBody
	@RequestMapping(value = "/showActivity", method = RequestMethod.GET)
	public String showActivity(@RequestParam("deviceId") String deviceId) throws Exception{
		cloudService=new CloudServiceImpl();
		System.out.println("Getting list of device activity");
		JSONArray deviceData=cloudService.getDeviceActivity(deviceId);
		String json = new Gson().toJson(deviceData);		
		return json;
	}
}