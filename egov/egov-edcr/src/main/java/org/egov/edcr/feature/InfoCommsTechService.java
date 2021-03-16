/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.ICT;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.od.OdishaUtill;
import org.egov.edcr.service.PlanService;
import org.springframework.stereotype.Service;

@Service
public class InfoCommsTechService extends FeatureProcess {
	private static final Logger LOG = Logger.getLogger(InfoCommsTechService.class);
	private static final BigDecimal ONE_ARCH=new BigDecimal("4046.86");
	private static final BigDecimal FIFTEEN=new BigDecimal("15");
	@Override
	public Plan validate(Plan plan) {
		return plan;
	}

	@Override
	public Plan process(Plan pl) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		scrutinyDetail.setKey("Common_ICT provision near main entrance gate");

		OccupancyTypeHelper helper = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
		boolean isApplicable = false;

		if (DxfFileConstants.APARTMENT_BUILDING.equals(helper.getSubtype().getCode()))
			isApplicable = true;
		if ((DxfFileConstants.OC_COMMERCIAL.equals(helper.getType().getCode()) || DxfFileConstants.OC_PUBLIC_SEMI_PUBLIC_OR_INSTITUTIONAL.equals(helper.getType().getCode()))
				&& pl.getPlot().getArea().compareTo(ONE_ARCH) >= 0)
			isApplicable = true;
		if(OdishaUtill.getMaxBuildingHeight(pl).compareTo(FIFTEEN)>0)
			isApplicable=true;
		if(pl.getPlot().getArea().compareTo(ONE_ARCH) >= 0)
			isApplicable=true;
		
		//height
		BigDecimal requiredHeight=new BigDecimal("3");//min
		BigDecimal requiredArea=new BigDecimal("12");//min
		BigDecimal requiredDoorWidth=new BigDecimal("1.2");
		long requiredDoorCount=2;
		
		BigDecimal minDoorWidth=BigDecimal.ZERO;
		BigDecimal minArea=BigDecimal.ZERO;
		BigDecimal minHeight=BigDecimal.ZERO;
		long count=0;
		try {
			minDoorWidth=pl.getIcts().stream().map(ict -> ict.getDoors().stream().map(m -> m.getWidth()).reduce(BigDecimal::min).get()).reduce(BigDecimal::min).get();
			minDoorWidth=minDoorWidth.setScale(2,BigDecimal.ROUND_HALF_UP);
			count=pl.getIcts().stream().map(ict -> ict.getDoors().size()).reduce(Integer::sum).get();
			minArea=pl.getIcts().stream().map(ict -> ict.getRooms().stream().map(r -> r.getArea()).reduce(BigDecimal::min).get()).reduce(BigDecimal::min).get();
			minArea=minArea.setScale(2, BigDecimal.ROUND_HALF_UP);
			minHeight=pl.getIcts().stream().map(ict -> ict.getHeights().stream().map(rh -> rh.getHeight()).reduce(BigDecimal::min).get()).reduce(BigDecimal::min).get();
			minHeight=minHeight.setScale(2,BigDecimal.ROUND_HALF_UP);
		}catch (Exception e) {
			LOG.error(e.getMessage());
		}
		
		
		if(isApplicable) {
			//Height
			addDetails(scrutinyDetail, RULE_NO, "Height", requiredHeight.toString(), minHeight.toString(), minHeight.compareTo(requiredHeight)>=0?Result.Accepted.getResultVal():Result.Not_Accepted.getResultVal());
			
			//Area
			addDetails(scrutinyDetail, RULE_NO, "Area", requiredArea.toString(), minArea.toString(), minArea.compareTo(requiredArea)>=0?Result.Accepted.getResultVal():Result.Not_Accepted.getResultVal());

			//Fire door width
			addDetails(scrutinyDetail, RULE_NO, "Fire door width", requiredDoorWidth.toString(), minDoorWidth.toString(), minDoorWidth.compareTo(requiredDoorWidth)>=0?Result.Accepted.getResultVal():Result.Not_Accepted.getResultVal());
			
			//Fire door count
			addDetails(scrutinyDetail, RULE_NO, "Fire door count", requiredDoorCount+"", count+"", count>=requiredDoorCount?Result.Accepted.getResultVal():Result.Not_Accepted.getResultVal());
			
		}
		
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		return pl;
	}
	
	private BigDecimal getDoorWidth(List<ICT> icts) {
		BigDecimal width=BigDecimal.ZERO;
		
		BigDecimal minWidth=icts.stream().map(ict -> ict.getDoors().stream().map(m -> m.getWidth()).reduce(BigDecimal::min).get()).reduce(BigDecimal::min).get();
		
		return width;
	}
	
	private void addDetails(ScrutinyDetail scrutinyDetail, String rule, String description, String required,
			String provided, String status) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, rule);
		details.put(DESCRIPTION, description);
		details.put(REQUIRED, required);
		details.put(PROVIDED, provided);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
	}
	

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
