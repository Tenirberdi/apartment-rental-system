package com.example.system.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "promotion_types")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private int ordered;
    @Column(nullable = false)
    private int price;

    @OneToMany(mappedBy = "promotionType", cascade = CascadeType.ALL)
    private List<Ad> ads;



}
