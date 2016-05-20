<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<style>
.myButton {
	-moz-box-shadow: 0px 0px 0px 0px #40e014;
	-webkit-box-shadow: 0px 0px 0px 0px #40e014;
	box-shadow: 0px 0px 0px 0px #40e014;
	background-color: #44c767;
	-moz-border-radius: 35px;
	-webkit-border-radius: 35px;
	border-radius: 35px;
	border: 1px solid #18ab29;
	display: inline-block;
	cursor: pointer;
	color: #ffffff;
	font-family: Verdana;
	font-size: 17px;
	padding: 18px 48px;
	text-decoration: none;
	text-shadow: 0px 1px 0px #2f6627;
}

.myButton:hover {
	background-color: #5cbf2a;
}

.myButton:active {
	position: relative;
	top: 1px;
}

.myButtonOff {
	-moz-box-shadow: 0px 0px 0px 0px #40e014;
	-webkit-box-shadow: 0px 0px 0px 0px #40e014;
	box-shadow: 0px 0px 0px 0px #40e014;
	background-color: #f53434;
	-moz-border-radius: 35px;
	-webkit-border-radius: 35px;
	border-radius: 35px;
	border: 1px solid #18ab29;
	display: inline-block;
	cursor: pointer;
	color: #ffffff;
	font-family: Verdana;
	font-size: 17px;
	padding: 18px 48px;
	text-decoration: none;
	text-shadow: 0px 1px 0px #2f6627;
}

.myButtonOff :hover {
	background-color: #a31515;
}

.myButtonOff :active {
	position: relative;
	top: 1px;
}

.lavaLampWithImage li a:hover {
	background: #DDD;
	border-radius: 15px 15px 15px 15px;
	-moz-border-radius: 15px 15px 15px 15px;
	-webkit-border: 15px 15px 15px 15px;
	height: 35px;
	z-index: 8;
	position: absolute;
	margin-top: -4px;
}
</style>
<script type="text/javascript" src="js/jquery-1.12.3.min.js"></script>
<script type="text/javascript" src="js/jquery.easing.min.js"></script>
<script type="text/javascript" src="js/jquery.lavalamp.min.js"></script>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js">
	<script type="text/javascript">
	$(function() {
		$("#lava_menu").lavaLamp({
			fx : "backout",
			speed : 700
		});
	});
</script>
<head>
<title>Switch implementation of Light Bulb demo</title>
<meta name="description" content="website description" />
<meta name="keywords" content="website keywords, website keywords" />
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="css/style2.css" />
<!-- modernizr enables HTML5 elements and feature detects -->
<script type="text/javascript" src="js/modernizr-1.5.min.js"></script>
</head>
<body>
	<div id="main">
		<header>
			<div id="logo">
				<h1>Light Bulb Demo</h1>
			</div>
			<nav>
				<ul class="lavaLampWithImage" id="lava_menu">
					<li><a href="dashboard.jsp">Home</a></li>
					<li class="current"><a href="getDeviceList">Switch Control</a></li>
					<li><a href="index.jsp">Logout</a></li>
				</ul>
			</nav>
		</header>
		<div id="site_content" align="center">
			<h1>Service integration with Azure IOT Hub as a switch</h1>
			<div>
				<c:forEach items="${devices}" var="device" varStatus="loop">
					<h3 id="header${loop.index}">
						<b id="device${loop.index}"></b> <img
							style="margin-left: 10%; margin-top: 0%" class="${loop.index}"
							id="switch${device}" src="pic_bulboff.gif" width="55" height="55">
						<a style="margin-left: 10%; margin-bottom: 2%" id="${device}"
							onclick="changeButton('${device}')" class="myButton">ON</a> <a
							id="activity${loop.index}" data-toggle="modal"
							data-target="#myModal${loop.index}" href="#"
							onclick="getDeviceActivityData('${device}')"
							style="margin-left: 10%;">Activity History</a>
					</h3>
					<br />
					<div class="modal fade" id="myModal${loop.index}" role="dialog">
						<div class="modal-dialog modal-lg">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal">&times;</button>
									<h4 class="modal-title">Device Activity Log</h4>
								</div>
								<div class="modal-body">
									<p id="modalData${device}"></p>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Close</button>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>

			</div>

			<p>
				<b>Click the switch to turn the bulb on/off</b>
			</p>
			<input id="deviceList" type="hidden" value="${devices}" /> <br>
			<br>

		</div>
		<footer>
			<p>
				<a href="index.jsp">Logout</a> | <a href="getDeviceList">Switch
					Control</a>
			</p>
			<p>
				&copy; LedIotHub Dashboard | <a
					href="http://www.css3templates.co.uk">design from
					css3templates.co.uk</a>
			</p>
		</footer>
	</div>
	<script>
		deviceStatus = 0;
		commandCounter = 0;
		$(document)
				.ready(
						function() {
							console.log("Document Ready!");
							deviceListElements = document
									.getElementById("deviceList");
							deviceListElements = deviceListElements.value;
							console.log(deviceListElements);
							deviceListElements = deviceListElements.substring(
									1, deviceListElements.length - 1);
							deviceListElements = deviceListElements.split(", ");
							console.log(deviceListElements);
							deviceList = [];
							for (var i = 0; i < deviceListElements.length; i++) {
								var deviceListElementBreak = deviceListElements[i]
										.split("=");
								console.log(deviceListElementBreak[0] + " "
										+ deviceListElementBreak[1]);
								deviceList[i] = deviceListElementBreak[0];
								deviceName = document.getElementById("device"
										+ i);
								deviceName.innerHTML = deviceListElementBreak[0];
								headerItem = document.getElementById("header"
										+ i);
								console.log(headerItem);
								if (deviceListElementBreak[1] === 'Simulated') {
									headerItem.style.background = "#74fcf5";
								} else if (deviceListElementBreak[1] === 'Rasberry') {
									headerItem.style.background = "#d5ff80";
								}
								$
										.ajax({
											type : 'GET',
											url : 'http://lediotpoc.cloudapp.net/IotDeviceInformation/getDeviceStatus',
											data : {
												deviceID : deviceListElementBreak[0]
											},
											async : false,
											headers : {
												"Access-Control-Expose-Headers" : "Content-Disposition",
												"Access-Control-Allow-Credentials" : "true"
											},
											success : function(data) {
												console
														.log("Device Status Recieved");
												console.log(data);
												deviceStatus = data;
												if ((deviceStatus != "")
														&& (deviceStatus != " ")) {
													var inputType = document
															.getElementById("switch"
																	+ deviceListElements[i]);
													if (deviceStatus === '1') {
														inputType.src = "pic_bulbon.gif";
													} else if (deviceStatus === '0') {
														inputType.src = "pic_bulboff.gif";
													}
													changeButtonStatus(deviceListElements[i]);
												}
											},
											error : function(error) {
												console
														.log("Could not send the command");
											}
										});
							}
						});
		function getDeviceActivityData() {
			deviceData = arguments[0];
			var deviceDataList = arguments[0].split("=");
			console.log(deviceDataList[0] + " " + deviceDataList[1]);
			deviceId = deviceDataList[0];
			$.ajax({
				type : 'GET',
				url : 'showActivity',
				data : {
					deviceId : deviceId
				},
				success : function(data) {
					console.log("Got device activity data successfully");
					console.log(data);
					deviceActivityData=JSON.parse(data);
					dataInsideModal="";
					for(var i=0;i<deviceActivityData.length;i++)
						{
					dataInsideModal=dataInsideModal+deviceActivityData[i].time+"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"+deviceActivityData[i].status+"<br/>";
						}
					modalDataEntry = document.getElementById("modalData"
							+ deviceData);
					console.log(modalDataEntry);
					modalDataEntry.innerHTML=dataInsideModal;

				},
				error : function(error) {
					console.log("Could not send the command");
				}
			});

		}
		function changeButtonStatus() {
			var deviceInfo = arguments[0];
			var buttonType = document.getElementById(deviceInfo);
			var inputType = document.getElementById("switch" + arguments[0]);
			inputType.value = deviceStatus;
			if ((deviceStatus === 1) || (deviceStatus == 1)) {
				buttonType.className = "myButtonOff";
				buttonType.innerText = "OFF";

			} else if ((deviceStatus === 0) || (deviceStatus == 0)) {
				buttonType.className = "myButton";
				buttonType.innerText = "ON";
			}
		}
		function changeButton() {
			deviceInformation = arguments[0];
			var deviceId = arguments[0].split("=");
			deviceId = deviceId[0];
			var buttonType = document.getElementById(arguments[0]);
			if (buttonType.innerText === "ON") {
				buttonType.className = "myButtonOff";
				buttonType.innerText = "OFF";
				deviceStatus = 1;
			} else {
				buttonType.className = "myButton";
				buttonType.innerText = "ON";
				deviceStatus = 0;
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
					var inputType = document.getElementById("switch"
							+ deviceInformation);
					if ((deviceStatus === '1') || (deviceStatus === 1)) {
						inputType.src = "pic_bulbon.gif";
						;
					} else if ((deviceStatus === '0') || (deviceStatus === 0)) {
						inputType.src = "pic_bulboff.gif";
						;
					}
				},
				error : function(error) {
					console.log("Could not send the command");
				}
			});
		}
	</script>

</body>

</html>