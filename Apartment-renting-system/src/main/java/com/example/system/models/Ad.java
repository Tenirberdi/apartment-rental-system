package com.example.system.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "ads")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "varchar(300)")
    private String description;
    @Column(nullable = false)
    private int pricePerMonth;
    @Column(nullable = false)
    private int totalRoomAmount;
    @Column(nullable = false)
    private int bathRoomAmount;
    @Column(nullable = false)
    private int bedRoomAmount;
    @Column(nullable = false)
    private int kitchenRoomAmount;
    @Column(nullable = false)
    private int area;
    private int whichFloor;
    @Column(columnDefinition = "varchar(100)")
    private String furniture;
    @Column(nullable = false)
    private String location;
    @Builder.Default
    private Date dateOfPosting = new java.sql.Date(System.currentTimeMillis());
    @Column(columnDefinition = "boolean default true")
    @Builder.Default
    private boolean available = true;
    @Column(columnDefinition = "boolean default true")
    @Builder.Default
    private boolean enabled = true;

    @ManyToOne
    @JoinColumn()
    private PromotionType promotionType;
    @ManyToOne
    @JoinColumn(nullable = false)
    private HouseType houseType;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User renter;



    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<SavedList> savedLists;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<Viewers> viewers;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<Photo> photos;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<PromotionExpiration> promotionExpirations;


}
