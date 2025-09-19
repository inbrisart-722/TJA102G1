package com.eventra.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventra.member.model.RegisterReqDTO;
import com.eventra.member.model.MemberService;
import com.eventra.member.model.UpdateInfoReqDTO;

@RestController
@RequestMapping("/api/front-end")
public class MemberRestController {
	
	private final MemberService MEMBER_SERVICE;
	
	public MemberRestController(MemberService memberService) {
		this.MEMBER_SERVICE = memberService;
	}
	
	@GetMapping("/protected/member/load-photo")
	public ResponseEntity<String> loadPhoto(){
		
		return ResponseEntity.ok("load-photo success");
	}
	
	@PostMapping("/protected/member/update-photo")
	public ResponseEntity<List<String>> updatePhoto(@RequestPart("user_photo") MultipartFile photo){
		
		return ResponseEntity.status(200).body(List.of("update-photo success", "123"));
	}
	
	@PostMapping("/protected/member/update-info")
	public ResponseEntity<String> updateInfo(
			@ModelAttribute UpdateInfoReqDTO req){
		
		MEMBER_SERVICE.updateMemberInfo(req);
		return ResponseEntity.status(200).body("update-info success");
	}
	
	// 可能性如下
	// 更改密碼
	// 更新資料（暱稱、手機號碼、生日、地址）
	// 更換照片
	// 第三方登入方式？

	@GetMapping("/member/check-if-member")
	public ResponseEntity<List<Boolean>> checkIfMember(@RequestParam("email") String email) {
		return ResponseEntity
				.status(200)
				.body(List.of(MEMBER_SERVICE.checkIfMember(email)));
	}
	
	@PostMapping("/member/register")
	public ResponseEntity<String> register(@RequestBody RegisterReqDTO req){
		String res = MEMBER_SERVICE.register(req);
		System.out.println(res + ": 成功註冊");
		return ResponseEntity.status(200).body(res);
	}
	
//	@PostMapping("member/login")
//	public ResponseEntity<String> login
	
}
