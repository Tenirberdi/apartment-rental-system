package com.example.system.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "house_types")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String type;

    @OneToMany(mappedBy = "houseType", cascade = CascadeType.ALL)
    private List<Ad> ads;
}
