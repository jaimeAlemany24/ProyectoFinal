package com.salesianostriana.dam.jaimealemany.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Builder
public class Reserva {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id_reserva;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fecha;
	
	private LocalTime horaInicio, horaFin;
	private double precio;
	private String nombre, correo, comentario;
	private boolean escenografia, cancelada;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mesa_id")
	@ToString.Exclude
    private Mesa mesa;
}
