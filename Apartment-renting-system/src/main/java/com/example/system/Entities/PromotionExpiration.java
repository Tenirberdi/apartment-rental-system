package com.example.system.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "promotion_expiration")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionExpiration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date expirationDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Ad ad;
}
