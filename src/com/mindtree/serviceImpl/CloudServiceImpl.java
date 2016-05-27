package com.mindtree.serviceImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.microsoft.azure.iot.service.sdk.DeliveryAcknowledgement;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.FeedbackBatch;
import com.microsoft.azure.iot.service.sdk.FeedbackReceiver;
import com.microsoft.azure.iot.service.sdk.IotHubServiceClientProtocol;
import com.microsoft.azure.iot.service.sdk.ServiceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;
import com.mindtree.entity.CommandData;
import com.mindtree.entity.DeviceEntity;
import com.mindtree.entity.DeviceTelemetry;
import com.microsoft.azure.iot.service.sdk.Message;
import com.microsoft.azure.iot.service.sdk.RegistryManager;

public class CloudServiceImpl {
	private static final String connectionString = "HostName=LedIotSolution.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=HWYsgV2YckGE4K5qBmWKmLZJbkMaIR5pYgId3b2H8N8=";
	// private static final String deviceId = "mydeviceLED";
	private static final IotHubServiceClientProtocol protocol_Service = IotHubServiceClientProtocol.AMQPS;
	// private static final IotHubServiceClientProtocol
	// iotHubServiceClientProtocol = IotHubServiceClientProtocol.AMQPS_WS;
	public static final String storageConnectionString = "DefaultEndpointsProtocol=http;"
			+ "AccountName=lediotsolution;"
			+ "AccountKey=4UmXKhpd+9VUL3usGRVj3hspk+oP85YIzxEiVwjWQNjzZLz7tfuNTAD+a3BuAReG0YLCJ7yjam/1Ywsw3TveXQ==";
	final String PARTITION_KEY = "PartitionKey";
	final String ROW_KEY = "RowKey";
	final String TIMESTAMP = "Timestamp";
	private static ServiceClient serviceClient = null;
	private static FeedbackReceiver feedbackReceiver = null;

	public List<Object> getAllDevices() throws Exception {
		List<Object> returnAllDevices=new ArrayList<Object>();
		HashMap<String, String> allDevices = new HashMap<String, String>();
		RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);
		List<Device> devices = registryManager.getDevices(10000);
		HashMap<String,String> gladiusChildDevices=new HashMap<String, String>();
		
		System.out.println("size " + devices.size());
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		// Create the table client.
		CloudTableClient tableClient = storageAccount.createCloudTableClient();

		// Create a cloud table object for the table.
		CloudTable cloudTable = tableClient.getTableReference("DeviceList");
		// Output the entity.

		for (Device dev : devices) {
			TableOperation retrieveData = TableOperation.retrieve("LedIotSolution",dev.getDeviceId(),
					DeviceEntity.class);

			// Submit the operation to the table service and get the specific
			// entity.
			DeviceEntity specificEntity = cloudTable.execute(retrieveData).getResultAsType();
			if(specificEntity.getType().equals("Gladius_Child"))
			{
				System.out.println("Inside GladiusChild");
				System.out.println("Parent Gladius is "+specificEntity.getGladiusParentId());
				gladiusChildDevices.put(dev.getDeviceId(),specificEntity.getGladiusParentId());
				allDevices.put(dev.getDeviceId(), specificEntity.getType());
			}
			else
			{
				allDevices.put(dev.getDeviceId(), specificEntity.getType());
			}
		}
		returnAllDevices.add(allDevices);
		returnAllDevices.add(gladiusChildDevices);
		return returnAllDevices;
	}

	public void sendCommandToDevice(String data, String deviceId, boolean gladiusChildFlag) throws Exception {
		System.out.println("********* Starting ServiceClient sample...");

		openServiceClient();
		openFeedbackReceiver(deviceId);

		// String commandMessage = data;
		CommandData commandData = new CommandData();
		commandData.setName("ChangeLEDState");
		commandData.setMessageId("123223");
		commandData.setCreatedTime(new Date().toString());
		if(gladiusChildFlag==false)
		{
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("LEDState", data);
		commandData.setParameters(parameter);
		}
		else
		{
		commandData.setGladiusParameters(data);	
		}
		System.out.println(commandData.serialize());
		System.out.println("********* Sending message to device...");
		Message messageToSend = new Message(commandData.serialize());
		System.out.println(commandData.serialize());
		messageToSend.setDeliveryAcknowledgement(DeliveryAcknowledgement.Full);

		// Setting standard properties
		messageToSend.setMessageId(java.util.UUID.randomUUID().toString());
		Date now = new Date();
		messageToSend.setExpiryTimeUtc(new Date(now.getTime() + 60 * 1000));
		messageToSend.setCorrelationId(java.util.UUID.randomUUID().toString());
		messageToSend.setUserId(java.util.UUID.randomUUID().toString());
		messageToSend.clearCustomProperties();

		// Setting user properties
		Map<String, String> propertiesToSend = new HashMap<String, String>();
		propertiesToSend.put("mycustomKey1", "mycustomValue1");
		propertiesToSend.put("mycustomKey2", "mycustomValue2");
		propertiesToSend.put("mycustomKey3", "mycustomValue3");
		propertiesToSend.put("mycustomKey4", "mycustomValue4");
		propertiesToSend.put("mycustomKey5", "mycustomValue5");
		messageToSend.setProperties(propertiesToSend);

		CompletableFuture<Void> completableFuture = serviceClient.sendAsync(deviceId, messageToSend);
		completableFuture.get();

		System.out.println("********* Waiting for feedback...");
		CompletableFuture<FeedbackBatch> future = feedbackReceiver.receiveAsync();
		FeedbackBatch feedbackBatch = future.get();
		System.out.println(
				"********* Feedback received, feedback time: " + feedbackBatch.getEnqueuedTimeUtc().toString());

		closeFeedbackReceiver();
		closeServiceClient();

		System.out.println("********* Shutting down ServiceClient sample...");
		// System.exit(0);

	}

	protected static void openServiceClient() throws Exception {
		System.out.println("Creating ServiceClient...");
		serviceClient = ServiceClient.createFromConnectionString(connectionString, protocol_Service);

		CompletableFuture<Void> future = serviceClient.openAsync();
		future.get();
		System.out.println("********* Successfully created an ServiceClient.");
	}

	protected static void closeServiceClient() throws ExecutionException, InterruptedException, IOException {
		serviceClient.close();

		CompletableFuture<Void> future = serviceClient.closeAsync();
		future.get();
		serviceClient = null;
		System.out.println("********* Successfully closed ServiceClient.");
	}

	protected static void openFeedbackReceiver(String deviceId) throws ExecutionException, InterruptedException {
		if (serviceClient != null) {
			feedbackReceiver = serviceClient.getFeedbackReceiver(deviceId);
			if (feedbackReceiver != null) {
				CompletableFuture<Void> future = feedbackReceiver.openAsync();
				future.get();
				System.out.println("********* Successfully opened FeedbackReceiver...");
			}
		}
	}

	protected static void closeFeedbackReceiver() throws ExecutionException, InterruptedException {
		CompletableFuture<Void> future = feedbackReceiver.closeAsync();
		future.get();
		feedbackReceiver = null;
		System.out.println("********* Successfully closed FeedbackReceiver.");
	}

	public JSONArray getDeviceActivity(String deviceId)
			throws InvalidKeyException, URISyntaxException, StorageException {
		// TODO Auto-generated method stub
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		// Create the table client.
		CloudTableClient tableClient = storageAccount.createCloudTableClient();

		// Create a cloud table object for the table.
		CloudTable cloudTable = tableClient.getTableReference("DeviceTelemetry");
		String partitionFilter = TableQuery.generateFilterCondition(PARTITION_KEY, QueryComparisons.EQUAL, deviceId);
		TableQuery<DeviceTelemetry> partitionQuery =
			       TableQuery.from(DeviceTelemetry.class)
			       .where(partitionFilter);
		JSONObject deviceActivity;
		
		JSONArray deviceActivityArray = new JSONArray();
		List<DeviceTelemetry> deviceTelemetryArray=new ArrayList<DeviceTelemetry>();
		for(DeviceTelemetry latestDeviceData: cloudTable.execute(partitionQuery))
		{
			deviceTelemetryArray.add(latestDeviceData);
		}
		DeviceTelemetry latestData;
		int count=0;
				while(deviceTelemetryArray.size()>0)
				{
					latestData=deviceTelemetryArray.get(0);
			    // Loop through the results, displaying information about the entity.
			    for(int i=0;i<deviceTelemetryArray.size();i++) {
			    	if((deviceTelemetryArray.get(i).getTimestamp().after(latestData.getTimestamp()))||(deviceTelemetryArray.get(i).getTimestamp().equals(latestData.getTimestamp())))
			    	{
			    		latestData=deviceTelemetryArray.get(i);
			    		count=i;
			    	}
			    	    }
			    
			    deviceTelemetryArray.remove(count);
			    deviceActivity= new JSONObject();
		    	 deviceActivity.put("time", latestData.getTimestamp().toString());
		    	 deviceActivity.put("status", latestData.getData());
		    	 System.out.println(deviceActivity);
		    	 deviceActivityArray.add(deviceActivity);
		}
			   
		 
			    
		return deviceActivityArray;
	}
}
