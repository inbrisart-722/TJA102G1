package com;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eventra.exhibitor.backend.controller.dto.ExhibitionCreateDTO;

@Controller
@RequestMapping("/back-end")
public class BackendIndexController {

    @GetMapping("exhibitor/back_end_homepage")
    public String exhibitorBackendPage(){
        return "back-end/back_end_homepage";
    }
    
    /**
     * 進入建立展覽頁面
     */
    @GetMapping("exhibitor/create_exhibition")
    public String goCreateExhibitionPage(Model model) {
    	// 創建空容器包裝使用者輸入的資料
    	ExhibitionCreateDTO exhibitionCreateDTO = new ExhibitionCreateDTO();
    	model.addAttribute("exhibitionCreateDTO", exhibitionCreateDTO);
    	
    	return "/back-end/create_exhibition";
    }
    
    @GetMapping("exhibitor/exhibition_list")
    public String eventListPage() {
    	return "back-end/exhibition_list";
    }
    
    @GetMapping("exhibitor/order_list")
    public String orderListPage() {
//    	List<OrderVO> orderVOs = ORDER_REPOSITORY.findAll();
//    	model.addAttribute("orderVOs", orderVOs);
    	return "back-end/order_list";
    }
    
    @GetMapping("exhibitor/exhibitor_info")
    public String exhibitorInfoPage() {
    	return "back-end/exhibitor_info";
    }
    
    @GetMapping("exhibitor/exhibitor_account_data")
    public String exhibitorAccountDataPage() {
    	return "back-end/exhibitor_account_data";
    }
    
    @GetMapping("exhibitor/exhibitor_login")
    public String exhibitorLoginPage() {
    	return "back-end/exhibitor_login";
    }
    
}
