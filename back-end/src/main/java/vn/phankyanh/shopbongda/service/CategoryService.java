package vn.phankyanh.shopbongda.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import vn.phankyanh.shopbongda.domain.Category;
import vn.phankyanh.shopbongda.dto.request.CategoryRequest;
import vn.phankyanh.shopbongda.dto.response.CategoryResponse;
import vn.phankyanh.shopbongda.mapper.CategoryMapper;
import vn.phankyanh.shopbongda.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> handleGetAllCategory(){
        return this.categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
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
