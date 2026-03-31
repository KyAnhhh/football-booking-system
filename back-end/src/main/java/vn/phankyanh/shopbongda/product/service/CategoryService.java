package vn.phankyanh.shopbongda.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.domain.Category;
import vn.phankyanh.shopbongda.product.dto.request.CategoryRequest;
import vn.phankyanh.shopbongda.product.dto.response.CategoryResponse;
import vn.phankyanh.shopbongda.product.mapper.CategoryMapper;
import vn.phankyanh.shopbongda.product.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public Page<CategoryResponse> handleGetAllCategory(int pageNo, int pageSize, String sortBy, String sortDir, String search){
        Sort.Direction direction = sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        
        Page<Category> categoriesPage;
        if(search != null && !search.isEmpty()){
            categoriesPage = categoryRepository.findByNameContaining(search, pageable);
        } else {
            categoriesPage = categoryRepository.findAll(pageable);
        }
        return categoriesPage.map(categoryMapper::toResponse);
    }
    public CategoryResponse handleGetUserById(Long id){
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    public CategoryResponse handleCreateCategory(CategoryRequest request){
        if(categoryRepository.existsByName(request.getName())){
            throw new RuntimeException("Name already exists");
        }
        Category category = categoryMapper.toCategory(request);
         this.categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    public void handleDeleteCategory(Long id){
        if(!categoryRepository.existsById(id)){
            throw new RuntimeException("Not found with id: " + id);
        }
        this.categoryRepository.deleteById(id);

    }

    public Category handleUpdateCategory(Long id, CategoryRequest request){

        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
         categoryMapper.updateCategory(category, request);
        return categoryRepository.save(category);
    }
}
