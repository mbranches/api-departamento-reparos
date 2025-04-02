package com.branches.model;

import jakarta.persistence.*;

@Entity(name = "telefone")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtelefone")
    private Long id;
    @Column(name = "numero")
    private String number;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_telefone")
    private PhoneType phoneType;
    @ManyToOne
    @JoinColumn(name = "fk_cliente_telefone", referencedColumnName = "idcliente")
    private Client client;
    @JoinColumn(name = "fk_funcionario_telefone", referencedColumnName = "idfuncionario")
    @ManyToOne
    private Employee employee;
}
