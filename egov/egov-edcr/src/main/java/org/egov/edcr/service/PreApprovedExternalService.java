package org.egov.edcr.service;

import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;

import org.egov.edcr.contract.PermitOrderRequest;
import org.egov.edcr.service.BpaService;
import org.egov.edcr.service.EdcrExternalService;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import org.egov.edcr.preApproved.helper.BlockPreApproved;
import org.egov.edcr.preApproved.helper.PlanPreApproved;
import org.egov.edcr.preApproved.helper.PreApprovedPlan;

@Service
public class PreApprovedExternalService {

	private Logger LOG = Logger.getLogger(PreApprovedExternalService.class);


	
	 
	    @Autowired
	    private FileStoreService fileStoreService;
	    @Autowired
	    private BpaService bpaService;

	    Object DrawingDetails =null;
	    
	    public PlanPreApproved loadPreApprovedData(String eDcrNumber,LinkedHashMap bpaApplication, RequestInfo requestInfo) {
	    	
	    	
	    	
	    	List<PreApprovedPlan> preApprovedPlanResponse = bpaService.fetchPreApproved(requestInfo,eDcrNumber);
	    	System.out.println(preApprovedPlanResponse);
	    	return buildDcrPreApprovedApplicationdetails(preApprovedPlanResponse,bpaApplication);
	    }
	    private PlanPreApproved buildDcrPreApprovedApplicationdetails(List<PreApprovedPlan> preApprovedPlanResponse,LinkedHashMap bpaApplication ) {
			// TODO Auto-generated method stub
	    	PlanPreApproved plan = new PlanPreApproved();
	    	Object obj = preApprovedPlanResponse.get(0);
	    	ObjectMapper mapper = new ObjectMapper();
	    	PreApprovedPlan preApprovedPlan= mapper.convertValue(
	    			obj, new TypeReference<PreApprovedPlan>() {});
	        
	        Object additionalDetails = bpaApplication.get("additionalDetails");
	         DrawingDetails = preApprovedPlan.getDrawingDetail();
//	        List nocs = (List) ((Map) nocResponse).get("Noc");
	        //String plotNo = (String ) ((Map)) DrawingDetails).get("plotNo");
	         System.out.println(additionalDetails);
	         //System.out.println((String ) ((Map) additionalDetails).get("plotNo"));
	         
	         Object planDetails = ((Map) additionalDetails).get("planDetail");
	         Object plot =  ((Map) planDetails).get("plot");
	         
	         Object planInformation = ((Map) planDetails).get("planInformation");
	        		 
	        plan.setPlotNo((String ) ((Map) plot).get("plotNo"));
	        
	        plan.setKhataNo((String ) ((Map) planInformation).get("khataNo"));
	        
	        plan.setPlotArea(new BigDecimal( (String )((Map) plot).get("area")));
	        
	        plan.setServiceType((String ) ((Map) DrawingDetails).get("serviceType"));
	        
	        plan.setFloorInfo((String ) ((Map) DrawingDetails).get("floorDescription"));
	        
	       // plan.setSubOccupancy((String ) ((Map) DrawingDetails).get("subOccupancy"));
	        
	        Object subocc = ((Map) DrawingDetails).get("subOccupancy");
	        
	        plan.setSubOccupancy((String)  ((Map) subocc).get("label"));
	        
	        plan.setRoadWidth(preApprovedPlan.getRoadWidth());
	        
	        plan.setTotalBuitUpArea(new BigDecimal((Double)  ((Map) DrawingDetails).get("totalBuitUpArea")));
	        
	        setTotalFloorAreaAndFar(plan,DrawingDetails);
	        
	       
	        return plan;
			
		}

		private void setTotalFloorAreaAndFar(PlanPreApproved plan, Object drawingDetails) {
			
			Double floorArea = (Double) ((Map) DrawingDetails).get("totalCarpetArea");
//			List<BlockPreApproved> blocks = ( List<BlockPreApproved>) ((Map) drawingDetails).get("blocks");
//			System.out.println("blocks:"+blocks);
//			BigDecimal floorArea = new BigDecimal("0");
//			List<String> floorAreas = new ArrayList<>();
//			List<BuildingPreapproved> buildings = new ArrayList<>();
//			ObjectMapper mapper = new ObjectMapper();
//			for (Object obj : blocks) {
//				ObjectMapper mappers = new ObjectMapper();
//				mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//				List<BuildingPreapproved> 	building = mapper.convertValue(
//		    			obj, new TypeReference<List<BuildingPreapproved>>() {});
//				System.out.println("blocked:"+building);
//			 buildings.addAll(building);
//		}
//			for (Object obj :buildings) {
//				System.out.println("1");
//				ObjectMapper mappers = new ObjectMapper();
//				BuildingPreapproved building = mappers.convertValue(
//		    			obj, new TypeReference<BuildingPreapproved>() {});
//				System.out.println("2");
//				List<String>	floorArrea = building.getFloors().stream().filter(f->f.getFloorArea()!=null).map(floorPreApproved::getFloorArea).collect(Collectors.toList());
//				floorAreas.addAll(floorArrea);	
//			}
//			
//			for(String floor : floorAreas) {
//				floorArea = floorArea.add(new BigDecimal(floor));
//				
//			}
			plan.setTotalfloorArea(new BigDecimal(floorArea));
			BigDecimal plotArea = plan.getPlotArea();
			if (plotArea.doubleValue() > 0) {
			BigDecimal	providedFar = plan.getTotalfloorArea().divide(plotArea, 3,
						ROUNDMODE_MEASUREMENTS);
			plan.setProvidedFar(providedFar.doubleValue());
			}
//			plan.getFarDetails().setProvidedFar(providedFar);
		}

		public List<BlockPreApproved> getblockDetails() {
			
			List<BlockPreApproved> blocks = ( List<BlockPreApproved>) ((Map) DrawingDetails).get("blocks");
		 
			
			return blocks;
		}

}
