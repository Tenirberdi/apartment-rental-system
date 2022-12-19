package com.example.system.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true, length = 30)
    private String username;
    @Column(nullable = false)
    private String pass;
    @Column(name = "reset_password_token", columnDefinition = "varchar(30)")
    private String resetPasswordToken;
    @Column(name = "token_expiration_date")
    private Timestamp tokenExpirationDate;
    @Column( columnDefinition = "boolean default true")
    private boolean enabled;
    @Column(nullable = false)
    private Date registrationDate;
    @Column(nullable = false, unique = true, length = 70)
    private String email;
    private String contactInfo;
    private String photoName;

    @ManyToOne
    @JoinColumn
    private Role role;

//  ================================

    @OneToMany(mappedBy = "renter", cascade = CascadeType.ALL)
    private List<Ad> ads;

    @OneToMany(mappedBy = "rentee", cascade = CascadeType.ALL)
    private List<SavedList> savedLists;

    @OneToMany(mappedBy = "viewer", cascade = CascadeType.ALL)
    private List<Viewers> viewers;
}
