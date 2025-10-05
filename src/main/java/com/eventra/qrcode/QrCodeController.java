package com.eventra.qrcode;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/back-end/exhibitor")
public class QrCodeController {

	private final QrCodeService QR_SVC;
	
	public QrCodeController(QrCodeService qrSvc) {
		this.QR_SVC = qrSvc;
	}
	
	@GetMapping("/qrcode-validation")
	public String validateQrCode(@RequestParam(name = "ticketCode", required = false) String ticketCode, Principal principal, Model model) {

		// -1: 非展商身份根本進不來

		/* ========== 1st part: 開始錯誤處理 ========== */
//		EMPTY("缺少票券代碼 (ticketCode 欄位）"),
//		FORBIDDEN("您沒有權限驗證此 QR Code！"),
//		NOT_EXIST("此 QR Code 對應之票號不存在！"),
//		USED("此 QR Code 已經使用過囉！"),
//		VALID_BUT_NOT_START("票號存在且未使用過，但此展覽尚未開展"),
//		VALID_BUT_END("票號存在且未使用過，但此展覽已經結束");
		
		// 0: qr code 沒給或為空 -> return "back-end/qrcode_failure" 帶 message "缺少票券代碼 (ticketCode 欄位）";
		if (ticketCode == null || ticketCode.isBlank()) {
			model.addAttribute("message", QrCodeValidationResult.EMPTY.getMessage());
			return "back-end/qrcode_failure";
		}
		
		QrCodeValidationResponse response = QR_SVC.validateByTicketCode(ticketCode, principal.getName()); // getName() 丟統編
		QrCodeValidationResult result = response.getResult();
		QrCodeDTO dto = response.getData();
		
		boolean isFailure = false;
		// 1: ticketCode 是否不存在
		// rollback: 根本不存在 -> return "back-end/qrcode_failure" 帶 message "此 QR Code 對應之票號不存在！";
		if(result == QrCodeValidationResult.EMPTY) isFailure = true;
		
		// 2: ticketCode 是否已使用過
		// rollback: 已使用過 -> return "back-end/qrcode_failure" 帶 message "此 QR Code 已經使用過囉！";
		if(result == QrCodeValidationResult.USED) isFailure = true;
		
		// 3: 從 ticketCode 找 orderItem 找 order 找 exhibitor 找 businessIdNumber 核對 principal
		// rollback: 不可驗證 -> return "back-end/qrcode_forbidden" 帶 message "您沒有權限驗證此 QR Code 哦！"
		if(result == QrCodeValidationResult.FORBIDDEN) isFailure = true;
		
		// 4: 從 ticketCode 找 orderItem 找 order 找 exhibitionTicketType 找 exhibition 找 startTime
		// rollback: 尚未開展 -> return "back-end/qrcode_failure" 帶 message "票號存在且未使用過，但此展覽尚未開展"
		if(result == QrCodeValidationResult.VALID_BUT_NOT_START) isFailure = true;
		
		// 5: 從 ticketCode 找 orderItem 找 order 找 exhibitionTicketType 找 exhibition 找 endTime
		// rollback: 已經結束 -> return "back-end/qrcode_failure" 帶 message "票號存在且未使用過，但此展覽已經結束"
		if(result == QrCodeValidationResult.VALID_BUT_END) isFailure = true;
		
		// 一起處理
		if(isFailure == true) {
			model.addAttribute("message", result.getMessage());
			return "back-end/qrcode_failure";
		}
		/* ========== 2nd part: 以下為成功狀況 ========== */
		
		// 5: 以下皆為成功！
		
//		if(result == QrCodeValidationResult.SUCCESS)
		model.addAttribute("dto", dto);
		return "back-end/qrcode_success";
//		}
	}
	
	// 以下為設計時，測試進入 qrcode 頁面用
//	@GetMapping("/qrcode-success")
//	public String qrCodeSuccess() {
//		return "back-end/qrcode_success";
//	}
//	@GetMapping("/qrcode-failure")
//	public String qrCodeFailure() {
//		return "back-end/qrcode_failure";
//	}
//	@GetMapping("/qrcode-forbidden")
//	public String qrCodeForbidden() {
//		return "back-end/qrcode_forbidden";
//	}
}
