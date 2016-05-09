<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style>
 .myButton {
	-moz-box-shadow: 0px 0px 0px 0px #40e014;
	-webkit-box-shadow: 0px 0px 0px 0px #40e014;
	box-shadow: 0px 0px 0px 0px #40e014;
	background-color:#44c767;
	-moz-border-radius:35px;
	-webkit-border-radius:35px;
	border-radius:35px;
	border:1px solid #18ab29;
	display:inline-block;
	cursor:pointer;
	color:#ffffff;
	font-family:Verdana;
	font-size:17px;
	padding:18px 48px;
	text-decoration:none;
	text-shadow:0px 1px 0px #2f6627;
}
.myButton:hover {
	background-color:#5cbf2a;
}
.myButton:active {
	position:relative;
	top:1px;
}
.myButtonOff {
	-moz-box-shadow: 0px 0px 0px 0px #40e014;
	-webkit-box-shadow: 0px 0px 0px 0px #40e014;
	box-shadow: 0px 0px 0px 0px #40e014;
	background-color:#f53434;
	-moz-border-radius:35px;
	-webkit-border-radius:35px;
	border-radius:35px;
	border:1px solid #18ab29;
	display:inline-block;
	cursor:pointer;
	color:#ffffff;
	font-family:Verdana;
	font-size:17px;
	padding:18px 48px;
	text-decoration:none;
	text-shadow:0px 1px 0px #2f6627;
}
.myButtonOff :hover {
	background-color:#a31515;
}
.myButtonOff :active {
	position:relative;
	top:1px;
}

        


</style>
<body>

<script>
 flag=0;
 </script>
 <div align="center">
<h1>Service integration with Azure IOT Hub as a switch</h1>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
   <div>
   <c:forEach items="${devices}" var="device" varStatus="loop">
   <h3>${device}
  	<a style="margin-left: 0%;" id="${device}" onclick="changeButton('${device}')" class="myButtonOff">OFF</a>
   Switch Status <input type="text" id="switch${device}" value="0"></h3>
   <br/>
   <br/>
   </c:forEach>
   </div>
   
   <p><b>Click the switch to turn the bulb on/off</b></p> 
   <input id="deviceList" type="hidden" value="${devices}"/>
   <br><br>

</div>
  <script>
  deviceStatus=0;
  commandCounter=0;
  $( document ).ready(function() {
	    console.log( "Document Ready!" );
	    deviceListElements=document.getElementById("deviceList");
	    deviceListElements=deviceListElements.value;
	    deviceListElements=deviceListElements.substring(1,deviceListElements.length-1);
	    deviceListElements=deviceListElements.split(", ");
	    console.log(deviceListElements);
	    for(var i=0;i<deviceListElements.length;i++)
	    	{
	     $.ajax({
	  		type : 'GET',
	  		url : 'http://deviceinformationstatus.azurewebsites.net/IotDeviceInformation/getDeviceStatus', 
	  		data : {
	  			deviceID : deviceListElements[i]
	  		},
	  		async: false,
	  		headers: {"Access-Control-Expose-Headers":"Content-Disposition","Access-Control-Allow-Credentials":"true"},
	  		success : function(data) {
	  			console.log("Device Status Recieved");
	  			console.log(data);
	  			deviceStatus=data;
	  			if((deviceStatus!="")&&(deviceStatus!=" "))
	  				{
	  			var inputType=document.getElementById("switch"+deviceListElements[i]);
	  			inputType.value=deviceStatus;
	  			changeButtonStatus(deviceListElements[i]);
	  				}
	  		},
			error : function(error) {
				console.log("Could not send the command");
			}
	  	}); 
	    	}
	});
	function changeButtonStatus() {
	  var deviceId=arguments[0];
	  var buttonType=document.getElementById(deviceId);
	  if((deviceStatus===1)||(deviceStatus==1))
	  {
		  buttonType.className="myButton";
		  buttonType.innerText="ON";
	  }else if((deviceStatus===0)||(deviceStatus==0))
	  {
		  buttonType.className="myButtonOff";
		  buttonType.innerText="OFF";
	  }
  }
  function changeButton() {
	  var deviceId=arguments[0];
	  var buttonType=document.getElementById(deviceId);
	  if(buttonType.innerText==="ON")
	  {
		  buttonType.className="myButtonOff";
		  buttonType.innerText="OFF";
		  deviceStatus=0;
	  }else
	  {
		  buttonType.className="myButton";
		  buttonType.innerText="ON";
		  deviceStatus=1;
	  }
	    console.log("Making ajax request to send command");
	       $.ajax({
	  		type : 'POST',
	  		url : 'sendCommand',
	  		data : {
	  			deviceStatus : deviceStatus,
	  			deviceId : deviceId
	  		},
	  		success : function(data) {
	  			console.log("Command successfully sent");
	  			var inputType=document.getElementById("switch"+deviceId);
	  			inputType.value=deviceStatus;
	  		  
	  		},
			error : function(error) {
				console.log("Could not send the command");
			}
	  	}); 
  }
    </script>
 
</body>

</html>