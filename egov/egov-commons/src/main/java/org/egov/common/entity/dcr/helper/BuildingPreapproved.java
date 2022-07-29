package org.egov.common.entity.dcr.helper;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildingPreapproved implements Serializable {
	
	private static final long serialVersionUID = 1237877727309489098L;
	
	@JsonProperty("floors")
	private List<floorPreApproved> floors;
	
	@JsonProperty("setBack")
	private List<setBackPreApproved> setBack;
	
	@JsonProperty("totalFloors")
	private int totalFloors;
	
	@JsonProperty("buildingHeight")
	private String buildingHeight;
	
	
	@JsonProperty("actualBuildingHeight")
	private String actualBuildingHeight;


	public List<floorPreApproved> getFloors() {
		return floors;
	}


	public void setFloors(List<floorPreApproved> floors) {
		this.floors = floors;
	}


	public List<setBackPreApproved> getSetBack() {
		return setBack;
	}


	public void setSetBack(List<setBackPreApproved> setBack) {
		this.setBack = setBack;
	}


	public int getTotalFloors() {
		return totalFloors;
	}


	public void setTotalFloors(int totalFloors) {
		this.totalFloors = totalFloors;
	}


	public String getBuildingHeight() {
		return buildingHeight;
	}


	public void setBuildingHeight(String buildingHeight) {
		this.buildingHeight = buildingHeight;
	}


	public String getActualBuildingHeight() {
		return actualBuildingHeight;
	}


	public void setActualBuildingHeight(String actualBuildingHeight) {
		this.actualBuildingHeight = actualBuildingHeight;
	}


}
