package com.mindtree.entity;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class DeviceEntity extends TableServiceEntity {
    public DeviceEntity(String deviceId, String azureIotAccount) {
        this.partitionKey = deviceId;
        this.rowKey = azureIotAccount;
    }

    public DeviceEntity() { }

    String type;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}