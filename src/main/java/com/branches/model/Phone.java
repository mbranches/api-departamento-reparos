package com.branches.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Person person;

    @Override
    public String toString() {
        return "Phone(id=" + this.getId() + ", number=" + this.getNumber() + ", phoneType=" + this.getPhoneType() + ")";
    }
}
