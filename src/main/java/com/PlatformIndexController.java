package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.platform_announcement.model.PlatformAnnouncementService;
import com.eventra.platform_announcement.model.PlatformAnnouncementVO;
import com.eventra.exhibition.model.ExhibitionService;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionServiceImpl;
import com.eventra.exhibitor.model.ExhibitorRepository;
import com.eventra.exhibitor.model.ExhibitorReviewListPageDTO;
import com.eventra.exhibitor.model.ExhibitorService;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.member.model.MemberService;
import com.eventra.member.model.MemberRepository;
import com.eventra.member.model.MemberVO;

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

	private final MemberRepository MEMBER_REPO;
	private final ExhibitorRepository EXHIBITOR_REPO;
	private final ExhibitorService EXHIBITOR_SVC;
	private final ExhibitionRepository EXHIBITION_REPO;
	private final ExhibitionServiceImpl EXHIBITION_SVC;

	public PlatformIndexController(MemberRepository memberRepo, ExhibitorRepository exhibitorRepo,
			ExhibitionRepository exhibitionRepo, ExhibitionServiceImpl exhibitionSvc, ExhibitorService exhibitorSvc) {
		this.MEMBER_REPO = memberRepo;
		this.EXHIBITOR_REPO = exhibitorRepo;
		this.EXHIBITION_REPO = exhibitionRepo;
		this.EXHIBITION_SVC = exhibitionSvc;
		this.EXHIBITOR_SVC = exhibitorSvc;
	}

	/* ========== 0th part: 登入 ========== */
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
    
	/* ========== 1st part: 展商列表 ========== */
	@GetMapping("/platform/exhibitor_list")
	public String exhibitorList() {
		return "platform/exhibitor_list";
	}

	/* ========== 2nd part: 展商審核列表 ========== */
	@GetMapping("/platform/exhibitor")
	public String exhibitor(@RequestParam(name = "page", required = false, defaultValue = "1") int page, Model model) {

		Page<ExhibitorReviewListPageDTO> exhibitorList = EXHIBITOR_SVC.findExhibitorsForReview(page - 1);
		model.addAttribute("exhibitorList", exhibitorList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", exhibitorList.getTotalPages());

		return "platform/exhibitor";
	}

	/* ========== 3rd part: 展商審核頁 ========== */
	@PostMapping("/platform/exhibitor_detail")
	public String exhibitorDetail(@RequestParam("exhibitorId") Integer exhibitorId, Model model) {
		ExhibitorVO exhibitor = EXHIBITOR_REPO.findById(exhibitorId).orElseThrow();
		model.addAttribute("exhibitor", exhibitor);
		return "platform/exhibitor_detail";
	}

	@GetMapping("/platform/exhibitor_review_success")
	public String exhibitorReviewSuccess(@RequestParam("token") String token, @RequestParam("domain") String domain) {

		return "platform/exhibitor_review_success";
	}

	@GetMapping("/platform/exhibitor_review_failure")
	public String exhibitorReviewFailure(@RequestParam("token") String token, @RequestParam("domain") String domain) {

		return "platform/exhibitor_review_failure";
	}

	/* ========== 4th part: 會員列表 ========== */
	@GetMapping("/platform/member")
	public String member(Model model) {
//		MemberVO member = MEMBER_REPO.findById(5).orElseThrow();
		List<MemberVO> memberList = MEMBER_REPO.findAll();
//		model.addAttribute("memberVO", member);
		model.addAttribute("memberList", memberList);
		return "platform/member";
	}

	/* ========== 5th part: 會員編輯 ========== */
	@PostMapping("/platform/member_edit")
	public String memberEdit(@RequestParam("memberId") Integer memberId, Model model) {
		MemberVO member = MEMBER_REPO.findById(memberId).orElseThrow();
		model.addAttribute("memberVO", member);
		return "platform/member_edit";
	}

}
