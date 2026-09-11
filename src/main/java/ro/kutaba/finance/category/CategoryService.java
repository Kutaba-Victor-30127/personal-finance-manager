package ro.kutaba.finance.category;

import org.springframework.stereotype.Service;

import ro.kutaba.finance.exception.CategoryNotFoundException;

import ro.kutaba.finance.user.User;
import ro.kutaba.finance.security.CurrentUserService;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CurrentUserService currentUserService;
    

    public CategoryService(CategoryRepository categoryRepository, CurrentUserService currentUserService) {
        this.categoryRepository = categoryRepository;
        this.currentUserService = currentUserService;
    }

    public CategoryResponse create(CreateCategoryRequest request){

        User currentUser = currentUserService.getCurrentUser();

        Category category = new Category();

        category.setName(request.name());
        category.setUser(currentUser);

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(
            savedCategory.getId(),
            savedCategory.getName()
        );
    }

    public List<CategoryResponse> getAll() {

        User currentUser = currentUserService.getCurrentUser();

        return categoryRepository.findByUser(currentUser)
            .stream()
            .map(category -> new CategoryResponse(
                category.getId(),
                category.getName()
            ))
            .toList();
    }

    public CategoryResponse update(Long id, CreateCategoryRequest request){

        User currentUser = currentUserService.getCurrentUser();

        Category category = categoryRepository.findByIdAndUser(id, currentUser)
                                .orElseThrow(CategoryNotFoundException::new);

        category.setName(request.name());

        Category updatedCategory = categoryRepository.save(category);

        return new CategoryResponse(
                updatedCategory.getId(),
                updatedCategory.getName()
        );
    }

    public void delete(Long id){

        User currentUser = currentUserService.getCurrentUser();

        Category category = categoryRepository.findByIdAndUser(id, currentUser)
                                .orElseThrow(CategoryNotFoundException::new);

        categoryRepository.delete(category);
    }

}
