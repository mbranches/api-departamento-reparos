package com.branches.model;

import jakarta.persistence.*;

@Entity(name = "funcionario")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfuncionario")
    private Long id;
    @Column(name = "nome")
    private String name;
    @Column(name = "sobrenome")
    private String lastName;
    @ManyToOne
    @JoinColumn(name = "fk_categoria_funcionario", referencedColumnName = "idcategoria")
    private Category category;
    @OneToOne
    @JoinColumn(name = "fk_endereco_funcionario", referencedColumnName = "idendereco")
    private Address address;
}
