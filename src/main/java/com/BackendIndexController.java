package com;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.exhibition.model.ExhibitionMapper;
import com.eventra.exhibition.model.ExhibitionService;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibitor.backend.controller.dto.ExhibitionCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/back-end")
public class BackendIndexController {
	
	private static final Integer DRAFT_STATUS_ID = 6;
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
    	
    	// 新增這行：讓模板有可用的 JSON 字串（空陣列）
        model.addAttribute("ticketListJson", "[]");
    	return "back-end/create_exhibition";
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
    
    /**
     * 進入編輯模式
     */
    @GetMapping("exhibitor/edit/{id}")
    public String editExhibition(@PathVariable Integer id, Model model) {
    	ExhibitionVO exhibition = exhibitionService.findById(id);
    	ExhibitionCreateDTO dto = ExhibitionMapper.toDTO(exhibition);
    	
    	// 轉換票種 VO -> JS-friendly 結構
    	List<Map<String, Object>> ticketList = (dto.getExhibitionTicketTypes() == null) ? List.of()
    	        : dto.getExhibitionTicketTypes().stream()
                .map(t -> Map.<String, Object>of(
                    "name",  t.getTicketType().getTicketTypeName(),
                    "price", t.getPrice()
                ))
                .toList();
    	
    	// 這裡用 Jackson 轉字串
        String ticketListJson = "[]";
        try {
            ticketListJson = new ObjectMapper().writeValueAsString(ticketList);
        } catch (Exception e) {
            // 可視需要記錄 log
            // log.warn("Serialize ticketList failed", e);
        }
        
        model.addAttribute("ticketListJson", ticketListJson);
    	model.addAttribute("ticketList", ticketList);
    	model.addAttribute("exhibitionCreateDTO", dto);
    	return "back-end/create_exhibition";
    }
    
    @PostMapping("exhibitor/update_exhibition")
    public String updateExhibition(ExhibitionCreateDTO dto, HttpSession session) {
    	exhibitionService.updateExhibition(dto, TEST_EXHIBITOR);
    	return "redirect:/back-end/exhibitor/exhibition_list";
    }
    
    @GetMapping("exhibitor/exhibition_list")
    public String goExhibitionListPage(Model model,
    								  @RequestParam(defaultValue = "0") int page,
    								  @RequestParam(defaultValue = "10") int size,	
    								  @RequestParam(defaultValue = "all") String tab,
    								  @RequestParam(required = false) String q) {

    	Integer exhibitorId = TEST_EXHIBITOR; // 實務上改成登入者 id
        Page<ExhibitionVO> p;

        switch (tab) {
            case "draft" -> p = exhibitionService.findDrafts(exhibitorId, DRAFT_STATUS_ID, page, size, q); // 6=草稿
            case "not_on_sale" -> p = exhibitionService.findNotOnSale(exhibitorId, page, size, q);
            case "on_sale" -> p = exhibitionService.findOnSale(exhibitorId, page, size, q);
            case "ended" -> p = exhibitionService.findEnded(exhibitorId, page, size, q);
            default -> p = exhibitionService.findAll(exhibitorId, page, size, q);
        }
        model.addAttribute("tab", tab);
    	model.addAttribute("exhibitions", p.getContent());
    	model.addAttribute("currentPage", page);
    	model.addAttribute("totalPages", p.getTotalPages());
    	model.addAttribute("totalElements", p.getTotalElements());
    	model.addAttribute("q", q == null ? "" : q);
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
