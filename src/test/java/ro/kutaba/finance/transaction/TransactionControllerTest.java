package ro.kutaba.finance.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.kutaba.finance.config.JwtFilter;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private TransactionService transactionService;

    @Test
    void shouldReturnTransactionsPage() throws Exception {

        TransactionResponse response =
                new TransactionResponse(
                        1L,
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("150"),
                        LocalDate.of(2026,1,5),
                        TransactionType.EXPENSE,
                        "Food"
                );

        Page<TransactionResponse> page =
                new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0,10),
                        1
                );  

        when(transactionService.getAll(
                eq(0),
                eq(10),
                eq("date"),
                eq("desc"),
                any(TransactionFilter.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title")
                        .value("Lidl"))
                .andExpect(jsonPath("$.content[0].categoryName")
                        .value("Food"))
                .andExpect(jsonPath("$.content[0].amount")
                        .value(150));
    }
    
    @Test
    void shouldReturnEmptyTransactionsPage() throws Exception {

        Page<TransactionResponse> page =
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0,10),
                        0
                );

        when(transactionService.getAll(
                eq(0),  
                eq(10),
                eq("date"),
                eq("desc"),
                any(TransactionFilter.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

    }

    @Test
    void shouldCreateTransaction() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("150"),
                        LocalDate.of(2026,1,5),
                        TransactionType.EXPENSE,
                        1L
                );

        TransactionResponse response =
                new TransactionResponse(
                        1L,
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("150"),
                        LocalDate.of(2026,1,5),
                        TransactionType.EXPENSE,
                        "Food"
                );

        when(transactionService.create(any(CreateTransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Lidl"))
                .andExpect(jsonPath("$.categoryName").value("Food"))
                .andExpect(jsonPath("$.amount").value(150));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "",
                        "Weekly groceries",
                        new BigDecimal("150"),
                        LocalDate.of(2026,1,5),
                        TransactionType.EXPENSE,
                        1L
                );

        mockMvc.perform(post("/api/transactions")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never())
                .create(any());

    }

    @Test
    void shouldReturn400WhenAmountIsNegative() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("-150"),
                        LocalDate.of(2026,1,5),
                        TransactionType.EXPENSE,
                        1L
                );

        mockMvc.perform(post("/api/transactions")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never())
                .create(any());

    }

    @Test 
    void shouldReturn400WhenCategoryIsMissing() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("150"),
                        LocalDate.of(2026,1,5),
                        TransactionType.EXPENSE,
                        null
                );

        mockMvc.perform(post("/api/transactions")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never())
                .create(any());

    }

    @Test
    void shouldReturn400WhenDateIsMissing() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("150"),
                        null,
                        TransactionType.EXPENSE,
                        1L
                );

        mockMvc.perform(post("/api/transactions")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never())
                .create(any());

    }

    @Test
    void shouldReturn400WhenTypeIsMissing() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("150"),
                        LocalDate.of(2026,1,5),
                        null,
                        1L
                );

        mockMvc.perform(post("/api/transactions")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never())
                .create(any());

    }

    @Test
    void shouldUpdateTransaction() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("150"),
                        LocalDate.of(2026,1,5),
                        TransactionType.EXPENSE,
                        1L
                );

        TransactionResponse response =
                new TransactionResponse(
                        1L,
                        "Lidl",
                        "Weekly groceries",
                        new BigDecimal("150"),
                        LocalDate.of(2026,1,5),
                        TransactionType.EXPENSE,
                        "Food"
                );

        when(transactionService.update(eq(1L), any(CreateTransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/transactions/1")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Lidl"))
                .andExpect(jsonPath("$.categoryName").value("Food"))
                .andExpect(jsonPath("$.amount").value(150));
    }

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "",
                        "",
                        new BigDecimal("-50"),
                        null,
                        null,
                        null
                );

        mockMvc.perform(put("/api/transactions/1")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never())
                .update(any(), any());
    }

    @Test
    void shouldDeleteTransaction() throws Exception {

        doNothing()
                .when(transactionService)
                .delete(1L);

        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk());
            
        verify(transactionService)  
                .delete(1L);
    }

    @Test
    void shouldUseCustomPagination() throws Exception {

        // Arrange
        when(transactionService.getAll(
                eq(2),
                eq(5),
                eq("date"),
                eq("desc"),
                any(TransactionFilter.class)
        )).thenReturn(Page.empty());

        // Act
        mockMvc.perform(
                get("/api/transactions")
                        .param("page", "2")
                        .param("size", "5")
        )
        .andExpect(status().isOk());

        // Assert
        verify(transactionService)
                .getAll(
                        eq(2),
                        eq(5),
                        eq("date"),
                        eq("desc"),
                        any(TransactionFilter.class)
                );
        }

    @Test
    void shouldUseCustomSorting() throws Exception{

        //Arrange
        when(transactionService.getAll(
                eq(0),
                eq(0),
                eq("amount"),
                eq("asc"),
                any(TransactionFilter.class)
        )).thenReturn(Page.empty());

        //Act
        mockMvc.perform(
                get("/api/transactions")
                        .param("sortBy", "amount")
                        .param("sortDir", "asc")       
        );

        //Assert
        verify(transactionService)
                .getAll(eq(0),
                        eq(10),
                        eq("amount"),
                        eq("asc"), 
                        any(TransactionFilter.class)
                );
    }

    @Test
    void shouldPassFilterParameters() throws Exception {

        // Arrange
        when(transactionService.getAll(
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                any(TransactionFilter.class)
        )).thenReturn(Page.empty());

        ArgumentCaptor<TransactionFilter> filterCaptor =
                ArgumentCaptor.forClass(TransactionFilter.class);

        // Act
        mockMvc.perform(get("/api/transactions")
                        .param("type", "EXPENSE")
                        .param("categoryId", "5")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk());

        // Assert
        verify(transactionService).getAll(
                eq(0),
                eq(10),
                eq("date"),
                eq("desc"),
                filterCaptor.capture()
        );

        TransactionFilter filter = filterCaptor.getValue();

        assertEquals(TransactionType.EXPENSE, filter.type());
        assertEquals(5L, filter.categoryId());
        assertEquals(LocalDate.of(2026, 1, 1), filter.startDate());
        assertEquals(LocalDate.of(2026, 1, 31), filter.endDate());
        }
    
    @Test
    void shouldUseDefaultPaginationValues() throws Exception {

        // Arrange
        when(transactionService.getAll(
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                any(TransactionFilter.class)
        )).thenReturn(Page.empty());

        // Act
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk());

        // Assert
        verify(transactionService).getAll(
                eq(0),
                eq(10),
                eq("date"),
                eq("desc"),
                any(TransactionFilter.class)
        );
        }




}
