package com.branches.model;

import jakarta.persistence.*;

@Entity(name = "cliente")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcliente")
    private Long id;
    @Column(name = "nome")
    private String name;
    @Column(name = "sobrenome")
    private String lastName;
    @OneToOne
    @JoinColumn(name = "fk_endereco_cliente", referencedColumnName = "idendereco")
    private Address address;
    @OneToOne
    @JoinColumn(name = "fk_telefone_cliente", referencedColumnName = "idtelefone")
    private Phone phone;
}
