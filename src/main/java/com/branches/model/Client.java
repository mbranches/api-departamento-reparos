package com.branches.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "cliente")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcliente")
    @EqualsAndHashCode.Include
    private Long id;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "fk_pessoa_cliente", referencedColumnName = "idpessoa")
    private Person person;
    private String email;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;
}
