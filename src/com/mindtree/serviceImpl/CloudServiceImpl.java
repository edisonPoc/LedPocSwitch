package com.mindtree.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.microsoft.azure.iot.service.sdk.DeliveryAcknowledgement;
import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.FeedbackBatch;
import com.microsoft.azure.iot.service.sdk.FeedbackReceiver;
import com.microsoft.azure.iot.service.sdk.IotHubServiceClientProtocol;
import com.microsoft.azure.iot.service.sdk.ServiceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iot.service.sdk.Message;
import com.microsoft.azure.iot.service.sdk.RegistryManager;

public class CloudServiceImpl {
	private static final String connectionString = "HostName=LedIotSuite.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=0mpAS5WMeuJJ2g6jGyvdWBidmIzPONV5ovT3HOfoUA8=";
    //private static final String deviceId = "mydeviceLED";
    private static final IotHubServiceClientProtocol protocol_Service = IotHubServiceClientProtocol.AMQPS;
//private static final IotHubServiceClientProtocol iotHubServiceClientProtocol = IotHubServiceClientProtocol.AMQPS_WS;

private static ServiceClient serviceClient = null;
private static FeedbackReceiver feedbackReceiver = null;
public List<String> getAllDevices() throws Exception
{
	List<String> allDevices=new ArrayList<String>();
	RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);	
	List<Device> devices=registryManager.getDevices(10000);
	System.out.println("size "+devices.size());
	for(Device dev:devices)
	{
		allDevices.add(dev.getDeviceId());
	}
	return allDevices;
}
public void sendCommandToDevice(String data,String deviceId) throws Exception
{
	System.out.println("********* Starting ServiceClient sample...");

    openServiceClient();
    openFeedbackReceiver(deviceId);

    String commandMessage = data;

    System.out.println("********* Sending message to device...");

    Message messageToSend = new Message(commandMessage);
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
    System.out.println("********* Feedback received, feedback time: " + feedbackBatch.getEnqueuedTimeUtc().toString());

    closeFeedbackReceiver();
    closeServiceClient();

    System.out.println("********* Shutting down ServiceClient sample...");
    //System.exit(0);

}
protected static void openServiceClient() throws Exception
{
    System.out.println("Creating ServiceClient...");
    serviceClient = ServiceClient.createFromConnectionString(connectionString, protocol_Service);

    CompletableFuture<Void> future = serviceClient.openAsync();
    future.get();
    System.out.println("********* Successfully created an ServiceClient.");
}

protected static void closeServiceClient() throws ExecutionException, InterruptedException, IOException
{
    serviceClient.close();

    CompletableFuture<Void> future = serviceClient.closeAsync();
    future.get();
    serviceClient = null;
    System.out.println("********* Successfully closed ServiceClient.");
}

protected static void openFeedbackReceiver(String deviceId) throws ExecutionException, InterruptedException
{
    if (serviceClient != null)
    {
        feedbackReceiver = serviceClient.getFeedbackReceiver(deviceId);
        if (feedbackReceiver != null)
        {
            CompletableFuture<Void> future = feedbackReceiver.openAsync();
            future.get();
            System.out.println("********* Successfully opened FeedbackReceiver...");
        }
    }
}

protected static void closeFeedbackReceiver() throws ExecutionException, InterruptedException
{
    CompletableFuture<Void> future = feedbackReceiver.closeAsync();
    future.get();
    feedbackReceiver = null;
    System.out.println("********* Successfully closed FeedbackReceiver.");
}
}
