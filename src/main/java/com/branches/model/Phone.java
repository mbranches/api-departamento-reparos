package com.branches.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "telefone")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @JoinColumn(name = "fk_pessoa_telefone", referencedColumnName = "idpessoa")
    @JsonIgnore
    @ToString.Exclude
    private Person person;

}
