package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.exhibition.model.ExhibitionService;
import com.eventra.exhibitor.model.ExhibitorRepository;
import com.eventra.exhibitor.model.ExhibitorService;
import com.eventra.member.model.MemberService;
import com.eventra.platform_announcement.model.PlatformAnnouncementService;
import com.eventra.platform_announcement.model.PlatformAnnouncementVO;

@Controller
@RequestMapping("/")
public class PlatformIndexController {

	@Autowired
	PlatformAnnouncementService annSvc;

	@Autowired
	private ExhibitorService exhibitorSvc;
	
	@Autowired
	private MemberService memberSvc;
	
	@Autowired
	private ExhibitionService exhibitionSvc;

	@GetMapping("/platform/login")
	public String login() {
		return "platform/login";
	}

	@GetMapping("/platform/comment")
	public String comment() {
		return "platform/comment";
	}

	// 首頁
	@GetMapping("/platform/index")
	public String index(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String keyword) {

		// === 分頁 + 關鍵字搜尋 ===
		PageRequest pageable = PageRequest.of(page, 5, Sort.by("createdAt").descending());
		Page<PlatformAnnouncementVO> annPage = annSvc.search(keyword, pageable);

		// === 公告內容轉純文字，避免html標籤影響表格 ===
		for (PlatformAnnouncementVO ann : annPage.getContent()) {
			if (ann.getContent() != null) {
				String plain = ann.getContent().replaceAll("<[^>]*>", "").trim();
				if (plain.length() > 50) {
					plain = plain.substring(0, 50) + "...";
				}
				ann.setContent(plain);
			}
		}

		model.addAttribute("annListData", annPage.getContent());
		model.addAttribute("annPage", annPage);
		model.addAttribute("keyword", keyword); // 保留搜尋欄的輸入值

		// === 帶入平台統計數據 ===
		model.addAttribute("pendingExhibitorCount", exhibitorSvc.countByStatusId(1)); // 只統計狀態是1待核准的
		model.addAttribute("memberCount", memberSvc.countAll());
		model.addAttribute("exhibitorCount", exhibitorSvc.countAll());
		model.addAttribute("exhibitionCount", exhibitionSvc.countAll());

		return "platform/index";
	}

	@GetMapping("/platform/platform_edit")
	public String goAddForm(Model model) {
		model.addAttribute("annVO", new PlatformAnnouncementVO());
		return "platform/platform_edit";
	}

	@GetMapping("/platform/member")
	public String member() {
		return "platform/member";
	}

	@GetMapping("/platform/partner")
	public String partner() {
		return "platform/partner";
	}

	@GetMapping("/platform/report")
	public String report() {
		return "platform/report";
	}

	@GetMapping("/platform/exhibitor")
	public String exhibitor() {
		return "platform/exhibitor";
	}

	@GetMapping("/platform/exhibition")
	public String exhibition() {
		return "platform/exhibition";
	}

}
