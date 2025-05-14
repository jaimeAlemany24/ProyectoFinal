package com.salesianostriana.dam.jaimealemany.modelo;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Builder
public class Reserva {
	
	@Id
	@GeneratedValue
	private long id_reserva;
	
	private LocalDate fecha;
	private int turno;
	private long id_mesa;
	private String nombre, correo, comentario;
	
}
