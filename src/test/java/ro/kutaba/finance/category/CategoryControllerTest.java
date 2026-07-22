package ro.kutaba.finance.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import ro.kutaba.finance.config.JwtFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private CategoryService categoryService;

    @Test
    void shouldReturnCategories() throws Exception {

        CategoryResponse response =
                new CategoryResponse(
                        1L,
                        "Food"
                );

        when(categoryService.getAll())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Food"));

        verify(categoryService).getAll();
    }

    @Test
    void shouldCreateCategory() throws Exception {

        CreateCategoryRequest request =
                new CreateCategoryRequest("Food");

        CategoryResponse response =
                new CategoryResponse(
                        1L,
                        "Food"
                );

        when(categoryService.create(any(CreateCategoryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"));

        verify(categoryService).create(any(CreateCategoryRequest.class));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {

        CreateCategoryRequest request =
                new CreateCategoryRequest("");

        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never())
                .create(any());
    }

    @Test
    void shouldReturn400WhenNameIsTooLong() throws Exception {

        CreateCategoryRequest request =
                new CreateCategoryRequest(
                        "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNO"
                );

        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never())
                .create(any());
    }

    @Test
    void shouldUpdateCategory() throws Exception {

        CreateCategoryRequest request =
                new CreateCategoryRequest("Groceries");

        CategoryResponse response =
                new CategoryResponse(
                        1L,
                        "Groceries"
                );

        when(categoryService.update(eq(1L), any(CreateCategoryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/categories/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Groceries"));

        verify(categoryService)
                .update(eq(1L), any(CreateCategoryRequest.class));
    }

    @Test
    void shouldReturn400WhenUpdateNameIsBlank() throws Exception {

        CreateCategoryRequest request =
                new CreateCategoryRequest("");

        mockMvc.perform(put("/api/categories/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never())
                .update(any(), any());
    }

    @Test
    void shouldDeleteCategory() throws Exception {

        doNothing()
                .when(categoryService)
                .delete(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isOk());

        verify(categoryService)
                .delete(1L);
    }

}