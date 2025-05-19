package com.salesianostriana.dam.jaimealemany.repositorios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesianostriana.dam.jaimealemany.modelo.Mesa;
import com.salesianostriana.dam.jaimealemany.modelo.Reserva;

public interface ReservaRepository 
	extends JpaRepository<Reserva, Long>{
	
	// Por motivos que desconozco, el repositorio hace automáticamente esta consulta
	// con tan solo declarar el método. Lo ha explicado Ángel.
	// No lo entiendo del todo, pero lo acepto con brazos abiertos.
	List<Reserva> findByFecha(LocalDate fecha);
	
	@Query ("select r from Reserva r where r.mesa_id = mesaId")
	List<Reserva> buscarReservaPorMesa(Mesa mesaId);

}
