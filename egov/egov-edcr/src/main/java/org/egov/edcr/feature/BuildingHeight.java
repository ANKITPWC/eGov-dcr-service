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

import static org.egov.edcr.constants.DxfFileConstants.OPEN_SPACE_USE_ZONE;
import static org.egov.edcr.constants.DxfFileConstants.SPECIAL_HERITAGE_ZONE;
import static org.egov.edcr.utility.DcrConstants.HEIGHT_OF_BUILDING;
import static org.egov.edcr.utility.DcrConstants.SECURITY_ZONE;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.CulDeSacRoad;
import org.egov.common.entity.edcr.Lane;
import org.egov.common.entity.edcr.NonNotifiedRoad;
import org.egov.common.entity.edcr.NotifiedRoad;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.stereotype.Service;

@Service
public class BuildingHeight extends FeatureProcess {
	private static final String RULE_EXPECTED_KEY = "buildingheight.expected";
	private static final String RULE_ACTUAL_KEY = "buildingheight.actual";
	private static final String SECURITYZONE_RULE_EXPECTED_KEY = "securityzone.expected";
	private static final String SECURITYZONE_RULE_ACTUAL_KEY = "securityzone.actual";

	private static final String SUB_RULE_32_1A = "32-1A";
	private static final String SUB_RULE_32_3 = "32-3";
	public static final String UPTO = "Up To";
	public static final String DECLARED = "Declared";
	private static final BigDecimal TEN = BigDecimal.valueOf(10);

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	@Override
	public Plan process(Plan Plan) {
		checkBuildingHeight(Plan);
		return Plan;
	}

	private void checkBuildingHeight(Plan plan) {
		String subRule = SUB_RULE_32_1A;

		for (Block block : plan.getBlocks()) {
			ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, UPTO);
			scrutinyDetail.addColumnHeading(4, PROVIDED);
			scrutinyDetail.addColumnHeading(5, STATUS);
			scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Height of Building");

			BigDecimal exptectedDistance = BigDecimal.ZERO;
			BigDecimal actualDistance = BigDecimal.ZERO;

			exptectedDistance = getMaxBulHeight(plan);
			actualDistance = block.getBuilding().getBuildingHeight();

			String actualResult = actualDistance.toString();
			String expectedResult = DxfFileConstants.NA;

			if (exptectedDistance.compareTo(BigDecimal.ZERO) > 0)
				expectedResult = exptectedDistance.toString();
			;

			if (actualDistance.compareTo(exptectedDistance) >= 0) {
				Map<String, String> details = new HashMap<>();
				details.put(RULE_NO, subRule);
				details.put(DESCRIPTION, HEIGHT_OF_BUILDING + " for Block " + block.getNumber());
				details.put(UPTO, expectedResult);
				details.put(PROVIDED, actualResult);
				details.put(STATUS, Result.Accepted.getResultVal());
				scrutinyDetail.getDetail().add(details);
				plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

			} else {
				Map<String, String> details = new HashMap<>();
				details.put(RULE_NO, subRule);
				details.put(DESCRIPTION, HEIGHT_OF_BUILDING + " for Block " + block.getNumber());
				details.put(UPTO, expectedResult);
				details.put(PROVIDED, actualResult);
				details.put(STATUS, Result.Not_Accepted.getResultVal());
				scrutinyDetail.getDetail().add(details);
				plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

			}
			// }
		}
	}

	private void checkBuildingInSecurityZoneArea(Plan Plan) {

		if (Plan.getPlanInformation().getSecurityZone()) {
			BigDecimal maxBuildingHeight = BigDecimal.ZERO;
			for (Block block : Plan.getBlocks()) {
				if (maxBuildingHeight.compareTo(BigDecimal.ZERO) == 0
						|| block.getBuilding().getBuildingHeight().compareTo(maxBuildingHeight) >= 0) {
					maxBuildingHeight = block.getBuilding().getBuildingHeight();
				}
			}
			if (maxBuildingHeight.compareTo(BigDecimal.ZERO) > 0) {

				scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.setKey("Common_Security Zone");
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, REQUIRED);
				scrutinyDetail.addColumnHeading(4, PROVIDED);
				scrutinyDetail.addColumnHeading(5, STATUS);

				String actualResult = getLocaleMessage(SECURITYZONE_RULE_ACTUAL_KEY, maxBuildingHeight.toString());
				String expectedResult = getLocaleMessage(SECURITYZONE_RULE_EXPECTED_KEY, TEN.toString());

				if (maxBuildingHeight.compareTo(TEN) <= 0) {
					Map<String, String> details = new HashMap<>();
					details.put(RULE_NO, SUB_RULE_32_3);
					details.put(DESCRIPTION, SECURITY_ZONE);
					details.put(REQUIRED, expectedResult);
					details.put(PROVIDED, actualResult);
					details.put(STATUS, Result.Verify.getResultVal());
					scrutinyDetail.getDetail().add(details);
					Plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
				} else {
					Map<String, String> details = new HashMap<>();
					details.put(RULE_NO, SUB_RULE_32_3);
					details.put(DESCRIPTION, SECURITY_ZONE);
					details.put(REQUIRED, expectedResult);
					details.put(PROVIDED, actualResult);
					details.put(STATUS, Result.Not_Accepted.getResultVal());
					scrutinyDetail.getDetail().add(details);
					Plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
				}
			}
		} else {
			scrutinyDetail = new ScrutinyDetail();
			scrutinyDetail.setKey("Common_Security Zone");
			scrutinyDetail.addColumnHeading(1, RULE_NO);
			scrutinyDetail.addColumnHeading(2, DESCRIPTION);
			scrutinyDetail.addColumnHeading(3, DECLARED);
			scrutinyDetail.addColumnHeading(4, STATUS);

			Map<String, String> details = new HashMap<>();
			details.put(RULE_NO, SUB_RULE_32_3);
			details.put(DESCRIPTION, SECURITY_ZONE);
			details.put(DECLARED, "No");
			details.put(STATUS, Result.Verify.getResultVal());
			scrutinyDetail.getDetail().add(details);
			Plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

		}

	}

	private BigDecimal getMaximumDistanceFromRoadEdge(BigDecimal maximumDistanceToRoadEdge, Block block) {
		if (block.getBuilding().getDistanceFromBuildingFootPrintToRoadEnd() != null) {
			for (BigDecimal distanceFromroadEnd : block.getBuilding().getDistanceFromBuildingFootPrintToRoadEnd()) {
				if (distanceFromroadEnd.compareTo(maximumDistanceToRoadEdge) > 0) {
					maximumDistanceToRoadEdge = distanceFromroadEnd;
				}
			}
		}
		return maximumDistanceToRoadEdge;
	}

	private BigDecimal getMaximumDistanceFromSetBackToBuildingLine(BigDecimal distanceFromSetbackToBuildingLine,
			Block block) {
		if (block.getBuilding().getDistanceFromSetBackToBuildingLine() != null) {
			for (BigDecimal distance : block.getBuilding().getDistanceFromSetBackToBuildingLine()) {
				if (distance.compareTo(distanceFromSetbackToBuildingLine) > 0) {
					distanceFromSetbackToBuildingLine = distance;
				}
			}
		}
		return distanceFromSetbackToBuildingLine;
	}

	private BigDecimal getMaximimShortestdistanceFromRoad(Plan Plan, BigDecimal maximumDistanceToRoad) {
		if (Plan.getNonNotifiedRoads() != null)
			for (NonNotifiedRoad nonnotifiedRoad : Plan.getNonNotifiedRoads())
				for (BigDecimal shortDistance : nonnotifiedRoad.getShortestDistanceToRoad())
					if (shortDistance.compareTo(maximumDistanceToRoad) > 0) {
						maximumDistanceToRoad = shortDistance;
					}
		if (Plan.getNotifiedRoads() != null)
			for (NotifiedRoad notifiedRoad : Plan.getNotifiedRoads())
				for (BigDecimal shortDistance : notifiedRoad.getShortestDistanceToRoad())
					if (shortDistance.compareTo(maximumDistanceToRoad) > 0) {
						maximumDistanceToRoad = shortDistance;
					}
		if (Plan.getCuldeSacRoads() != null)
			for (CulDeSacRoad culdRoad : Plan.getCuldeSacRoads())
				for (BigDecimal shortDistance : culdRoad.getShortestDistanceToRoad())
					if (shortDistance.compareTo(maximumDistanceToRoad) > 0) {
						maximumDistanceToRoad = shortDistance;
					}
		if (Plan.getLaneRoads() != null)
			for (Lane lane : Plan.getLaneRoads())
				for (BigDecimal shortDistance : lane.getShortestDistanceToRoad())
					if (shortDistance.compareTo(maximumDistanceToRoad) > 0) {
						maximumDistanceToRoad = shortDistance;
					}
		return maximumDistanceToRoad;
	}

	private BigDecimal getMaxBulHeight(Plan plan) {
		BigDecimal maxPermissibleHeight = BigDecimal.ZERO;
		OccupancyTypeHelper occupancyTypeHelper = plan.getVirtualBuilding().getMostRestrictiveFarHelper();
		switch (plan.getPlanInformation().getLandUseZone()) {
		case OPEN_SPACE_USE_ZONE:
			maxPermissibleHeight = new BigDecimal("3.5");
			break;
		case SPECIAL_HERITAGE_ZONE:
			maxPermissibleHeight = new BigDecimal("15");
			break;
		}

		if (maxPermissibleHeight.compareTo(BigDecimal.ZERO) > 0)
			return maxPermissibleHeight;

		if (DxfFileConstants.PETROL_PUMP_FILLING_STATION_AND_SERVICE_STATION
				.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.PETROL_PUMP_ONLY_FILLING_STATION.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.CNG_MOTHER_STATION.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.FARM_HOUSE.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.COUNTRY_HOMES.equals(occupancyTypeHelper.getSubtype().getCode()))
			maxPermissibleHeight = new BigDecimal("7");
		else if (DxfFileConstants.SHOP_CUM_RESIDENTIAL.equals(occupancyTypeHelper.getSubtype().getCode()))
			maxPermissibleHeight = new BigDecimal("12");

		return maxPermissibleHeight;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
