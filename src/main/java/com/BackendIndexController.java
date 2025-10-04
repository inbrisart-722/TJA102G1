package com;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventra.exhibition.DraftChecks;
import com.eventra.exhibition.PublishChecks;
import com.eventra.exhibition.model.ExhibitionMapper;
import com.eventra.exhibition.model.ExhibitionService;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibitor.backend.controller.dto.ExhibitionCreateDTO;
import com.eventra.exhibitor.backend.controller.dto.ExhibitorAccountUpdateDTO;
import com.eventra.exhibitor.backend.controller.dto.ExhibitorInfoUpdateDTO;
import com.eventra.exhibitor.model.ExhibitorRepository;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.order.model.OrderRepository;
import com.eventra.order.model.OrderSummaryDTO;
import com.eventra.platform_announcement.model.PlatformAnnouncementRepository;
import com.eventra.platform_announcement.model.PlatformAnnouncementVO;
import com.eventra.tickettype.model.TicketTypeRepository;
import com.eventra.tickettype.model.TicketTypeVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/back-end")
public class BackendIndexController {
	
	private static final Integer DRAFT_STATUS_ID = 6;
	private static final Integer PENDING_STATUS_ID  = 1;
	private static final Integer REJECTED_STATUS_ID = 2;
	private final ExhibitionService exhibitionService;
//	private final Integer TEST_EXHIBITOR = 2;
	private final TicketTypeRepository ticketTypeRepository;
	private final ExhibitorRepository exhibitorRepository;
	private final PlatformAnnouncementRepository platformAnnouncementRepository;
	private final OrderRepository orderRepository;

	public BackendIndexController(ExhibitionService exhibitionService, 
								  TicketTypeRepository ticketTypeRepository,
								  ExhibitorRepository exhibitorRepository,
								  PlatformAnnouncementRepository platformAnnouncementRepository,
								  OrderRepository orderRepository) {
		this.exhibitionService = exhibitionService;
		this.ticketTypeRepository = ticketTypeRepository;
		this.exhibitorRepository = exhibitorRepository;
		this.platformAnnouncementRepository = platformAnnouncementRepository;
		this.orderRepository = orderRepository;
	}
	
	private Integer currentExhibitorId(String businessIdNumber) {
	    return exhibitorRepository.findByBusinessIdNumber(businessIdNumber)
	        .map(ExhibitorVO::getExhibitorId)
	        .orElseThrow(() -> new IllegalArgumentException("Exhibitor not found: " + businessIdNumber));
	}
	
    @GetMapping("exhibitor/back_end_homepage")
    public String exhibitorBackendPage(
    								   @AuthenticationPrincipal(expression = "username") String businessIdNumber,
    								   Model model,
    								   @RequestParam(defaultValue = "0") int page,
    								   @RequestParam(defaultValue = "10") int size,
    								   @RequestParam(required = false) String q){
    	
    	// 公告列表
    	PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    	
    	Page<PlatformAnnouncementVO> p =
    			(q == null || q.isBlank())
    				? platformAnnouncementRepository.findAll(pr)
    				: platformAnnouncementRepository
    					.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, pr);
    	
    	// 取得每日新訂單數量及總額
    	Integer exhibitorId = currentExhibitorId(businessIdNumber);
    	
    	var zone = java.time.ZoneId.of("Asia/Taipei");
    	var today = java.time.LocalDate.now(zone);
    	var start = Timestamp.valueOf(today.atStartOfDay());
    	var end = Timestamp.valueOf(today.plusDays(1).atStartOfDay());
    	
    	long newOrdersToday = orderRepository.countNewOrdersToday(exhibitorId, start, end);
    	long newOrdersAmountToday = orderRepository.sumNewOrdersAmountToday(exhibitorId, start, end);
    	
    	model.addAttribute("announcements", p.getContent());
    	model.addAttribute("currentPage", page);
    	model.addAttribute("totalPages", p.getTotalPages());
    	model.addAttribute("totalElements", p.getTotalElements());
    	model.addAttribute("q", q == null ? "" : q);
    	model.addAttribute("newOrdersToday", newOrdersToday);
    	model.addAttribute("newOrdersAmountToday", newOrdersAmountToday);
    	
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
    	
    	// 取全部票種，建立「可啟用/停用」的列資料
    	List<TicketTypeVO> allTypes = ticketTypeRepository.findAll();

        List<Map<String, Object>> ticketList = allTypes.stream()
            .map(t -> {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("name", t.getTicketTypeName());
                m.put("price", null);
                m.put("enabled", true); // 建立頁預設開啟；想預設全關就改成 false
                return m;
            })
            .collect(java.util.stream.Collectors.toList());

        String ticketListJson = "[]";
        try {
            ticketListJson = new ObjectMapper().writeValueAsString(ticketList);
        } catch (Exception ignore) {}

        model.addAttribute("ticketList", ticketList);
        model.addAttribute("ticketListJson", ticketListJson);
        return "back-end/create_exhibition";
    }
    
    /**
     * 接收用戶新增資料請求
     */
    /* ========================= 新增（發佈） ========================= */
    @PostMapping(value = "exhibitor/insert_exhibition", params = "action=publish")
    public String insertExhibitionPublish(
            @AuthenticationPrincipal(expression = "username") String businessIdNumber,
            @Validated(PublishChecks.class) @ModelAttribute("exhibitionCreateDTO") ExhibitionCreateDTO dto,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {

        if (br.hasErrors()) {
            model.addAttribute("ticketListJson", dto.getTicketJson() != null ? dto.getTicketJson() : "[]");
            return "back-end/create_exhibition";
        }

        Integer exhibitorId = currentExhibitorId(businessIdNumber);
        exhibitionService.addExhibition(dto, exhibitorId, false);

        ra.addFlashAttribute("msg", "建立成功！");
        return "redirect:/back-end/exhibitor/exhibition_list";
    }
    
    /* ========================= 新增（草稿） ========================= */
    @PostMapping(value = "exhibitor/insert_exhibition", params = "action=draft")
    public String insertExhibitionDraft(
            @AuthenticationPrincipal(expression = "username") String businessIdNumber,
            @Validated(DraftChecks.class) @ModelAttribute("exhibitionCreateDTO") ExhibitionCreateDTO dto,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {

        // 就算有錯也照存草稿；如需在同頁顯示錯誤，可把錯誤塞進 model，但不 return 表單
        Integer exhibitorId = currentExhibitorId(businessIdNumber);
        exhibitionService.addExhibition(dto, exhibitorId, true);

        ra.addFlashAttribute("msg", "草稿已儲存");
        return "redirect:/back-end/exhibitor/exhibition_list";
    }
    
    /**
     * 進入編輯模式
     */
    @GetMapping("exhibitor/edit/{id}")
    public String editExhibition(@PathVariable Integer id, Model model) {
    	ExhibitionVO exhibition = exhibitionService.findById(id);
    	ExhibitionCreateDTO dto = ExhibitionMapper.toDTO(exhibition);
    	
    	// 已設定的票種：名稱 -> 價格
        java.util.Map<String, Integer> chosen =
            (dto.getExhibitionTicketTypes() == null) ? java.util.Collections.emptyMap()
            : dto.getExhibitionTicketTypes().stream()
                .collect(java.util.stream.Collectors.toMap(
                    t -> t.getTicketType().getTicketTypeName(),
                    t -> t.getPrice()
                ));

        // 全部票種（每列都存在；有設定的 enabled=true 帶原價，未設定的 enabled=false）
        List<TicketTypeVO> allTypes = ticketTypeRepository.findAll();

        List<Map<String, Object>> ticketList = allTypes.stream()
            .map(t -> {
                String name = t.getTicketTypeName();
                boolean enabled = chosen.containsKey(name);
                Integer price = enabled ? chosen.get(name) : null;

                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("name", name);
                m.put("price", price);
                m.put("enabled", enabled);
                return m;
            })
            .collect(java.util.stream.Collectors.toList());

        String ticketListJson = "[]";
        try {
            ticketListJson = new ObjectMapper().writeValueAsString(ticketList);
        } catch (Exception ignore) {}

        model.addAttribute("ticketList", ticketList);
        model.addAttribute("ticketListJson", ticketListJson);
        model.addAttribute("exhibitionCreateDTO", dto);
        return "back-end/create_exhibition"; 
    }
    
    /* ========================= 更新（發佈） ========================= */
    @PostMapping(value = "exhibitor/update_exhibition", params = "action=publish")
    public String updateExhibitionPublish(
            @AuthenticationPrincipal(expression = "username") String businessIdNumber,
            @Validated(PublishChecks.class) @ModelAttribute("exhibitionCreateDTO") ExhibitionCreateDTO dto,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {

        if (br.hasErrors()) {
            model.addAttribute("ticketListJson", dto.getTicketJson() != null ? dto.getTicketJson() : "[]");
            return "back-end/create_exhibition";
        }

        Integer exhibitorId = currentExhibitorId(businessIdNumber);
        exhibitionService.updateExhibition(dto, exhibitorId, false);

        ra.addFlashAttribute("msg", "更新成功！");
        return "redirect:/back-end/exhibitor/exhibition_list";
    }
    
    /* ========================= 更新（草稿） ========================= */
    @PostMapping(value = "exhibitor/update_exhibition", params = "action=draft")
    public String updateExhibitionDraft(
            @AuthenticationPrincipal(expression = "username") String businessIdNumber,
            @Validated(DraftChecks.class) @ModelAttribute("exhibitionCreateDTO") ExhibitionCreateDTO dto,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {

        Integer exhibitorId = currentExhibitorId(businessIdNumber);
        exhibitionService.updateExhibition(dto, exhibitorId, true);

        ra.addFlashAttribute("msg", "草稿已更新");
        return "redirect:/back-end/exhibitor/exhibition_list";
    }
    
    @GetMapping("exhibitor/exhibition_list")
    public String goExhibitionListPage(
    								  @AuthenticationPrincipal(expression = "username") String businessIdNumber,
    								  Model model,
    								  @RequestParam(defaultValue = "0") int page,
    								  @RequestParam(defaultValue = "10") int size,	
    								  @RequestParam(defaultValue = "all") String tab,
    								  @RequestParam(required = false) String q) {

    	Integer exhibitorId = currentExhibitorId(businessIdNumber);
        Page<ExhibitionVO> p;

        switch (tab) {
	        case "pending"     -> p = exhibitionService.findByStatus(exhibitorId, PENDING_STATUS_ID,  page, size, q);
	        case "rejected"    -> p = exhibitionService.findByStatus(exhibitorId, REJECTED_STATUS_ID, page, size, q);
            case "not_on_sale" -> p = exhibitionService.findNotOnSale(exhibitorId, page, size, q);
            case "on_sale" -> p = exhibitionService.findOnSale(exhibitorId, page, size, q);
            case "ended" -> p = exhibitionService.findEnded(exhibitorId, page, size, q);
            case "draft" -> p = exhibitionService.findDrafts(exhibitorId, DRAFT_STATUS_ID, page, size, q); // 6=草稿
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
    public String orderListPage(
    							@AuthenticationPrincipal(expression = "username") String businessIdNumber,
    							Model model,
					    		@RequestParam(required = false) String status,
					    	    @RequestParam(required = false) String q,
    							@RequestParam(defaultValue = "0") int page,
    							@RequestParam(defaultValue = "10") int size) {
    	
    	Integer exhibitorId = currentExhibitorId(businessIdNumber);
    	// 將字串轉為 Enum；無效或空就給 null
        com.eventra.order.model.OrderStatus statusEnum = null;
        if(status != null && !status.isBlank()) {
        	try {
        		statusEnum = com.eventra.order.model.OrderStatus.valueOf(status);
        	}catch(IllegalArgumentException ignore) {
        		
        	}
        }
    	
    	Page<OrderSummaryDTO> p = orderRepository.findOrderSummaries(exhibitorId, statusEnum, q, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    	
    	
    	model.addAttribute("orders", p.getContent());
    	model.addAttribute("currentPage", page);
    	model.addAttribute("totalPage", p.getTotalPages());
    	model.addAttribute("totalElements", p.getTotalElements());
    	model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("status", status == null ? "" : status);
        model.addAttribute("size", size);
    	return "back-end/order_list";
    }
    
    @GetMapping("exhibitor/exhibitor_info")
    public String exhibitorInfoPage(@AuthenticationPrincipal(expression = "username") String businessIdNumber,Model model) {
    	Integer exhibitorId = currentExhibitorId(businessIdNumber);
        ExhibitorVO e = exhibitorRepository.findById(exhibitorId).orElse(null);

        // 準備表單物件
        ExhibitorInfoUpdateDTO dto = new ExhibitorInfoUpdateDTO();
        if (e != null) {
        	dto.setCompanyName(e.getCompanyName());
            dto.setExhibitorRegistrationName(e.getExhibitorRegistrationName());
            dto.setContactPhone(e.getContactPhone());
            dto.setEmail(e.getEmail());
            dto.setAbout(e.getAbout());
        }
        model.addAttribute("exhibitor", e);
        model.addAttribute("infoForm", dto);
        return "back-end/exhibitor_info";
    }
    
    @PostMapping("exhibitor/exhibitor_info/update")
    public String updateExhibitorInfo(
    		@AuthenticationPrincipal(expression = "username") String businessIdNumber,
            @Valid @ModelAttribute("infoForm") ExhibitorInfoUpdateDTO form,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {

    	Integer exhibitorId = currentExhibitorId(businessIdNumber);
    	
        if (br.hasErrors()) {
            ExhibitorVO e = exhibitorRepository.findById(exhibitorId).orElse(null);
            model.addAttribute("exhibitor", e);
            model.addAttribute("openModal", true);
            return "back-end/exhibitor_info";
        }

        ExhibitorVO e = exhibitorRepository.findById(exhibitorId)
                .orElseThrow(() -> new IllegalArgumentException("Exhibitor not found: " + exhibitorId));

        e.setCompanyName(form.getCompanyName());
        e.setExhibitorRegistrationName(form.getExhibitorRegistrationName());
        e.setContactPhone(form.getContactPhone());
        e.setEmail(form.getEmail());
        e.setAbout(form.getAbout());

        exhibitorRepository.save(e);

        ra.addFlashAttribute("msg", "更新成功！");
        return "redirect:/back-end/exhibitor/exhibitor_info";
    }
    
    @GetMapping("exhibitor/exhibitor_account_data")
    public String exhibitorAccountDataPage(@AuthenticationPrincipal(expression = "username") String businessIdNumber,Model model) {
    	Integer exhibitorId = currentExhibitorId(businessIdNumber);
        ExhibitorVO e = exhibitorRepository.findById(exhibitorId).orElse(null);
        
     // 建 DTO 當表單物件
        ExhibitorAccountUpdateDTO dto = new ExhibitorAccountUpdateDTO();
        if (e != null) {
            dto.setBusinessIdNumber(e.getBusinessIdNumber());
            dto.setEmail(e.getEmail());
            dto.setContactName(e.getContactName());
            dto.setContactPhone(e.getContactPhone());
            dto.setCompanyName(e.getCompanyName());
            dto.setExhibitorRegistrationName(e.getExhibitorRegistrationName());
            dto.setBankAccountName(e.getBankAccountName());
            dto.setBankCode(e.getBankCode());
            dto.setBankAccountNumber(e.getBankAccountNumber());
        }
        model.addAttribute("exhibitor", e);
        model.addAttribute("form", dto);
    	return "back-end/exhibitor_account_data";
    }
    
    @PostMapping("exhibitor/exhibitor_account_data/update")
    public String updateExhibitorAccountData(
    		@AuthenticationPrincipal(expression = "username") String businessIdNumber,
    		@Valid @org.springframework.web.bind.annotation.
    		ModelAttribute("form") ExhibitorAccountUpdateDTO form,
            org.springframework.validation.BindingResult br,
            Model model,
            RedirectAttributes ra) {
    	
    	Integer exhibitorId = currentExhibitorId(businessIdNumber);
    	
    	if (br.hasErrors()) {
            // 回填顯示資料 + 表單 + 讓 modal 自動打開
            ExhibitorVO e = exhibitorRepository.findById(exhibitorId).orElse(null);
            model.addAttribute("exhibitor", e);
            model.addAttribute("form", form);
            model.addAttribute("openModal", true);
            return "back-end/exhibitor_account_data";
        }

        ExhibitorVO e = exhibitorRepository.findById(exhibitorId)
                .orElseThrow(() -> new IllegalArgumentException("Exhibitor not found: "));

        // 依表單更新
        e.setBusinessIdNumber(form.getBusinessIdNumber());
        e.setEmail(form.getEmail());
        e.setContactName(form.getContactName());
        e.setContactPhone(form.getContactPhone());
        e.setCompanyName(form.getCompanyName());
        e.setExhibitorRegistrationName(form.getExhibitorRegistrationName());
        e.setBankAccountName(form.getBankAccountName());
        e.setBankCode(form.getBankCode());
        e.setBankAccountNumber(form.getBankAccountNumber());

        exhibitorRepository.save(e);

        ra.addFlashAttribute("msg", "更新成功！");
        return "redirect:/back-end/exhibitor/exhibitor_account_data";
    }
    
    @GetMapping("exhibitor/exhibitor_login")
    public String exhibitorLoginPage(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "logout", required = false) String logout,
        jakarta.servlet.http.HttpSession session,
        Model model
    ){
        if (error != null) {
            Object ex = session.getAttribute(org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION);
            String msg = "帳號或密碼錯誤";
            if (ex instanceof LockedException)            msg = "帳號已被鎖定";
            else if (ex instanceof DisabledException)     msg = "帳號已停用";
            else if (ex instanceof AccountExpiredException)    msg = "帳號已過期";
            else if (ex instanceof CredentialsExpiredException) msg = "密碼已過期，請重設";
            model.addAttribute("errorMsg", msg);
        }
        if (logout != null) model.addAttribute("infoMsg", "您已登出");
        return "back-end/exhibitor_login";
    }
    
}
