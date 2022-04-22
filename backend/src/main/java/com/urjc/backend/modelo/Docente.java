package com.urjc.backend.modelo;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name = "docente")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(unique = true, nullable = false)
    private Set<POD> pods;

    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(unique = true, nullable = false)
    private Set<CursoDocente> cursoDocentes;

    @Column(nullable = false)
    private String rol;

    @NotEmpty(message = "Se debe completar el nombre")
    @Column(nullable = false)
    private String nombre;

    @Email
    @NotEmpty(message = "Se debe completar el email")
    @Column(unique = true, nullable = false)
    private String email;
}
