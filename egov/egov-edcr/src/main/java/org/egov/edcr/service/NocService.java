
package org.egov.edcr.service;

import java.util.HashMap;
import java.util.Map;

import org.egov.commons.mdms.config.MdmsConfiguration;
import org.egov.commons.service.RestCallService;
import org.egov.infra.microservice.models.RequestInfo;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@Service
public class NocService {
	private RestCallService serviceRequestRepository;

	public NocService(RestCallService serviceRequestRepository) {
		this.serviceRequestRepository = serviceRequestRepository;
	}

	public StringBuilder getNocSearchUrl() {
		return new StringBuilder().append("http://localhost:8083/").append("noc-services/v1/noc").append("/_search");
	}

	public Object fetchNocs(RequestInfo requestInfo,String tenantId,String bpaApplicationNo) {
		StringBuilder searchUrl = getNocSearchUrl().append("?sourceRefId=").append(bpaApplicationNo)
				.append("&tenantId=").append(tenantId);
		Map<String,Object> requestInfoPayload = new HashMap<>();
		requestInfoPayload.put("RequestInfo", requestInfo);
		Object result = serviceRequestRepository.fetchResult(searchUrl, requestInfoPayload);
		return result;
	}
	
	public String getValue(Map dataMap, String key) {
		String jsonString = new JSONObject(dataMap).toString();
		DocumentContext context = JsonPath.using(Configuration.defaultConfiguration()).parse(jsonString);
		return context.read(key) + "";
	}
}
