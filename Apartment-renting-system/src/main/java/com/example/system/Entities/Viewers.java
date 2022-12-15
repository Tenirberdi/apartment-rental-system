package com.example.system.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "viewers")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Viewers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Ad ad;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User viewer;
}
