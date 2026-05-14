package org.example.e_market.repositories;

import org.example.e_market.entities.Category;
import org.example.e_market.dto.CategoryTreeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query(value = "WITH RECURSIVE CategoryTree AS (" +
            "    SELECT id, name, parent_category_id, name AS path, 1 AS level " +
            "    FROM categories " +
            "    WHERE parent_category_id IS NULL " +
            "    UNION ALL " +
            "    SELECT c.id, c.name, c.parent_category_id, ct.path || ' > ' || c.name, ct.level + 1 " +
            "    FROM categories c " +
            "    JOIN CategoryTree ct ON c.parent_category_id = ct.id " +
            ") " +
            "SELECT id, name, parent_category_id as parentCategoryId, path, level FROM CategoryTree", nativeQuery = true)
    List<CategoryTreeProjection> getCategoryTree();
}
