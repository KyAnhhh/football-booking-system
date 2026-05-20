package vn.phankyanh.shopbongda.product.service.impl;

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
import vn.phankyanh.shopbongda.product.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryResponse> getAllCategory(int pageNo, int pageSize, String sortBy, String sortDir, String search){
        Sort.Direction direction = sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        
        Page<Category> categoriesPage =
        ((search != null && !search.isEmpty())? categoryRepository.findByNameContaining(search, pageable) :
            categoryRepository.findAll(pageable));
        
        return categoriesPage.map(categoryMapper::toResponse);
    }

    @Override
    public CategoryResponse getCategoryById(Long id){
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepository.existsByName(request.getName())){
            throw new RuntimeException("Name already exists");
        }
        Category category = categoryMapper.toCategory(request);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    public void deleteCategory(Long id){
        if(!categoryRepository.existsById(id)){
            throw new RuntimeException("Not found with id: " + id);
        }
        this.categoryRepository.deleteById(id);

    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request){

        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
         categoryMapper.updateCategory(category, request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }
}
