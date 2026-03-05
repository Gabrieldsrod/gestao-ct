package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.MemberRegistrationDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MemberUpdateResponseDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MemberResponseDTO;
import com.gabrieldsrod.gestao_ct.Service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberResponseDTO> registerMember(@RequestBody MemberRegistrationDTO data) {
        MemberResponseDTO newMember = memberService.register(data);

        return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
    }

    @GetMapping
    public ResponseEntity<Page<MemberResponseDTO>> listMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberResponseDTO> membersPageDto = memberService.pageMembers(pageable);
        return ResponseEntity.ok(membersPageDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<MemberResponseDTO>> listActiveMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberResponseDTO> activeMembers = memberService.pageActiveMembers(pageable);
        return ResponseEntity.ok(activeMembers);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MemberResponseDTO>> searchMembersByName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "name") String partialName) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(memberService.searchByPartialName(partialName, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberUpdateResponseDTO> updateMember(@PathVariable Long id, @RequestBody MemberRegistrationDTO data) {
        MemberUpdateResponseDTO response = memberService.updateMember(id, data);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<MemberUpdateResponseDTO> inactivateMember(@PathVariable Long id) {
        MemberUpdateResponseDTO response = memberService.inactivateMember(id);
        return ResponseEntity.ok().body(response);
    }

     @PatchMapping("/{id}/activate")
    public ResponseEntity<MemberUpdateResponseDTO> activateMember(@PathVariable Long id) {
        MemberUpdateResponseDTO response = memberService.activateMember(id);
     return ResponseEntity.ok().body(response);
    }
}
