package com.gabrieldsrod.gestao_ct.service;

import com.gabrieldsrod.gestao_ct.DTO.request.NewPlanDTO;
import com.gabrieldsrod.gestao_ct.DTO.request.PlanUpdateDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PlanResponseDTO;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanRepository;
import com.gabrieldsrod.gestao_ct.Service.PlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PlanService planService;

    @Test
    @DisplayName("Deve retornar todos os planos sem contagem de membros")
    void shouldGetAllPlansWithoutMemberCount() {
        // Arrange
        Plan plan1 = new Plan();
        plan1.setId(1L);
        plan1.setName("Plano Básico");
        plan1.setPrice(new BigDecimal("50.00"));
        plan1.setUpdatedAt(LocalDateTime.now());

        Plan plan2 = new Plan();
        plan2.setId(2L);
        plan2.setName("Plano Premium");
        plan2.setPrice(new BigDecimal("100.00"));
        plan2.setUpdatedAt(LocalDateTime.now());


        when(planRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(plan1, plan2));

        // Act
        List<PlanResponseDTO> result = planService.getAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Plano Básico", result.get(0).name());
        assertEquals("Plano Premium", result.get(1).name());
        assertEquals(0, result.get(0).activeMembers()); // Verifica se a contagem de membros não foi feita
        verify(planRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "id"));
        verify(memberRepository, never()).countActiveMembersByPlanId(anyLong());
    }

    @Test
    @DisplayName("Deve retornar todos os planos com contagem de membros ativos")
    void shouldGetAllPlansWithActiveMemberCount() {
        // Arrange
        Plan plan1 = new Plan();
        plan1.setId(1L);
        plan1.setName("Plano Básico");
        plan1.setPrice(new BigDecimal("50.00"));
        plan1.setUpdatedAt(LocalDateTime.now());

        Plan plan2 = new Plan();
        plan2.setId(2L);
        plan2.setName("Plano Premium");
        plan2.setPrice(new BigDecimal("100.00"));
        plan2.setUpdatedAt(LocalDateTime.now());


        when(planRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(plan1, plan2));
        when(memberRepository.countActiveMembersByPlanId(1L)).thenReturn(10L);
        when(memberRepository.countActiveMembersByPlanId(2L)).thenReturn(5L);

        // Act
        List<PlanResponseDTO> result = planService.getAllPlans();

        // Assert
        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).activeMembers());
        assertEquals(5L, result.get(1).activeMembers());
        verify(planRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "id"));
        verify(memberRepository, times(1)).countActiveMembersByPlanId(1L);
        verify(memberRepository, times(1)).countActiveMembersByPlanId(2L);
    }

    @Test
    @DisplayName("Deve retornar um plano pelo ID")
    void shouldGetPlanById() {
        // Arrange
        Long planId = 1L;
        Plan plan = new Plan();
        plan.setId(planId);
        plan.setName("Plano Teste");

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));

        // Act
        Plan result = planService.getById(planId);

        // Assert
        assertNotNull(result);
        assertEquals(planId, result.getId());
        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar plano com ID inexistente")
    void shouldThrowExceptionWhenGettingNonExistentPlanById() {
        // Arrange
        Long planId = 99L;
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> planService.getById(planId));

        assertEquals("Plano não encontrado com ID: " + planId, exception.getMessage());
        verify(planRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("Deve criar um novo plano com sucesso")
    void shouldCreatePlanSuccessfully() {
        // Arrange
        NewPlanDTO newPlanDTO = new NewPlanDTO("Novo Plano", new BigDecimal("75.00"));
        Plan savedPlan = new Plan();
        savedPlan.setId(3L);
        savedPlan.setName(newPlanDTO.name());
        savedPlan.setPrice(newPlanDTO.price());
        savedPlan.setCreatedAt(LocalDateTime.now());
        savedPlan.setUpdatedAt(LocalDateTime.now());


        when(planRepository.save(any(Plan.class))).thenReturn(savedPlan);

        // Act
        PlanResponseDTO result = planService.createPlan(newPlanDTO);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.id());
        assertEquals("Novo Plano", result.name());
        assertEquals(new BigDecimal("75.00"), result.price());
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    @DisplayName("Deve atualizar um plano existente com sucesso")
    void shouldUpdatePlanSuccessfully() {
        // Arrange
        Long planId = 1L;
        PlanUpdateDTO planUpdateDTO = new PlanUpdateDTO("Plano Atualizado", new BigDecimal("120.00"));
        Plan existingPlan = new Plan();
        existingPlan.setId(planId);
        existingPlan.setName("Plano Antigo");
        existingPlan.setPrice(new BigDecimal("100.00"));
        existingPlan.setUpdatedAt(LocalDateTime.now());

        when(planRepository.findById(planId)).thenReturn(Optional.of(existingPlan));
        when(planRepository.save(any(Plan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PlanResponseDTO result = planService.updatePlan(planId, planUpdateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Plano Atualizado", result.name());
        assertEquals(new BigDecimal("120.00"), result.price());
        verify(planRepository, times(1)).findById(planId);
        verify(planRepository, times(1)).save(existingPlan);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar plano inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentPlan() {
        // Arrange
        Long planId = 99L;
        PlanUpdateDTO planUpdateDTO = new PlanUpdateDTO("Plano Inexistente", new BigDecimal("99.00"));

        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> planService.updatePlan(planId, planUpdateDTO));

        assertEquals("Plano não encontrado com ID: " + planId, exception.getMessage());
        verify(planRepository, times(1)).findById(planId);
        verify(planRepository, never()).save(any());
    }
}
