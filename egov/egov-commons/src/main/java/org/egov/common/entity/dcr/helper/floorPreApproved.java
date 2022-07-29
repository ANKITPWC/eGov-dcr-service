package org.egov.common.entity.dcr.helper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class floorPreApproved  implements Serializable {
	
	private static final long serialVersionUID = 1237877745309489098L;
	
	
	
	@JsonProperty("floorNo")
	private String floorNo;
	
	
	@JsonProperty("floorArea")
	private String floorArea;
	
	
	@JsonProperty("floorName")
	private String floorName;
	
	
	@JsonProperty("carpetArea")
	private String carpetArea;
	
	
	@JsonProperty("builtUpArea")
	private String builtUpArea;
	
	
	@JsonProperty("floorHeight")
	private String floorHeight;


	public String getFloorNo() {
		return floorNo;
	}


	public void setFloorNo(String floorNo) {
		this.floorNo = floorNo;
	}


	public String getFloorArea() {
		return floorArea;
	}


	public void setFloorArea(String floorArea) {
		this.floorArea = floorArea;
	}


	public String getFloorName() {
		return floorName;
	}


	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}


	public String getCarpetArea() {
		return carpetArea;
	}


	public void setCarpetArea(String carpetArea) {
		this.carpetArea = carpetArea;
	}


	public String getBuiltUpArea() {
		return builtUpArea;
	}


	public void setBuiltUpArea(String builtUpArea) {
		this.builtUpArea = builtUpArea;
	}


	public String getFloorHeight() {
		return floorHeight;
	}


	public void setFloorHeight(String floorHeight) {
		this.floorHeight = floorHeight;
	}
	
	
	
	

}
