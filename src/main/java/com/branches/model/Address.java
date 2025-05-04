package com.branches.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "endereco")
@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idendereco")
    private Long id;
    @Column(name = "rua")
    private String street;
    @Column(name = "bairro")
    private String district;
    @Column(name = "cidade")
    private String city;
    @Column(name = "uf", columnDefinition = "CHAR")
    private String state;
}
