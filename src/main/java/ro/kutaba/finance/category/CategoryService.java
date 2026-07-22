package ro.kutaba.finance.category;

import org.springframework.stereotype.Service;

import ro.kutaba.finance.exception.CategoryNotFoundException;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse create(CreateCategoryRequest request){

        Category category = new Category();
        category.setName(request.name());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(
            savedCategory.getId(),
            savedCategory.getName()
        );
    }

    public List<CategoryResponse> getAll() {

        return categoryRepository.findAll()
            .stream()
            .map(category -> new CategoryResponse(
                category.getId(),
                category.getName()
            ))
            .toList();
    }

    public CategoryResponse update(Long id, CreateCategoryRequest request){

        Category category = categoryRepository.findById(id)
                                .orElseThrow(() -> new CategoryNotFoundException());

        category.setName(request.name());

        Category updatedCategory = categoryRepository.save(category);

        return new CategoryResponse(
                updatedCategory.getId(),
                updatedCategory.getName()
        );
    }

    public void delete(Long id){

        Category category = categoryRepository.findById(id)
                                .orElseThrow(() -> new CategoryNotFoundException());

        categoryRepository.delete(category);
    }

}
