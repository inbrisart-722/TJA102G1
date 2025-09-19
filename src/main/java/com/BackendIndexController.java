package com;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.exhibition.model.ExhibitionService;
import com.eventra.exhibition.model.ExhibitionServiceImpl;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibitor.backend.controller.dto.ExhibitionCreateDTO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/back-end")
public class BackendIndexController {
	
	private final ExhibitionService exhibitionService;
	private final Integer TEST_EXHIBITOR = 3;

	public BackendIndexController(ExhibitionService exhibitionService) {
		this.exhibitionService = exhibitionService;
	}
	
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
    
    /**
     * 接收用戶新增資料請求
     */
    @PostMapping("exhibitor/insert_exhibition")
    public String insertExhibition(ExhibitionCreateDTO dto, HttpSession session) {
    	/*********** 錯誤處理(建議能成功新增後再做)************/
    	
    	/*********** 新增 ***********/
    	// 取得登入展商id
    	// eg. Integer exhibitorId = session.getAttribute("loginId")
    	exhibitionService.addExhibition(dto, TEST_EXHIBITOR); // 呼叫service進行新增 
    	
    	return "redirect:/back-end/exhibitor/exhibition_list";
    }
    
    
    @GetMapping("exhibitor/exhibition_list")
    public String goExhibitionListPage(Model model,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
    	List<ExhibitionVO> exhibitions = exhibitionService.getAllExhibitions();
    	model.addAttribute("exhibitions", exhibitions);
    	
    	Page<ExhibitionVO> exhibitionPage = exhibitionService.getExhibitionsPage(page, size);
    	
    	model.addAttribute("exhibitions", exhibitionPage.getContent());
    	model.addAttribute("currentPage", page);
    	model.addAttribute("totalPages", exhibitionPage.getTotalPages());
    	model.addAttribute("totalElements", exhibitionPage.getTotalElements());
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
