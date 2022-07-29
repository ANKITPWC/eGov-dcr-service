package org.egov.common.entity.dcr.helper;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockPreApproved implements Serializable {
	
	private static final long serialVersionUID = 1236977727309489098L;
	
	@JsonProperty("building")
	private BuildingPreapproved building;

	public BuildingPreapproved getBuilding() {
		return building;
	}

	public void setBuilding(BuildingPreapproved building) {
		this.building = building;
	}

	

	
	
}
