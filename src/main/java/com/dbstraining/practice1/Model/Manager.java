package com.dbstraining.practice1.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="managers")
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;


    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;
}
