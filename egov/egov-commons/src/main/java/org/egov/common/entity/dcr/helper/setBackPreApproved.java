package org.egov.common.entity.dcr.helper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class setBackPreApproved implements Serializable  {
	
	
	
	private static final long serialVersionUID = 1237877745386589098L;
	
	
	@JsonProperty("leftSetback")
	private String leftSetback;
	
	
	@JsonProperty("rearSetback")
	private String rearSetback;
	
	
	@JsonProperty("frontSetback")
	private String frontSetback;
	
	
	@JsonProperty("rightSetback")
	private String rightSetback;


	public String getLeftSetback() {
		return leftSetback;
	}


	public void setLeftSetback(String leftSetback) {
		this.leftSetback = leftSetback;
	}


	public String getRearSetback() {
		return rearSetback;
	}


	public void setRearSetback(String rearSetback) {
		this.rearSetback = rearSetback;
	}


	public String getFrontSetback() {
		return frontSetback;
	}


	public void setFrontSetback(String frontSetback) {
		this.frontSetback = frontSetback;
	}


	public String getRightSetback() {
		return rightSetback;
	}


	public void setRightSetback(String rightSetback) {
		this.rightSetback = rightSetback;
	}
	
	
	
	

}
