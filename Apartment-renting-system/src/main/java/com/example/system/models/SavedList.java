package com.example.system.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "saved_list")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Ad ad;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User rentee;
}
