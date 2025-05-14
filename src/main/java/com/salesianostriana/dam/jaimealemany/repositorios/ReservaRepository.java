package com.salesianostriana.dam.jaimealemany.repositorios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salesianostriana.dam.jaimealemany.modelo.Reserva;

public interface ReservaRepository 
	extends JpaRepository<Reserva, Long>{
	
	List<Reserva> findByFecha(LocalDate fecha);

}
