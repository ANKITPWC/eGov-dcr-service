package org.egov.edcr.preApproved.helper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;



public class PreApprovedPlan implements Serializable {
	
	private static final long serialVersionUID = 1235517727309489098L;
	
	
	@JsonProperty("id")
	private String id = null;

	@JsonProperty("drawingNo")
	private String drawingNo = null;

	@JsonProperty("plotLength")
	private BigDecimal plotLength = null;

	@JsonProperty("plotWidth")
	private BigDecimal plotWidth = null;

	@JsonProperty("roadWidth")
	private BigDecimal roadWidth = null;

	@JsonProperty("drawingDetail")
	private Object drawingDetail = null;

	@JsonProperty("active")
	private boolean active;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	
	@JsonProperty("auditDetails")
	private AuditDetailsPreApproved auditDetails = null;
	
	@JsonProperty("documents")
	@Valid
	private List<Document> documents = null;
	
	


//	public PreApprovedPlan(PreApprovedPlan preApprovedPlan) {
//		super();
//		//TODO Auto-generated constructor stub
//	}

	public AuditDetailsPreApproved getAuditDetails() {
		return auditDetails;
	}

	public void setAuditDetails(AuditDetailsPreApproved auditDetails) {
		this.auditDetails = auditDetails;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDrawingNo() {
		return drawingNo;
	}

	public void setDrawingNo(String drawingNo) {
		this.drawingNo = drawingNo;
	}

	public BigDecimal getPlotLength() {
		return plotLength;
	}

	public void setPlotLength(BigDecimal plotLength) {
		this.plotLength = plotLength;
	}

	public BigDecimal getPlotWidth() {
		return plotWidth;
	}

	public void setPlotWidth(BigDecimal plotWidth) {
		this.plotWidth = plotWidth;
	}

	public BigDecimal getRoadWidth() {
		return roadWidth;
	}

	public void setRoadWidth(BigDecimal roadWidth) {
		this.roadWidth = roadWidth;
	}

	public Object getDrawingDetail() {
		return drawingDetail;
	}

	public void setDrawingDetail(Object drawingDetail) {
		this.drawingDetail = drawingDetail;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Object getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(Object additionalDetails) {
		this.additionalDetails = additionalDetails;
	}



	
		
	

}
