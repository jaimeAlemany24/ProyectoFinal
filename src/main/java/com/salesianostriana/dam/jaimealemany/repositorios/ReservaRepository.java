package com.salesianostriana.dam.jaimealemany.repositorios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salesianostriana.dam.jaimealemany.modelo.Reserva;

public interface ReservaRepository 
	extends JpaRepository<Reserva, Long>{
	
	// Por motivos que desconozco, el repositorio hace automáticamente esta consulta
	// con tan solo declarar el método. Lo ha explicado Ángel.
	// No lo entiendo del todo, pero lo acepto con brazos abiertos.
	List<Reserva> findByFecha(LocalDate fecha);

}
