
package org.egov.edcr.service;

import java.util.HashMap;
import java.util.Map;

import org.egov.commons.service.RestCallService;
import org.egov.infra.microservice.models.RequestInfo;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@Service
public class PaymentService {
	private RestCallService serviceRequestRepository;

	public PaymentService(RestCallService serviceRequestRepository) {
		this.serviceRequestRepository = serviceRequestRepository;
	}

	public StringBuilder getPaymentsSearchUrl(String businessservice) {
		return new StringBuilder().append("http://localhost:8083/").append("collection-services/payments/")
				.append(businessservice).append("/_search");
	}

	public Object fetchApplicationFeePaymentDetails(RequestInfo requestInfo, String consumerCode, String tenantId) {
		StringBuilder searchUrl = getPaymentsSearchUrl("BPA.NC_APP_FEE").append("?consumerCodes=").append(consumerCode)
				.append("&tenantId=").append(tenantId);
		Map<String, Object> requestInfoPayload = new HashMap<>();
		requestInfoPayload.put("RequestInfo", requestInfo);
		Object result = serviceRequestRepository.fetchResult(searchUrl, requestInfoPayload);
		return result;
	}

	public Object fetchPermitFeePaymentDetails(RequestInfo requestInfo, String consumerCode, String tenantId) {
		StringBuilder searchUrl = getPaymentsSearchUrl("BPA.NC_SAN_FEE").append("?consumerCodes=").append(consumerCode)
				.append("&tenantId=").append(tenantId);
		Map<String, Object> requestInfoPayload = new HashMap<>();
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
