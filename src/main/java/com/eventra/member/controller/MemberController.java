package com.eventra.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.eventra.member.model.MemberVO;
import com.eventra.member.model.MemberService;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberSvc;

	/*
	 * 新增頁面
	 */
	@GetMapping("addMember")
	public String addMember(ModelMap model) {
		MemberVO memberVO = new MemberVO();
		model.addAttribute("memberVO", memberVO);
		return "back-end/member/addMember";
	}

	/*
	 * 新增提交（處理上傳大頭貼）
	 */
	@PostMapping("insert")
	public String insert(@Valid MemberVO memberVO, BindingResult result, ModelMap model,
			@RequestParam("profilePic") MultipartFile[] parts) throws IOException {

		// 去除 BindingResult 中 profilePic 欄位的 FieldError 紀錄（由我們自行處理檔案上傳）
		result = removeFieldError(memberVO, result, "profilePic");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
			model.addAttribute("errorMessage", "會員照片: 請上傳照片");
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes();
				memberVO.setProfilePic(buf);
			}
		}

		if (result.hasErrors() || parts[0].isEmpty()) {
			return "back-end/member/addMember";
		}

		// 2. 開始新增
		memberSvc.addMember(memberVO);

		// 3. 新增完成，重導至列表
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/member/listAllMember";
	}

	/*
	 * 取得單一會員，前往更新頁
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("memberId") String memberId, ModelMap model) {
		MemberVO memberVO = memberSvc.getOneMember(Integer.valueOf(memberId));
		model.addAttribute("memberVO", memberVO);
		return "back-end/member/update_member_input";
	}

	/*
	 * 更新提交（處理上傳大頭貼）
	 */
	@PostMapping("update")
	public String update(@Valid MemberVO memberVO, BindingResult result, ModelMap model,
			@RequestParam("profilePic") MultipartFile[] parts) throws IOException {

		// 去除 BindingResult 中 profilePic 欄位的 FieldError 紀錄
		result = removeFieldError(memberVO, result, "profilePic");

		if (parts[0].isEmpty()) { // 未上傳新圖片 → 取回舊圖
			byte[] pic = memberSvc.getOneMember(memberVO.getMemberId()).getProfilePic();
			memberVO.setProfilePic(pic);
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] pic = multipartFile.getBytes();
				memberVO.setProfilePic(pic);
			}
		}

		if (result.hasErrors()) {
			return "back-end/member/update_member_input";
		}

		// 2. 開始修改
		memberSvc.updateMember(memberVO);

		// 3. 修改完成，顯示單筆
		model.addAttribute("success", "- (修改成功)");
		memberVO = memberSvc.getOneMember(memberVO.getMemberId());
		model.addAttribute("memberVO", memberVO);
		return "back-end/member/listOneMember";
	}

	/*
	 * 複合查詢（select_page.html 提交）
	 */
	@PostMapping("listMembers_ByCompositeQuery")
	public String listMembers_ByCompositeQuery(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<MemberVO> list = memberSvc.getAll(map);
		model.addAttribute("memberListData", list);
		return "back-end/member/listAllMember";
	}

	/*
	 * 供畫面下拉使用的性別 Map 範例（可視需求調整/刪除） <form:select path="gender"
	 * items="${genderMapData}"/>
	 */
	@ModelAttribute("genderMapData")
	protected Map<String, String> genderMapData() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("M", "男");
		map.put("F", "女");
		map.put("N", "不透露");
		return map;
	}

	// 去除 BindingResult 中某個欄位的 FieldError 紀錄
	public BindingResult removeFieldError(MemberVO memberVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(err -> !err.getField().equals(removedFieldname)).collect(Collectors.toList());
		result = new BeanPropertyBindingResult(memberVO, "memberVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
}
