/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2018>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.edcr.service;

import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.egov.common.entity.dcr.helper.AuditDetailsPreApproved;
import org.egov.common.entity.dcr.helper.BlockPreApproved;
import org.egov.common.entity.dcr.helper.BuildingPreapproved;
import org.egov.common.entity.dcr.helper.EdcrApplicationInfo;
import org.egov.common.entity.dcr.helper.PlanPreApproved;
import org.egov.common.entity.dcr.helper.PreApprovedPlan;
import org.egov.common.entity.dcr.helper.floorPreApproved;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.FloorDescription;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.edcr.contract.PermitOrderRequest;
import org.egov.edcr.entity.EdcrApplicationDetail;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.utils.DateUtils;
import org.omg.CORBA.ObjectHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EdcrExternalService {

    private Logger LOG = Logger.getLogger(EdcrExternalService.class);

    /*
     * Names should same as DCR checklist name in application with underscore concatenation for each space
     */
    private static final String SITE_PLAN = "Site_Plan";
    private static final String SERVICE_PLAN = "Service_Plan";
    private static final String PARKING_PLAN = "Parking_Plan";
    private static final String BUILDING_PLAN = "Building_Plan";
    private static final String TERRACE_PLAN = "Terrace_Plan";
    private static final String ROOF_PLAN = "Roof_Plan";
    private static final String ELEVATION_PLAN = "Elevation_Plans";
    private static final String SECTION_PLAN = "Section_Plans";
    private static final String DETAILS_PLAN = "Details_Plan";
    private static final String FLOOR_PLAN = "Floor_Plans";
    private static final String FLOOR_PLAN_ELEVTN_SECTN = "Floor_Plans,_Elevations,_Sections";
    
    Object DrawingDetails =null;
    
    @Autowired
    private EdcrApplicationDetailService edcrApplicationDetailService;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private BpaService bpaService;

    public EdcrApplicationInfo loadEdcrApplicationDetails(String eDcrNumber) {
        EdcrApplicationDetail applicationDetail = edcrApplicationDetailService.findByDcrNumber(eDcrNumber);
        return buildDcrApplicationDetails(applicationDetail);
    }
    
    public PlanPreApproved loadPreApprovedData(String eDcrNumber,PermitOrderRequest permitOrderRequest) {
    	
    	
    	
    	List<PreApprovedPlan> preApprovedPlanResponse = bpaService.fetchPreApproved(permitOrderRequest.getRequestInfo(),eDcrNumber);
    	System.out.println(preApprovedPlanResponse);
    	return buildDcrPreApprovedApplicationdetails(preApprovedPlanResponse,permitOrderRequest);
    }

    private PlanPreApproved buildDcrPreApprovedApplicationdetails(List<PreApprovedPlan> preApprovedPlanResponse,PermitOrderRequest permitOrderRequest ) {
		// TODO Auto-generated method stub
    	PlanPreApproved plan = new PlanPreApproved();
    	Object obj = preApprovedPlanResponse.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	PreApprovedPlan preApprovedPlan= mapper.convertValue(
    			obj, new TypeReference<PreApprovedPlan>() {});
        
        Object additionalDetails = permitOrderRequest.getBpaList().get(0).get("additionalDetails");
         DrawingDetails = preApprovedPlan.getDrawingDetail();
//        List nocs = (List) ((Map) nocResponse).get("Noc");
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
//		List<BlockPreApproved> blocks = ( List<BlockPreApproved>) ((Map) drawingDetails).get("blocks");
//		System.out.println("blocks:"+blocks);
//		BigDecimal floorArea = new BigDecimal("0");
//		List<String> floorAreas = new ArrayList<>();
//		List<BuildingPreapproved> buildings = new ArrayList<>();
//		ObjectMapper mapper = new ObjectMapper();
//		for (Object obj : blocks) {
//			ObjectMapper mappers = new ObjectMapper();
//			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//			List<BuildingPreapproved> 	building = mapper.convertValue(
//	    			obj, new TypeReference<List<BuildingPreapproved>>() {});
//			System.out.println("blocked:"+building);
//		 buildings.addAll(building);
//	}
//		for (Object obj :buildings) {
//			System.out.println("1");
//			ObjectMapper mappers = new ObjectMapper();
//			BuildingPreapproved building = mappers.convertValue(
//	    			obj, new TypeReference<BuildingPreapproved>() {});
//			System.out.println("2");
//			List<String>	floorArrea = building.getFloors().stream().filter(f->f.getFloorArea()!=null).map(floorPreApproved::getFloorArea).collect(Collectors.toList());
//			floorAreas.addAll(floorArrea);	
//		}
//		
//		for(String floor : floorAreas) {
//			floorArea = floorArea.add(new BigDecimal(floor));
//			
//		}
		plan.setTotalfloorArea(new BigDecimal(floorArea));
		BigDecimal plotArea = plan.getPlotArea();
		if (plotArea.doubleValue() > 0) {
		BigDecimal	providedFar = plan.getTotalfloorArea().divide(plotArea, 3,
					ROUNDMODE_MEASUREMENTS);
		plan.setProvidedFar(providedFar.doubleValue());
		}
//		plan.getFarDetails().setProvidedFar(providedFar);
	}

	public EdcrApplicationInfo buildDcrApplicationDetails(EdcrApplicationDetail applicationDetail) {
        EdcrApplicationInfo applicationInfo = new EdcrApplicationInfo();
        applicationInfo.seteDcrApplicationId((applicationDetail.getApplication().getId()));
        applicationInfo
                .setApplicationDate(DateUtils.toDefaultDateFormat(applicationDetail.getApplication().getApplicationDate()));
        applicationInfo.setCreatedDate(DateUtils.toDefaultDateTimeFormat(applicationDetail.getApplication().getCreatedDate()));
        applicationInfo.setApplicationNumber(applicationDetail.getApplication().getApplicationNumber());
        applicationInfo.setDcrNumber(applicationDetail.getDcrNumber());
        applicationInfo.seteDcrApplicationId((applicationDetail.getApplication().getId()));
        applicationInfo.setDxfFile(applicationDetail.getDxfFileId());
        applicationInfo.setReportOutput(applicationDetail.getReportOutputId());
        applicationInfo.setProjectType(applicationDetail.getApplication().getProjectType());
        applicationInfo.setApplicationType(applicationDetail.getApplication().getApplicationType().getApplicationTypeVal());
        applicationInfo.setPlanPermitNumber(applicationDetail.getApplication().getPlanPermitNumber());
        applicationInfo.setServiceType(applicationDetail.getApplication().getServiceType() == null ? "N/A"
                : applicationDetail.getApplication().getServiceType());
        applicationInfo.setOwnerName(applicationDetail.getApplication().getApplicantName() == null ? "N/A"
                : applicationDetail.getApplication().getApplicantName());
        if (applicationDetail.getPlanDetailFileStore() != null)
            applicationInfo.setPlanDetailFileStore(applicationDetail.getPlanDetailFileStore().getId());
        /*
         * if (!applicationDetail.getEdcrPdfDetails().isEmpty()) { Map<String, List<FileStoreMapper>> planScrutinyPdfs = new
         * LinkedHashMap<>(); Collections.reverse(applicationDetail.getEdcrPdfDetails());
         */
        /*
         * Here we are building system generated plan pdf's and then those need to auto populate into application to corresponding
         * DCR checklist document
         */
        /*
         * applicationDetail.getEdcrPdfDetails().forEach(scrutinyPdf -> { if(scrutinyPdf != null && scrutinyPdf.getConvertedPdf()
         * != null) { List<FileStoreMapper> fileStoreMappers = new ArrayList<>();
         * fileStoreMappers.add(scrutinyPdf.getConvertedPdf()); if (scrutinyPdf.getLayer().contains(SITE_PLAN.toUpperCase())) { if
         * (planScrutinyPdfs.containsKey(SITE_PLAN)) planScrutinyPdfs.get(SITE_PLAN).add(scrutinyPdf.getConvertedPdf()); else
         * planScrutinyPdfs.put(SITE_PLAN, fileStoreMappers); } else if (scrutinyPdf.getLayer().equalsIgnoreCase(SERVICE_PLAN)) {
         * if (planScrutinyPdfs.containsKey(SERVICE_PLAN)) planScrutinyPdfs.get(SERVICE_PLAN).add(scrutinyPdf.getConvertedPdf());
         * else planScrutinyPdfs.put(SERVICE_PLAN, fileStoreMappers); } else if
         * (scrutinyPdf.getLayer().equalsIgnoreCase(PARKING_PLAN)) { if (planScrutinyPdfs.containsKey(PARKING_PLAN))
         * planScrutinyPdfs.get(PARKING_PLAN).add(scrutinyPdf.getConvertedPdf()); else planScrutinyPdfs.put(PARKING_PLAN,
         * fileStoreMappers); } else if (scrutinyPdf.getLayer().equalsIgnoreCase(BUILDING_PLAN)) { if
         * (planScrutinyPdfs.containsKey(BUILDING_PLAN)) planScrutinyPdfs.get(BUILDING_PLAN).add(scrutinyPdf.getConvertedPdf());
         * else planScrutinyPdfs.put(BUILDING_PLAN, fileStoreMappers); } else if
         * (scrutinyPdf.getLayer().equalsIgnoreCase(TERRACE_PLAN)) { if (planScrutinyPdfs.containsKey(TERRACE_PLAN))
         * planScrutinyPdfs.get(TERRACE_PLAN).add(scrutinyPdf.getConvertedPdf()); else planScrutinyPdfs.put(TERRACE_PLAN,
         * fileStoreMappers); } else if (scrutinyPdf.getLayer().contains("DETAILS_")) { if
         * (planScrutinyPdfs.containsKey(DETAILS_PLAN)) planScrutinyPdfs.get(DETAILS_PLAN).add(scrutinyPdf.getConvertedPdf());
         * else planScrutinyPdfs.put(DETAILS_PLAN, fileStoreMappers); } else if (scrutinyPdf.getLayer().contains("ROOF_PLAN_")) {
         * if (planScrutinyPdfs.containsKey(ROOF_PLAN)) planScrutinyPdfs.get(ROOF_PLAN).add(scrutinyPdf.getConvertedPdf()); else
         * planScrutinyPdfs.put(ROOF_PLAN, fileStoreMappers); } else if (scrutinyPdf.getLayer().contains("FLOOR_PLAN_") ||
         * scrutinyPdf.getLayer().contains("SECTION_") || scrutinyPdf.getLayer().contains("ELEVATION_")) { if
         * (planScrutinyPdfs.containsKey(FLOOR_PLAN_ELEVTN_SECTN))
         * planScrutinyPdfs.get(FLOOR_PLAN_ELEVTN_SECTN).add(scrutinyPdf.getConvertedPdf()); else
         * planScrutinyPdfs.put(FLOOR_PLAN_ELEVTN_SECTN, fileStoreMappers); } else if
         * (scrutinyPdf.getLayer().contains("SECTION_")) { if (planScrutinyPdfs.containsKey(SECTION_PLAN))
         * planScrutinyPdfs.get(SECTION_PLAN).add(scrutinyPdf.getConvertedPdf()); else planScrutinyPdfs.put(SECTION_PLAN,
         * fileStoreMappers); } else if (scrutinyPdf.getLayer().contains("ELEVATION_")) { if
         * (planScrutinyPdfs.containsKey(ELEVATION_PLAN)) planScrutinyPdfs.get(ELEVATION_PLAN).add(scrutinyPdf.getConvertedPdf());
         * else planScrutinyPdfs.put(ELEVATION_PLAN, fileStoreMappers); } else if (scrutinyPdf.getLayer().contains("FLOOR_PLAN_"))
         * { if (planScrutinyPdfs.containsKey(FLOOR_PLAN)) planScrutinyPdfs.get(FLOOR_PLAN).add(scrutinyPdf.getConvertedPdf());
         * else planScrutinyPdfs.put(FLOOR_PLAN, fileStoreMappers); } } }); applicationInfo.setPlanScrutinyPdfs(planScrutinyPdfs);
         * }
         */

        if (applicationDetail.getPlanDetailFileStore() == null) {
            /*
             * It is used to support approved dcr plans which are in phase 1, using approved plans should able to submit bpa
             * application.
             */
            edcrApplicationDetailService.buildBuildingDetailForApprovedPlans(applicationDetail, applicationInfo);
        } else {
            LOG.info("Before de-serialization....................");
            if (LOG.isInfoEnabled())
                LOG.info("**************** Start - Reading Plan detail file **************");
            File file = fileStoreService.fetch(applicationDetail.getPlanDetailFileStore().getFileStoreId(),
                    DcrConstants.APPLICATION_MODULE_TYPE);
            if (LOG.isInfoEnabled())
                LOG.info("**************** End - Reading Plan detail file **************" + file);
            try {

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Plan pl1 = mapper.readValue(file, Plan.class);
                if (LOG.isInfoEnabled())
                    LOG.info("**************** Plan detail object **************" + pl1);
                applicationInfo.setPlan(pl1);
            } catch (IOException e) {
                LOG.log(Level.ERROR, e);
            }
            LOG.debug("Completed de-serialization");
            if (applicationInfo.getPlan() != null)
                for (Block b : applicationInfo.getPlan().getBlocks()) {
                    for (Floor f : b.getBuilding().getFloors()) {
                        f.setName(getFloorDescription(f));
                        /*
                         * Need to be subtract existing building area from proposed building area to get actual proposed area
                         */
                        for (Occupancy occupancy : f.getOccupancies()) {
                            occupancy.setBuiltUpArea(occupancy.getBuiltUpArea().subtract(occupancy.getExistingBuiltUpArea()));
                            occupancy.setFloorArea(occupancy.getFloorArea().subtract(occupancy.getExistingFloorArea()));
                            occupancy.setCarpetArea(occupancy.getCarpetArea().subtract(occupancy.getExistingCarpetArea()));
                        }
                    }

                    /*
                     * This was used to get actual occupancies of proposed buildings, when auto populate sub occupancies, we need
                     * to consider only proposed building occupancies. We should not consider existing building occupancies.
                     */
                    for (Occupancy actualOccupancy : b.getBuilding().getTotalArea()) {
                        actualOccupancy.setBuiltUpArea(
                                actualOccupancy.getBuiltUpArea().subtract(actualOccupancy.getExistingBuiltUpArea()));
                        actualOccupancy
                                .setFloorArea(actualOccupancy.getFloorArea().subtract(actualOccupancy.getExistingFloorArea()));
                        actualOccupancy
                                .setCarpetArea(actualOccupancy.getCarpetArea().subtract(actualOccupancy.getExistingCarpetArea()));
                    }
                    b.getBuilding().setTotalBuitUpArea(
                            b.getBuilding().getTotalBuitUpArea().subtract(b.getBuilding().getTotalExistingBuiltUpArea()));
                    b.getBuilding().setTotalFloorArea(
                            b.getBuilding().getTotalBuitUpArea().subtract(b.getBuilding().getTotalExistingBuiltUpArea()));
                }
        }

        if (applicationInfo.getPlan() != null && applicationInfo.getPlan().getPlanInformation() != null) {
            applicationInfo.setAmenities(applicationInfo.getPlan().getPlanInformation().getAmenities() == null ? "N/A"
                    : applicationInfo.getPlan().getPlanInformation().getAmenities());
            applicationInfo.setOccupancy(applicationInfo.getPlan().getPlanInformation().getOccupancy() == null ? "N/A"
                    : applicationInfo.getPlan().getPlanInformation().getOccupancy());
            applicationInfo.setArchitectInformation(
                    applicationInfo.getPlan().getPlanInformation().getArchitectInformation() == null ? "N/A"
                            : applicationInfo.getPlan().getPlanInformation().getArchitectInformation());
            applicationInfo.setPlotArea(applicationInfo.getPlan().getPlanInformation().getPlotArea() == null ? BigDecimal.ZERO
                    : applicationInfo.getPlan().getPlanInformation().getPlotArea());
        } else {
            if (LOG.isInfoEnabled())
                LOG.info("**************** Error Occurred while de-serialization **************"
                        + applicationDetail.getDcrNumber());
            if (applicationDetail.getApplication().getPlanInformation() != null) {
                applicationInfo
                        .setAmenities(applicationDetail.getApplication().getPlanInformation().getAmenities() == null ? "N/A"
                                : applicationDetail.getApplication().getPlanInformation().getAmenities());
                applicationInfo
                        .setServiceType(applicationDetail.getApplication().getPlanInformation().getServiceType() == null ? "N/A"
                                : applicationDetail.getApplication().getPlanInformation().getServiceType());
                applicationInfo
                        .setOccupancy(applicationDetail.getApplication().getPlanInformation().getOccupancy() == null ? "N/A"
                                : applicationInfo.getPlan().getPlanInformation().getOccupancy());
                applicationInfo.setArchitectInformation(
                        applicationDetail.getApplication().getPlanInformation().getArchitectInformation() == null ? "N/A"
                                : applicationDetail.getApplication().getPlanInformation().getArchitectInformation());
                applicationInfo.setPlotArea(
                        applicationDetail.getApplication().getPlanInformation().getPlotArea() == null ? BigDecimal.ZERO
                                : applicationDetail.getApplication().getPlanInformation().getPlotArea());
                applicationInfo
                        .setOwnerName(applicationDetail.getApplication().getPlanInformation().getOwnerName() == null ? "N/A"
                                : applicationDetail.getApplication().getPlanInformation().getOwnerName());
            }
        }

        return applicationInfo;
    }

    private String getFloorDescription(Floor floor) {
        String name;
        if (floor.getNumber() < 0)
            name = FloorDescription.CELLAR_FLOOR.getFloorDescriptionVal();
        else if (floor.getNumber() > 0 && floor.getTerrace())
            name = FloorDescription.TERRACE_FLOOR.getFloorDescriptionVal();
        else if (floor.getNumber() > 0)
            name = FloorDescription.UPPER_FLOOR.getFloorDescriptionVal();
        else
            name = FloorDescription.GROUND_FLOOR.getFloorDescriptionVal();
        return name;
    }

	public List<BlockPreApproved> getblockDetails() {
		
		List<BlockPreApproved> blocks = ( List<BlockPreApproved>) ((Map) DrawingDetails).get("blocks");
	 
		
		return blocks;
	}
	/*
	Object nocResponse = nocService.fetchNocs(requestInfo, tenantId, bpaApplicationNo);
	Set<String> nocviewableNames = new HashSet<>();
	if (Objects.nonNull(nocResponse) && nocResponse instanceof Map && ((Map) nocResponse).get("Noc") instanceof List
			&& !CollectionUtils.isEmpty((List) ((Map) nocResponse).get("Noc"))) {
		List nocs = (List) ((Map) nocResponse).get("Noc");
		Set<String> nocNames = (Set<String>) nocs.stream().map(noc -> String.valueOf(((Map) noc).get("nocType")))
				.collect(Collectors.toSet());
	public Map<String, BigDecimal> getSetBackData() {	
		List<BlockPreApproved> blocks = ( List<BlockPreApproved>) ((Map) DrawingDetails).get("blocks");
	}*/
		
//	}
}