package com.eventra.exhibitor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExhibitorService {

	@Autowired
    private ExhibitorRepository exhibitorRepo;

    public ExhibitorVO getExhibitorById(Integer id) {
        return exhibitorRepo.findById(id).orElse(null);
    }
}
