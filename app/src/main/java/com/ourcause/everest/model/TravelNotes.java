package com.ourcause.everest.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by infonous on 16-9-2.
 * 将所拍摄的图片信息、配置信息， 以及测量比对信息保存， 以便于以后浏览
 */
public class TravelNotes implements Serializable {

    private static final long serialVersionUID = -7060260544600464445L;

    private Integer travelNotesPtr;
    private String notes;
    private Date lastUpdateDateTime;

    private TravelMomentSetting itemTravelMomentSetting;
    private TravelCameraImageInfo itemTravelCameraImageInfo;

    public Integer getTravelNotesPtr() {
        return travelNotesPtr;
    }

    public void setTravelNotesPtr(Integer travelNotesPtr) {
        this.travelNotesPtr = travelNotesPtr;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    public void setLastUpdateDateTime(Date lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
    }

    public TravelMomentSetting getItemTravelMomentSetting() {
        return itemTravelMomentSetting;
    }

    public void setItemTravelMomentSetting(TravelMomentSetting itemTravelMomentSetting) {
        this.itemTravelMomentSetting = itemTravelMomentSetting;
    }

    public TravelCameraImageInfo getItemTravelCameraImageInfo() {
        return itemTravelCameraImageInfo;
    }

    public void setItemTravelCameraImageInfo(TravelCameraImageInfo itemTravelCameraImageInfo) {
        this.itemTravelCameraImageInfo = itemTravelCameraImageInfo;
    }
}
