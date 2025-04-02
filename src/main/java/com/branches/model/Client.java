package com.branches.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity(name = "cliente")
@Getter
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
    @OneToMany(mappedBy = "client")
    @JsonManagedReference
    private List<Phone> phones;
}
