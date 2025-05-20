package com.salesianostriana.dam.jaimealemany.modelo;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor
@Entity
public class Mesa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_mesa;
	private boolean tieneEsceno;
	private double precioBase;
	
	@OneToMany(mappedBy = "mesa", cascade = CascadeType.REMOVE)
	@ToString.Exclude
	private List<Reserva> reservas;
}
