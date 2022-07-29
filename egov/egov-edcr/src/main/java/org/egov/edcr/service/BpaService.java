package org.egov.edcr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.entity.dcr.helper.PreApprovedPlan;
import org.egov.commons.mdms.config.MdmsConfiguration;
import org.egov.commons.service.RestCallService;
import org.egov.infra.microservice.models.RequestInfo;
import org.springframework.stereotype.Service;

@Service
public class BpaService {
	private RestCallService serviceRequestRepository;
	private MdmsConfiguration mdmsConfiguration;
	
	
	public  BpaService(RestCallService serviceRequestRepository,MdmsConfiguration mdmsConfiguration) {
		this.serviceRequestRepository = serviceRequestRepository;
		this.mdmsConfiguration = mdmsConfiguration;
	}
	
	public StringBuilder getPreApprovedSearchUrl(String drawingNo) {
			String hostUrl = mdmsConfiguration.getBpaHost();
		
		String url = String.format("%s/bpa-services/v1/preapprovedplan/_search?drawingNo="+drawingNo, hostUrl);
		System.out.println("url"+url);
		return new StringBuilder(url);
	}
	
	public List<PreApprovedPlan> fetchPreApproved(RequestInfo requestInfo,  String drawingNo) {
		StringBuilder searchUrl = getPreApprovedSearchUrl(drawingNo);
		Map<String, Object> requestInfoPayload = new HashMap<>();
		requestInfoPayload.put("RequestInfo", requestInfo);
		Object result = serviceRequestRepository.fetchResult(searchUrl, requestInfoPayload);
		List<PreApprovedPlan> preApprove = (List<PreApprovedPlan>) ((Map) result).get("preapprovedPlan");
		return preApprove;
	

}
}
