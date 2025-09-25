package com.eventra.exhibition.model;

import com.eventra.exhibitor.backend.controller.dto.ExhibitionCreateDTO;

public class ExhibitionMapper {

	// DTO -> VO
	public static ExhibitionVO toVO(ExhibitionCreateDTO dto) {
		if(dto == null) {
			return null;
		}
		
		ExhibitionVO vo = new ExhibitionVO();
		vo.setExhibitionId(dto.getExhibitionId());
		vo.setExhibitionName(dto.getExhibitionName());
        vo.setStartTime(dto.getStartTime());
        vo.setEndTime(dto.getEndTime());
        vo.setLocation(dto.getLocation());
        vo.setTicketStartTime(dto.getTicketStartTime());
        vo.setTotalTicketQuantity(dto.getTotalTicketQuantity());
        vo.setDescription(dto.getDescription());
        vo.setExhibitionTicketTypes(dto.getExhibitionTicketTypes());
        return vo;
	}
	
	// VO -> DTO
	public static ExhibitionCreateDTO toDTO(ExhibitionVO vo) {
		if(vo == null) {
			return null;
		}
		
		ExhibitionCreateDTO dto = new ExhibitionCreateDTO();
		dto.setExhibitionId(vo.getExhibitionId());
        dto.setExhibitionName(vo.getExhibitionName());
        dto.setStartTime(vo.getStartTime());
        dto.setEndTime(vo.getEndTime());
        dto.setLocation(vo.getLocation());
        dto.setTicketStartTime(vo.getTicketStartTime());
        dto.setTotalTicketQuantity(vo.getTotalTicketQuantity());
        dto.setDescription(vo.getDescription());
        dto.setExhibitionTicketTypes(vo.getExhibitionTicketTypes());
        dto.setPhotoPortraitPath(vo.getPhotoPortrait());
        dto.setPhotoLandscapePath(vo.getPhotoLandscape());
        return dto;
	}
}
