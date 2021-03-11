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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.A.H.m;
import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.od.OdishaUtill;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.utils.StringUtils;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.DxfFileConstants.*;

@Service
public class Coverage extends FeatureProcess {
	// private static final String OCCUPANCY2 = "OCCUPANCY";

	private static final Logger LOG = Logger.getLogger(Coverage.class);

	// private static final String RULE_NAME_KEY = "coverage.rulename";
	private static final String RULE_DESCRIPTION_KEY = "coverage.description";
	private static final String RULE_EXPECTED_KEY = "coverage.expected";
	private static final String RULE_ACTUAL_KEY = "coverage.actual";
	// private static final BigDecimal ThirtyFive = BigDecimal.valueOf(35);
	private static final BigDecimal Forty = BigDecimal.valueOf(40);
	/*
	 * private static final BigDecimal FortyFive = BigDecimal.valueOf(45); private
	 * static final BigDecimal Sixty = BigDecimal.valueOf(60); private static final
	 * BigDecimal SixtyFive = BigDecimal.valueOf(65); private static final
	 * BigDecimal Seventy = BigDecimal.valueOf(70); private static final BigDecimal
	 * SeventyFive = BigDecimal.valueOf(75); private static final BigDecimal Eighty
	 * = BigDecimal.valueOf(80);
	 */
	public static final String RULE_38 = "38";
	private static final BigDecimal ROAD_WIDTH_TWELVE_POINTTWO = BigDecimal.valueOf(12.2);
	private static final BigDecimal ROAD_WIDTH_THIRTY_POINTFIVE = BigDecimal.valueOf(30.5);

	@Override
	public Plan validate(Plan pl) {
//        for (Block block : pl.getBlocks()) {
//            if (block.getCoverage().isEmpty()) {
//                pl.addError("coverageArea" + block.getNumber(), "Coverage Area for block " + block.getNumber() + " not Provided");
//            }
//        }
		return pl;
	}

	@Override
	public Plan process(Plan pl) {
		validate(pl);
		init(pl);
		BigDecimal totalCoverage = BigDecimal.ZERO;
		BigDecimal totalCoverageArea = BigDecimal.ZERO;

		totalCoverageArea = updatedAmmenityArea(pl);

		for (Block block : pl.getBlocks()) {
			BigDecimal coverageAreaWithoutDeduction = BigDecimal.ZERO;
			BigDecimal coverageDeductionArea = BigDecimal.ZERO;

			for (Measurement coverage : block.getCoverage()) {
				coverageAreaWithoutDeduction = coverageAreaWithoutDeduction.add(coverage.getArea());
			}
			for (Measurement deduct : block.getCoverageDeductions()) {
				coverageDeductionArea = coverageDeductionArea.add(deduct.getArea());
			}
			if (block.getBuilding() != null) {
				block.getBuilding().setCoverageArea(coverageAreaWithoutDeduction.subtract(coverageDeductionArea));
				BigDecimal coverage = BigDecimal.ZERO;
				if (pl.getPlot().getArea().doubleValue() > 0)
					coverage = block.getBuilding().getCoverageArea().multiply(BigDecimal.valueOf(100)).divide(
							pl.getPlanInformation().getPlotArea(), DcrConstants.DECIMALDIGITS_MEASUREMENTS,
							DcrConstants.ROUNDMODE_MEASUREMENTS);

				block.getBuilding().setCoverage(coverage);

				totalCoverageArea = totalCoverageArea.add(block.getBuilding().getCoverageArea());
				// totalCoverage =
				// totalCoverage.add(block.getBuilding().getCoverage());
			}

		}

		// pl.setCoverageArea(totalCoverageArea);
		// use plotBoundaryArea
		if (pl.getPlot() != null && pl.getPlot().getArea().doubleValue() > 0)
			totalCoverage = totalCoverageArea.multiply(BigDecimal.valueOf(100)).divide(
					pl.getPlanInformation().getPlotArea(), DcrConstants.DECIMALDIGITS_MEASUREMENTS,
					DcrConstants.ROUNDMODE_MEASUREMENTS);
		pl.setCoverage(totalCoverage);
		if (pl.getVirtualBuilding() != null) {
			pl.getVirtualBuilding().setTotalCoverageArea(totalCoverageArea);
		}

		BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();

		BigDecimal requiredCoverage = getPermissibleGroundCoverage(pl);
		// if(requiredCoverage.compareTo(BigDecimal.ZERO)>0)
		processCoverage(pl, StringUtils.EMPTY, totalCoverage, requiredCoverage);
		OdishaUtill.updateBlock(pl);
		return pl;
	}

	private BigDecimal updatedAmmenityArea(Plan pl) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Amenity in open space");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		
		BigDecimal totalArea = BigDecimal.ZERO;
		if (pl.getAmmenity().getGuardRooms().size() > 0) {
			BigDecimal guardRoomArea = pl.getAmmenity().getGuardRooms().stream().map(m -> m.getArea())
					.reduce(BigDecimal::add).get();
			if (guardRoomArea.compareTo(new BigDecimal("10")) > 0)
				totalArea = totalArea.add(guardRoomArea);
			addDetails(scrutinyDetail, "55-1-a", "Guard room", DxfFileConstants.NA,
					guardRoomArea.toString(), Result.Accepted.getResultVal());
		}
		if (pl.getAmmenity().getElectricCabins().size() > 0) {
			BigDecimal electricCabinArea = pl.getAmmenity().getElectricCabins().stream().map(m -> m.getArea())
					.reduce(BigDecimal::add).get();
			if (electricCabinArea.compareTo(new BigDecimal("10")) > 0)
				totalArea = totalArea.add(electricCabinArea);
			addDetails(scrutinyDetail, "55-1-a", "Electric cabin", DxfFileConstants.NA,
					electricCabinArea.toString(), Result.Accepted.getResultVal());
		}
		if (pl.getAmmenity().getSubStations().size() > 0) {
			BigDecimal subStationArea = pl.getAmmenity().getSubStations().stream().map(m -> m.getArea())
					.reduce(BigDecimal::add).get();
			if (subStationArea.compareTo(new BigDecimal("10")) > 0)
				totalArea = totalArea.add(subStationArea);
			addDetails(scrutinyDetail, "55-1-a", "Sub-Station", DxfFileConstants.NA,
					subStationArea.toString(), Result.Accepted.getResultVal());
		}
		if (pl.getAmmenity().getAreaForGeneratorSet().size() > 0) {
			BigDecimal AreaForGeneratorSetArea = pl.getAmmenity().getAreaForGeneratorSet().stream()
					.map(m -> m.getArea()).reduce(BigDecimal::add).get();
			if (AreaForGeneratorSetArea.compareTo(new BigDecimal("10")) > 0)
				totalArea = totalArea.add(AreaForGeneratorSetArea);
			addDetails(scrutinyDetail, "55-1-a", "Area for generator set", DxfFileConstants.NA,
					AreaForGeneratorSetArea.toString(), Result.Accepted.getResultVal());
		}
		if (pl.getAmmenity().getAtms().size() > 0) {
			BigDecimal atmArea = pl.getAmmenity().getAtms().stream().map(m -> m.getArea()).reduce(BigDecimal::add)
					.get();
			if (atmArea.compareTo(new BigDecimal("10")) > 0)
				totalArea = totalArea.add(atmArea);
			addDetails(scrutinyDetail, "55-1-a", "ATM", DxfFileConstants.NA,
					atmArea.toString(), Result.Accepted.getResultVal());
		}
		if (pl.getAmmenity().getOtherAmmenities().size() > 0) {
			BigDecimal otherAmmenitieArea = pl.getAmmenity().getOtherAmmenities().stream().map(m -> m.getArea())
					.reduce(BigDecimal::add).get();
			if (otherAmmenitieArea.compareTo(new BigDecimal("10")) > 0)
				totalArea = totalArea.add(otherAmmenitieArea);
			addDetails(scrutinyDetail, "55-1-a", "Other Ammenities", DxfFileConstants.NA,
					otherAmmenitieArea.toString(), Result.Accepted.getResultVal());
		}
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		return totalArea;
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
	
	private void processCoverage(Plan pl, String occupancy, BigDecimal coverage, BigDecimal upperLimit) {
		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Coverage");
		scrutinyDetail.setHeading("Coverage in Percentage");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		// scrutinyDetail.addColumnHeading(3, OCCUPANCY);
		scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);

		String desc = getLocaleMessage(RULE_DESCRIPTION_KEY, upperLimit.toString());
		String actualResult = getLocaleMessage(RULE_ACTUAL_KEY, coverage.toString());
		String expectedResult = getLocaleMessage(RULE_EXPECTED_KEY, upperLimit.toString());

		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, RULE_38);
		details.put(DESCRIPTION, "Coverage");

		details.put(PROVIDED, coverage.toString());
		if (upperLimit.compareTo(BigDecimal.ZERO) > 0) {
			details.put(PERMISSIBLE, upperLimit.toString());
			if (coverage.doubleValue() <= upperLimit.doubleValue()) {
				details.put(STATUS, Result.Accepted.getResultVal());
			} else {
				details.put(STATUS, Result.Not_Accepted.getResultVal());
			}

		} else {
			details.put(PERMISSIBLE, DxfFileConstants.NA);
			details.put(STATUS, Result.Accepted.getResultVal());
		}

		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

	}

	protected OccupancyType getMostRestrictiveCoverage(EnumSet<OccupancyType> distinctOccupancyTypes) {

		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_B1))
			return OccupancyType.OCCUPANCY_B1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_B2))
			return OccupancyType.OCCUPANCY_B2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_B3))
			return OccupancyType.OCCUPANCY_B3;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_D))
			return OccupancyType.OCCUPANCY_D;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_D1))
			return OccupancyType.OCCUPANCY_D1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_I2))
			return OccupancyType.OCCUPANCY_I2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_I1))
			return OccupancyType.OCCUPANCY_I1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_C))
			return OccupancyType.OCCUPANCY_C;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_A1))
			return OccupancyType.OCCUPANCY_A1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_A4))
			return OccupancyType.OCCUPANCY_A4;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_A2))
			return OccupancyType.OCCUPANCY_A2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_G1))
			return OccupancyType.OCCUPANCY_G1;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_E))
			return OccupancyType.OCCUPANCY_E;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_F))
			return OccupancyType.OCCUPANCY_F;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_F4))
			return OccupancyType.OCCUPANCY_F4;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_G2))
			return OccupancyType.OCCUPANCY_G2;
		if (distinctOccupancyTypes.contains(OccupancyType.OCCUPANCY_H))
			return OccupancyType.OCCUPANCY_H;

		else
			return null;
	}

	private BigDecimal getPermissibleGroundCoverage(Plan pl) {

		if (checkLowRiskBuildingCriteria(pl))
			return BigDecimal.ZERO;

		BigDecimal maxPermissibleGroundCoverage = BigDecimal.ZERO;
		switch (pl.getPlanInformation().getLandUseZone()) {
		case OPEN_SPACE_USE_ZONE:
			if (getPublicOpenSpace(pl).compareTo(new BigDecimal("40")) < 0)
				maxPermissibleGroundCoverage = new BigDecimal("30");
			break;
		case ENVIRONMENTALLY_SENSITIVE_ZONE:
			maxPermissibleGroundCoverage = new BigDecimal("40");
			break;
		}

		if (maxPermissibleGroundCoverage.compareTo(BigDecimal.ZERO) > 0)
			return maxPermissibleGroundCoverage;

		// General Criteria
		OccupancyTypeHelper occupancyTypeHelper = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
		System.out.println(occupancyTypeHelper);
		System.out.println(occupancyTypeHelper + " - " + occupancyTypeHelper.getSubtype());
		System.out.println(occupancyTypeHelper + " - " + occupancyTypeHelper.getSubtype() + " - "
				+ occupancyTypeHelper.getSubtype().getCode());
		if (DxfFileConstants.PETROL_PUMP_ONLY_FILLING_STATION.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.PETROL_PUMP_FILLING_STATION_AND_SERVICE_STATION
						.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.CNG_MOTHER_STATION.equals(occupancyTypeHelper.getSubtype().getCode()))
			maxPermissibleGroundCoverage = new BigDecimal("20");
		else if (DxfFileConstants.FARM_HOUSE.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.COUNTRY_HOMES.equals(occupancyTypeHelper.getSubtype().getCode()))
			maxPermissibleGroundCoverage = new BigDecimal("15");
		else
			maxPermissibleGroundCoverage = getGeneralCriteria(pl);

		return maxPermissibleGroundCoverage;
	}

	private BigDecimal getGeneralCriteria(Plan pl) {
		BigDecimal buildingHeight = OdishaUtill.getMaxBuildingHeight(pl);
		;
		BigDecimal maxPermissibleGroundCoverage = BigDecimal.ZERO;
		if (buildingHeight.compareTo(new BigDecimal("15")) < 0)
			maxPermissibleGroundCoverage = BigDecimal.ZERO;
		else if (buildingHeight.compareTo(new BigDecimal("15")) >= 0
				&& buildingHeight.compareTo(new BigDecimal("18")) < 0)
			maxPermissibleGroundCoverage = new BigDecimal("50");
		else if (buildingHeight.compareTo(new BigDecimal("18")) >= 0
				&& buildingHeight.compareTo(new BigDecimal("40")) <= 0)
			maxPermissibleGroundCoverage = new BigDecimal("40");
		else
			maxPermissibleGroundCoverage = new BigDecimal("40");
		return maxPermissibleGroundCoverage;
	}

	private BigDecimal getPublicOpenSpace(Plan pl) {
		BigDecimal totalPublicOpenSace = BigDecimal.ZERO;
		BigDecimal inPercentage = BigDecimal.ZERO;
		for (Block block : pl.getBlocks()) {
			for (Measurement measurement : block.getPlantationGreenStripes()) {
				totalPublicOpenSace = totalPublicOpenSace.add(measurement.getArea());
			}
		}
		inPercentage = totalPublicOpenSace.divide(pl.getPlot().getArea()).multiply(new BigDecimal("100"));
		return inPercentage;
	}

	private static void init(Plan pl) {
		OdishaUtill.updateDUnitInPlan(pl);
		OdishaUtill.updateAmmenity(pl);
	}

	private boolean checkLowRiskBuildingCriteria(Plan pl) {
		OccupancyTypeHelper occupancyTypeHelper = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
		boolean isLowRiskBuilding = false;
		if (DxfFileConstants.OC_RESIDENTIAL.equals(occupancyTypeHelper.getType().getCode())) {

			if (pl.getPlot().getArea().compareTo(new BigDecimal("500")) <= 0
					&& DxfFileConstants.YES.equalsIgnoreCase(pl.getPlanInformation().getApprovedLayoutDeclaration())) {
				if (!checkIsBeasment(pl) && OdishaUtill.getMaxBuildingHeight(pl).compareTo(new BigDecimal("10")) <= 0) {
					isLowRiskBuilding = true;
				}
			}
		}
		pl.getPlanInformation().setLowRiskBuilding(isLowRiskBuilding);

		if (isLowRiskBuilding) {
			pl.getPlanInformation().setRiskType(DxfFileConstants.LOW);
			pl.getPlanInformation().setRiskTypeDes(DxfFileConstants.LOW);
		} else {
			pl.getPlanInformation().setRiskType(DxfFileConstants.HIGH);
			pl.getPlanInformation().setRiskTypeDes(DxfFileConstants.OTHER_THAN_LOW);
		}

		return isLowRiskBuilding;
	}

	private boolean checkIsBeasment(Plan pl) {
		boolean flage = false;
		if (pl.getBlocks() != null) {
			for (Block b : pl.getBlocks()) {
				if (b.getBuilding() != null && b.getBuilding().getFloors() != null
						&& !b.getBuilding().getFloors().isEmpty()) {
					for (Floor f : b.getBuilding().getFloors()) {
						if (f.getNumber() == -1) {
							flage = true;
							break;
						}

					}
				}
				if (flage)
					break;
			}
		}
		return flage;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}
}
