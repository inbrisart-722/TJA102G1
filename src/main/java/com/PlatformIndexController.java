package com;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionServiceImpl;
import com.eventra.exhibitor.model.ExhibitorRepository;
import com.eventra.exhibitor.model.ExhibitorReviewListPageDTO;
import com.eventra.exhibitor.model.ExhibitorService;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.member.model.MemberRepository;
import com.eventra.member.model.MemberVO;

@Controller
@RequestMapping("/")
public class PlatformIndexController {

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