package org.egov.common.entity.dcr.helper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuditDetailsPreApproved implements Serializable {
	
	private static final long serialVersionUID = 1235517727309489053L;

	@JsonProperty("createdBy")
	  private String createdBy = null;

	  @JsonProperty("lastModifiedBy")
	  private String lastModifiedBy = null;

	  @JsonProperty("createdTime")
	  private Long createdTime = null;

	  @JsonProperty("lastModifiedTime")
	  private Long lastModifiedTime = null;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public Long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	  
	  
}
