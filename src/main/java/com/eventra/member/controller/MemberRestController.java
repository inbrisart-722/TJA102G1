package com.eventra.member.controller;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventra.fileupload.FileCategory;
import com.eventra.fileupload.FileUploadService;
import com.eventra.member.model.GetMemberInfoResDTO;
import com.eventra.member.model.MemberService;
import com.eventra.member.model.PasswordDTO;
import com.eventra.member.verif.model.ForgotPasswordReqDTO;
import com.eventra.member.verif.model.RegisterReqDTO;
import com.eventra.member.verif.model.UpdateInfoReqDTO;

@RestController
@RequestMapping("/api/front-end")
public class MemberRestController {
	
	private final MemberService MEMBER_SERVICE;
	private final FileUploadService FILE_SERVICE;
	
	public MemberRestController(MemberService memberService, FileUploadService fileService) {
		this.MEMBER_SERVICE = memberService;
		this.FILE_SERVICE = fileService;
	}
	
	@GetMapping("/protected/member/getMyProfilePic")
	public ResponseEntity<String> getMyProfilePic(Principal principal){
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		String profilePic = MEMBER_SERVICE.getMyProfilePic(memberId);
		return ResponseEntity.ok(profilePic);
	}
	@GetMapping("/protected/member/getMyMemberId")
	public ResponseEntity<Integer> getMyMemberId(Principal principal){
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		return ResponseEntity.ok(memberId);
	}
	
	@GetMapping("/protected/member/getMemberInfo")
	public ResponseEntity<GetMemberInfoResDTO> getMemberInfo(Principal principal){
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		GetMemberInfoResDTO res = MEMBER_SERVICE.getMemberInfo(memberId);
		return ResponseEntity.ok(res);
	}
	
	@PostMapping("/protected/member/update-photo")
	// file + JSON DTO 等混著上傳，才需要 @RequestPart
	public ResponseEntity<List<String>> updatePhoto(@RequestParam("user_photo") MultipartFile photo, Authentication auth, @AuthenticationPrincipal UserDetails user, Principal principal){

		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null; 
		
		String src = null;
		
		try{ src = FILE_SERVICE.save(photo, FileCategory.profile_pic); }
		catch(IllegalArgumentException e) {
			// unsupported file type
			System.out.println(e.toString());
			return ResponseEntity.status(200).body(List.of(e.toString(), ""));
		}
		catch(RuntimeException e) {
			// any other io failure -> could not saved file
			System.out.println(e.toString());
			return ResponseEntity.status(200).body(List.of(e.toString(), ""));
		}
		
		try {MEMBER_SERVICE.updateMemberPhoto(src, memberId);}
		catch(NoSuchElementException e) { // 測試，repo findBy 可用此例外去接
			System.out.println(e.toString());
			return ResponseEntity.status(200).body(List.of(e.toString(), ""));
		}
		
		return ResponseEntity.status(200).body(List.of("success", src));
	}
	
	@PostMapping("/protected/member/update-info")
	public ResponseEntity<String> updateInfo(@ModelAttribute UpdateInfoReqDTO req, Principal principal){
		
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		
		System.out.println(req!=null? req.getFullName(): null);
		System.out.println(req!=null? req.getNickname(): null);
		System.out.println(req!=null? req.getGender(): null);
		System.out.println(req!=null? req.getPhoneNumber(): null);
		System.out.println(req!=null? req.getBirthDate(): null);
		System.out.println(req!=null? req.getAddress(): null);
		
		MEMBER_SERVICE.updateMemberInfo(req, memberId);
		
		return ResponseEntity.status(200).body("update-info success");
	}

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
	
	@PostMapping("/member/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordReqDTO req){
		String email = MEMBER_SERVICE.forgotPassword(req);
		if(email != null) System.out.println(email + ": 成功重設密碼");
		return ResponseEntity.status(200).body(email);
	}
	
	@PostMapping("/protected/member/check-if-password-correct")
	public ResponseEntity<Boolean> checkIfPasswordCorrect(@RequestBody PasswordDTO req, Principal principal){
		
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		String password = req != null ? req.getPassword() : null;
		
		Boolean res = MEMBER_SERVICE.checkIfPasswordCorrect(password, memberId);
		System.out.println("會員 " + memberId + " 舊密碼是否正確: " + res);
		
		return ResponseEntity.status(200).body(res);
	}
	
	@PostMapping("/protected/member/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody PasswordDTO req, Principal principal){
		
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		String password = req != null ? req.getPassword() : null;
		System.out.println("會員 " + memberId + " 送來新密碼: "+ password);
		MEMBER_SERVICE.resetPassword(password, memberId);
		
		return ResponseEntity.ok("SUCCESS");
	}
	
	
}
