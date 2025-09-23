package com.eventra.member.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventra.member.model.MemberService;
import com.eventra.member.verif.model.RegisterReqDTO;
import com.eventra.member.verif.model.ResetPasswordReqDTO;
import com.eventra.member.verif.model.UpdateInfoReqDTO;

@RestController
@RequestMapping("/api/front-end")
public class MemberRestController {
	
	private final MemberService MEMBER_SERVICE;
	
	public MemberRestController(MemberService memberService) {
		this.MEMBER_SERVICE = memberService;
	}
	
	@GetMapping("/protected/member/getMyMemberId")
	public ResponseEntity<Integer> getMyMemberId(@AuthenticationPrincipal Principal principal, Authentication auth){
		Integer memberId = auth != null ? Integer.valueOf(auth.getName()) : null;
//		System.out.println("=========");
//		System.out.println(principal != null ? principal.getName() : null);
//		System.out.println(auth != null ? auth.getName() : null);
//		System.out.println("=========");
		return ResponseEntity.ok(memberId);
	}
	
	@GetMapping("/protected/member/load-photo")
	public ResponseEntity<String> loadPhoto(){
		
		return ResponseEntity.ok("load-photo success");
	}
	
	@PostMapping("/protected/member/update-photo")
	// file + JSON DTO 等混著上傳，才需要 @RequestPart
	public ResponseEntity<List<String>> updatePhoto(@RequestParam("user_photo") MultipartFile photo){
		
		UUID uuid = UUID.randomUUID();
		String fileName = uuid.toString() + ".png";
		// 存到本地
		System.out.println(photo);
		System.out.println("==================");
		
		String basePath = System.getProperty("user.dir");  
		
		// 專案根目錄/src/main/resources/static/front-end/img/testing_user_profile_pic/
		Path path = Paths.get(basePath, "src/main/resources/static/front-end/img/testing_user_profile_pic", fileName);
		System.out.println(path);
		
		try {
			// 確保資料夾存在
			Files.createDirectories(path.getParent());
			Files.write(path, photo.getBytes());
		}
		catch(IOException e) {
			System.out.println(e.toString());
			return ResponseEntity.status(200).body(List.of("failure"));
		}
		
		String webPath = "img/testing_user_profile_pic/" + fileName;
		return ResponseEntity.status(200).body(List.of("success", webPath));
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
		String email = MEMBER_SERVICE.register(req);
		if(email != null) System.out.println(email + ": 成功註冊");
		return ResponseEntity.status(200).body(email);
	}
	
	@PostMapping("/member/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordReqDTO req){
		String email = MEMBER_SERVICE.resetPassword(req);
		if(email != null) System.out.println(email + ": 成功重設密碼");
		return ResponseEntity.status(200).body(email);
	}
	
//	@PostMapping("member/login")
//	public ResponseEntity<String> login
	
}
