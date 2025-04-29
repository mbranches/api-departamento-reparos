package com.branches.model;

import jakarta.persistence.*;
import lombok.*;

@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "funcionario")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfuncionario")
    private Long id;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "fk_pessoa_funcionario", referencedColumnName = "idpessoa")
    private Person person;
    @ManyToOne
    @JoinColumn(name = "fk_categoria_funcionario", referencedColumnName = "idcategoria")
    private Category category;
}
