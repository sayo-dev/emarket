package org.example.e_market.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "categories")
public class Category extends AbstractEntity {

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parentCategory;

}
