package com.eventra.exhibition.model;

import java.util.List;

public class ExhibitionSidebarResultDTO {
	private List<ExhibitionSidebarDTO> list;
	private Boolean hasNextPage;
	
	public List<ExhibitionSidebarDTO> getList() {
		return list;
	}
	public ExhibitionSidebarResultDTO setList(List<ExhibitionSidebarDTO> list) {
		this.list = list;
		return this;
	}
	public Boolean getHasNextPage() {
		return hasNextPage;
	}
	public ExhibitionSidebarResultDTO setHasNextPage(Boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
		return this;
	}
	
}
