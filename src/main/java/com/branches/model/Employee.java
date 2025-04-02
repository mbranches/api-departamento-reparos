package com.branches.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "funcionario")
@Getter
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
    @JsonManagedReference
    private Address address;
}
