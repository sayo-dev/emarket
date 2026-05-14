package org.example.e_market.dto;

public interface CategoryTreeProjection {
    Long getId();
    String getName();
    Long getParentCategoryId();
    String getPath();
    Integer getLevel();
}
