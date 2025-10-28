package com.example.IMS_Backend.repository;

import com.example.IMS_Backend.model.Category;
import com.example.IMS_Backend.dto.CategoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);

    // Add this method to get categories with product counts
    @Query("SELECT new com.example.IMS_Backend.dto.CategoryDTO(c.id, c.name, c.description, COUNT(p.id)) " +
            "FROM Category c LEFT JOIN c.products p " +
            "GROUP BY c.id, c.name, c.description " +
            "ORDER BY c.name")
    List<CategoryDTO> findAllWithProductCount();

    // Optional: If you need counts for a single category
    @Query("SELECT new com.example.IMS_Backend.dto.CategoryDTO(c.id, c.name, c.description, COUNT(p.id)) " +
            "FROM Category c LEFT JOIN c.products p " +
            "WHERE c.id = :categoryId " +
            "GROUP BY c.id, c.name, c.description")
    Optional<CategoryDTO> findByIdWithProductCount(Long categoryId);
}