package com;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionReviewPageDTO;
import com.eventra.exhibition.model.ExhibitionServiceImpl;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibitor.model.ExhibitorRepository;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.member.model.MemberRepository;
import com.eventra.member.model.MemberVO;

@Controller
@RequestMapping("/")
public class PlatformIndexController {

	private final MemberRepository MEMBER_REPO;
	private final ExhibitorRepository EXHIBITOR_REPO;
	private final ExhibitionRepository EXHIBITION_REPO;
	private final ExhibitionServiceImpl EXHIBITION_SVC;


	public PlatformIndexController(MemberRepository memberRepo,ExhibitorRepository exhibitorRepo,ExhibitionRepository exhibitionRepo, ExhibitionServiceImpl exhibitionSvc) {
		this.MEMBER_REPO = memberRepo;
		this.EXHIBITOR_REPO = exhibitorRepo;
		this.EXHIBITION_REPO = exhibitionRepo;
		this.EXHIBITION_SVC = exhibitionSvc;
	}
	
	@GetMapping("/platform/comment")
	public String commenT() {
		return "platform/comment";
	}

	@GetMapping("/platform/exhibition")
	public String exhibition(Model model) {
//		ExhibitionVO exhibition = EXHIBITION_REPO.findById(5).orElseThrow();
//		List<ExhibitionVO> exhibitionList = EXHIBITION_REPO.findAll();

//		model.addAttribute("EXHIBITION_REPO", exhibition);
		List<ExhibitionReviewPageDTO> exhibitionList = EXHIBITION_SVC.getExhibitionsForReviewPage();
		model.addAttribute("exhibitionList", exhibitionList);
		return "platform/exhibition";

	}

	@PostMapping("/platform/exhibition_detail")
	public String exhibitionDetail(@RequestParam("exhibitionId") Integer exhibitionId,Model model) {
		ExhibitionVO exhibition = EXHIBITION_REPO.findById(exhibitionId) .orElseThrow();
		model.addAttribute("exhibitionVO", exhibition);
		
		return "platform/exhibition_detail";
	}

	@GetMapping("/platform/exhibitor")
	public String exhibitor(Model model) {	
//		ExhibitorVO exhibitor = EXHIBITOR_REPO.findById(3).orElseThrow();
		List<ExhibitorVO> exhibitorList = EXHIBITOR_REPO.findAll();		
//		model.addAttribute("EXHIBITION_REPO",exhibitor);
		model.addAttribute("exhibitorList",exhibitorList);
		return "platform/exhibitor";

	}

	@PostMapping("/platform/exhibitor_detail")
	public String exhibitorDetail(@RequestParam("exhibitorId")Integer exhibitorId,Model model) {		
		ExhibitorVO exhibitor = EXHIBITOR_REPO.findById(exhibitorId).orElseThrow();
		model.addAttribute("exhibitorVO",exhibitor);
		return "platform/exhibitor_detail";
	}

	@GetMapping("/platform/index")
	public String indeX() {
		return "platform/index";
	}

	@GetMapping("/platform/login")
	public String login() {
		return "platform/login";
	}

	@GetMapping("/platform/member")
	public String member(Model model) {
//		MemberVO member = MEMBER_REPO.findById(5).orElseThrow();
		List<MemberVO> memberList = MEMBER_REPO.findAll();
//		model.addAttribute("memberVO", member);
		model.addAttribute("memberList", memberList);
		return "platform/member";
	}

	@PostMapping("/platform/member_edit")
	public String memberEdit(@RequestParam("memberId") Integer memberId, Model model) {
		MemberVO member = MEMBER_REPO.findById(memberId).orElseThrow();
		model.addAttribute("memberVO", member);
		return "platform/member_edit";
	}

	@GetMapping("/platform/partner")
	public String partner(Model model) {
//		ExhibitorVO exhibitor =EXHIBITOR_REPO.findById(2).orElseThrow();
		List<ExhibitorVO> exhibitorList = EXHIBITOR_REPO.findAll();
//		model.addAttribute("ExhibitionVO", exhibitor);
		model.addAttribute("exhibitorList",exhibitorList);
//		model.addAttribute("reviewStatusId",Map.of(1,"未審核", 2, "已審核"));
		return "platform/partner";
	}

	@PostMapping("/platform/partner_edit")
	public String partnerEdit(@RequestParam("exhibitorId") Integer exhibitorId,Model model) {
		ExhibitorVO exhibitor =EXHIBITOR_REPO.findById(exhibitorId).orElseThrow();
		model.addAttribute("ExhibitorVO", exhibitor);
		return "platform/partner_edit";
	}

	@GetMapping("/platform/platform_edit")
	public String platformEdit() {
		return "platform/platform_edit";
	}

	@GetMapping("/platform/report")
	public String report() {
		return "platform/report";
	}

}
