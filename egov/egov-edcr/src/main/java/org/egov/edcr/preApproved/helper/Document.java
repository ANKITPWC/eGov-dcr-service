package org.egov.edcr.preApproved.helper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Document implements Serializable  {
	
	private static final long serialVersionUID = 1235517727309489863L;

	 @JsonProperty("id")
	  private String id = null;

	  @JsonProperty("documentType")
	  private String documentType = null;

	  @JsonProperty("fileStoreId")
	  private String fileStoreId = null;

	  @JsonProperty("documentUid")
	  private String documentUid = null;

	  @JsonProperty("additionalDetails")
	  private Object additionalDetails = null;
	  
	  @JsonProperty("auditDetails")
	  private AuditDetailsPreApproved auditDetails = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getFileStoreId() {
		return fileStoreId;
	}

	public void setFileStoreId(String fileStoreId) {
		this.fileStoreId = fileStoreId;
	}

	public String getDocumentUid() {
		return documentUid;
	}

	public void setDocumentUid(String documentUid) {
		this.documentUid = documentUid;
	}

	public Object getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(Object additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public AuditDetailsPreApproved getAuditDetails() {
		return auditDetails;
	}

	public void setAuditDetails(AuditDetailsPreApproved auditDetails) {
		this.auditDetails = auditDetails;
	}
	  
	  
	  
	  
}
