package ro.kutaba.finance.dashboard;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import ro.kutaba.finance.config.JwtFilter;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private DashboardService dashboardService;


    @Test
    void shouldReturnDashboard() throws Exception {

        DashboardResponse response =
                new DashboardResponse(
                        new BigDecimal("5000"),
                        new BigDecimal("3200"),
                        new BigDecimal("1800"),
                        15,
                        List.of()
                );

        when(dashboardService.getDashboard(new DashboardFilter(null,null,null)))
                .thenReturn(response);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(5000))
                .andExpect(jsonPath("$.totalExpenses").value(3200))
                .andExpect(jsonPath("$.balance").value(1800))
                .andExpect(jsonPath("$.transactionCount").value(15));

        verify(dashboardService).getDashboard(new DashboardFilter(null,null,null));
    }

    @Test
    void shouldReturnCategorySummary() throws Exception {

        CategorySummaryResponse response =
                new CategorySummaryResponse(
                        "Food",
                        new BigDecimal("750"),
                        3L
                );

        when(dashboardService.getCategorySummary(new DashboardFilter(null,null,null)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/dashboard/category-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Food"))
                .andExpect(jsonPath("$[0].total").value(750));

        verify(dashboardService).getCategorySummary(new DashboardFilter(null,null,null));
    }

    @Test
    void shouldReturnMonthlySummary() throws Exception {

        MonthlySummaryResponse response =
                new MonthlySummaryResponse(
                        "2026-01",
                        new BigDecimal("5000"),
                        new BigDecimal("3200")
                );

        when(dashboardService.getMonthlySummary())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/dashboard/monthly-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").value("2026-01"))
                .andExpect(jsonPath("$[0].income").value(5000))
                .andExpect(jsonPath("$[0].expenses").value(3200));

        verify(dashboardService).getMonthlySummary();
    }


}