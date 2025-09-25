package com.eventra.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.map.dto.MapExploreDTO;
import com.eventra.map.model.MapExploreService;

@RestController
@RequestMapping("/api/exhibitions")
public class MapExploreApiController {
	
	@Autowired
    private MapExploreService service;
	
	/* 查詢附近展覽 */
    @GetMapping("/nearby")
    public List<MapExploreDTO> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radius) {
        return service.getNearby(lat, lng, radius);
    }

}
