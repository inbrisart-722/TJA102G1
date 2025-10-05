package com.eventra.map.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventra.map.dto.MapExploreDTO;

@Service
public class MapExploreService {
	
	@Autowired
	private MapExploreRepository repository;

	/*
     * 取得附近展覽（Service 層負責將 Object[] → DTO）
     * */

	public List<MapExploreDTO> getNearby(double lat, double lng, double radiusKm) {
		List<Object[]> rows = repository.findNearbyExhibitionsNative();
		List<MapExploreDTO> result = new ArrayList<>();

	    for (Object[] r : rows) {
	    	Double longitude = r[3] != null ? ((Number) r[3]).doubleValue() : null;
	    	Double latitude  = r[4] != null ? ((Number) r[4]).doubleValue() : null;
	    	
	    	MapExploreDTO dto = new MapExploreDTO(
	    		    ((Number) r[0]).intValue(),       // exhibitionId
	    		    (String) r[1],                    // exhibitionName
	    		    (String) r[2],                    // location
	    		    ((Number) r[3]).doubleValue(),    // longitude
	    		    ((Number) r[4]).doubleValue(),    // latitude
	    		    (String) r[5],                    // photoPortrait
	    		    ((Timestamp) r[6]).toLocalDateTime(), // startTime
	    		    ((Timestamp) r[7]).toLocalDateTime(), // endTime
	    		    ((Number) r[9]).intValue(),       // ratingCount
	    		    ((Number) r[8]).doubleValue()     // averageRatingScore
	    		);

	        result.add(dto);
	    }
		return result;
	}

}
