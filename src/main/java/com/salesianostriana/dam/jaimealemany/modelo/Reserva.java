package com.salesianostriana.dam.jaimealemany.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fecha;
	
	private LocalTime horaInicio, horaFin;
	private double precio;
	private String nombre, correo, comentario;
	private boolean cancelada, escenografia;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;
}
