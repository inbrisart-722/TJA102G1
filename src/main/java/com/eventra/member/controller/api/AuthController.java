package com.eventra.member.controller.api;

import com.eventra.member.dto.*;
import com.eventra.member.mapper.MemberMapper;
import com.eventra.member.model.MemberVO;
import com.eventra.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService service;

    public AuthController(MemberService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<MemberResponse> register(@RequestBody @Valid RegisterRequest req) {
        MemberVO m = service.register(req.getEmail(), req.getPassword(), req.getNickname(), req.getFullName());
        return ResponseEntity.ok(MemberMapper.toResponse(m));
    }

    @PostMapping("/login")
    public ResponseEntity<MemberResponse> login(@RequestBody @Valid LoginRequest req) {
        MemberVO m = service.loginWithEmailOrNickname(req.getIdentifier(), req.getPassword());
        return ResponseEntity.ok(MemberMapper.toResponse(m));
    }
}
