package com.mindtree.entity;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class DeviceEntity extends TableServiceEntity {
    public DeviceEntity(String azureIotAccount,String deviceId) {
        this.partitionKey = azureIotAccount;
        this.rowKey = deviceId;
    }

    public DeviceEntity() { }

    String Type;
    String gladiusDeviceId;
    String gladiusParentId;
  

	String configuration;
    
    public String getGladiusDeviceId() {
		return gladiusDeviceId;
	}
    
    public String getGladiusParentId() {
  		return gladiusParentId;
  	}

  	public void setGladiusParentId(String gladiusParentId) {
  		this.gladiusParentId = gladiusParentId;
  	}
	public void setGladiusDeviceId(String gladiusDeviceId) {
		this.gladiusDeviceId = gladiusDeviceId;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

    public String getType() {
        return this.Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

}