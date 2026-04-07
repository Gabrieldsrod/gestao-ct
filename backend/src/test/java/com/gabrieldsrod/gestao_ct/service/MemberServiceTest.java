package com.gabrieldsrod.gestao_ct.service;

import com.gabrieldsrod.gestao_ct.DTO.request.MemberRegistrationDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MemberResponseDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MemberUpdateResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.BusinessRuleException;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import com.gabrieldsrod.gestao_ct.Service.MemberService;
import com.gabrieldsrod.gestao_ct.Service.PaymentService;
import com.gabrieldsrod.gestao_ct.Service.PlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlanService planService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("Deve buscar todos os alunos com paginação e sem filtro de status")
    void shouldGetAllMembersWithoutStatusFilter() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Member member = new Member();
        member.setId(1L);
        member.setName("João");
        member.setBirthDate(LocalDate.of(1995, 7, 10));
        Plan plan = new Plan();
        plan.setName("Plano Básico");
        plan.setUpdatedAt(LocalDateTime.now());
        member.setPlan(plan);
        member.setStatus(MemberStatus.ACTIVE);
        member.setRegistrationDate(LocalDate.now());
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        Page<Member> memberPage = new PageImpl<>(List.of(member));

        when(memberRepository.findAll(pageable)).thenReturn(memberPage);

        // Act
        Page<MemberResponseDTO> result = memberService.getAllMembers(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("João", result.getContent().getFirst().name());
        verify(memberRepository, times(1)).findAll(pageable);
        verify(memberRepository, never()).findByStatus(any(), any());
    }

    @Test
    @DisplayName("Deve buscar todos os alunos com filtro de status ativo")
    void shouldGetAllMembersWithActiveStatusFilter() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Member member = new Member();
        member.setId(1L);
        member.setName("Maria");
        member.setStatus(MemberStatus.ACTIVE);
        member.setBirthDate(LocalDate.of(1990, 5, 20));
        Plan plan = new Plan();
        plan.setName("Plano Básico");
        plan.setUpdatedAt(LocalDateTime.now());
        member.setPlan(plan);
        member.setRegistrationDate(LocalDate.now());
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        Page<Member> memberPage = new PageImpl<>(List.of(member));

        when(memberRepository.findByStatus(MemberStatus.ACTIVE, pageable)).thenReturn(memberPage);

        // Act
        Page<MemberResponseDTO> result = memberService.getAllMembers(MemberStatus.ACTIVE, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Maria", result.getContent().getFirst().name());
        verify(memberRepository, times(1)).findByStatus(MemberStatus.ACTIVE, pageable);
        verify(memberRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve registrar um novo aluno com sucesso e gerar cobrança")
    void shouldRegisterNewMemberAndGenerateCharge() {
        // Arrange
        MemberRegistrationDTO dto = new MemberRegistrationDTO();
        dto.setName("Carlos");
        dto.setEmail("carlos@teste.com");
        dto.setBirthDate(LocalDate.of(1985, 3, 15));
        dto.setPlanId(1L);

        Plan plan = new Plan();
        plan.setId(1L);
        plan.setName("Plano Básico");
        plan.setUpdatedAt(LocalDateTime.now());

        when(planService.getById(1L)).thenReturn(plan);
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member savedMember = invocation.getArgument(0);
            savedMember.setCreatedAt(LocalDateTime.now());
            savedMember.setUpdatedAt(LocalDateTime.now());
            savedMember.setId(10L);
            return savedMember;
        });
        when(paymentService.generateCharge(any(Member.class), any(LocalDate.class))).thenReturn(new MemberPayment());

        // Act
        MemberResponseDTO result = memberService.register(dto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("Carlos", result.name());
        verify(planService, times(1)).getById(1L);
        verify(memberRepository, times(1)).existsByEmail("carlos@teste.com");
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(paymentService, times(1)).generateCharge(any(Member.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar aluno com email já existente")
    void shouldThrowExceptionWhenRegisteringWithExistingEmail() {
        // Arrange
        MemberRegistrationDTO dto = new MemberRegistrationDTO();
        dto.setName("Carlos");
        dto.setEmail("carlos@teste.com");
        dto.setPlanId(1L);

        Plan plan = new Plan();
        plan.setId(1L);

        when(planService.getById(1L)).thenReturn(plan);
        when(memberRepository.existsByEmail("carlos@teste.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.register(dto));

        assertEquals("Já existe um aluno cadastrado com o email: carlos@teste.com", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
        verify(paymentService, never()).generateCharge(any(), any());
    }

    @Test
    @DisplayName("Deve inativar aluno com sucesso")
    void shouldInactivateMemberSuccessfully() {
        // Arrange
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setStatus(MemberStatus.ACTIVE);
        member.setDependents(Collections.emptyList());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        MemberUpdateResponseDTO result = memberService.inactivateMember(memberId);

        // Assert
        assertEquals(MemberStatus.INACTIVE, member.getStatus());
        assertNotNull(member.getInactivationDate());
        assertEquals("Aluno inativado com sucesso. Ele não receberá mais cobranças a partir de agora.", result.message());
        verify(paymentService, times(1)).cancelPendingCharges(member);
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    @DisplayName("Deve lançar exceção ao inativar titular com dependentes ativos")
    void shouldThrowExceptionWhenInactivatingHolderWithActiveDependents() {
        // Arrange
        Long memberId = 1L;
        Member holder = new Member();
        holder.setId(memberId);
        holder.setStatus(MemberStatus.ACTIVE);

        Member dependent = new Member();
        dependent.setId(2L);
        dependent.setStatus(MemberStatus.ACTIVE);
        
        holder.setDependents(List.of(dependent));

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(holder));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> memberService.inactivateMember(memberId));

        assertEquals("Não é possível inativar este titular. Ele possui dependentes ativos. Inative os dependentes primeiro ou promova-os a titulares mudando o plano.", exception.getMessage());
        verify(paymentService, never()).cancelPendingCharges(any());
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve reativar aluno com sucesso")
    void shouldActivateMemberSuccessfully() {
        // Arrange
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setStatus(MemberStatus.INACTIVE);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(paymentService.generateCharge(any(Member.class), any(LocalDate.class))).thenReturn(new MemberPayment()); // Simula geração de cobrança

        // Act
        MemberUpdateResponseDTO result = memberService.activateMember(memberId);

        // Assert
        assertEquals(MemberStatus.PENDING, member.getStatus()); // Porque uma cobrança foi gerada
        assertNull(member.getInactivationDate());
        assertTrue(result.message().contains("Uma nova cobrança foi gerada para o mês de retorno."));
        verify(memberRepository, times(1)).save(member);
    }
}
