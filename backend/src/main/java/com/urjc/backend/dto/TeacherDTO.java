package com.urjc.backend.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class TeacherDTO {

    public interface Base {
    }

    @Id
    @JsonView(Base.class)
    private Long id;

    @NotNull(message = "Se deben añadir roles")
    private List<String> roles;

    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @NotBlank(message = "Se debe completar el nombre")
    @JsonView(Base.class)
    private String name;

    @Email(message = "Introduzca un email válido")
    @NotBlank(message = "Se debe completar el email")
    @JsonView(Base.class)
    private String email;
}
