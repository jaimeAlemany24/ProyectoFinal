package com.salesianostriana.dam.jaimealemany.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@Entity
public class Mesa {

	@Id
	@GeneratedValue
	private Long id_mesa;
	private boolean tieneEsceno;
	private double precioBase;
}
