package com.salesianostriana.dam.jaimealemany.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salesianostriana.dam.jaimealemany.modelo.Reserva;
import com.salesianostriana.dam.jaimealemany.repositorios.ReservaRepository;
import com.salesianostriana.dam.jaimealemany.service.base.BaseServiceImpl;

@Service
public class ReservaServicio extends BaseServiceImpl<Reserva, Long, ReservaRepository>{
	
	@Autowired
	private ReservaRepository repositorio;
	
	public List<Reserva> findAllByFecha(LocalDate ld){
		return repositorio.findByFecha(ld);
	}
	
	// Método que comprueba si la reserva que se va a añadir es válida
	// (válida = no es una reserva para un día anterior a la fecha 
	// actual o ya existe una reserva para el mismo día, misma mesa, mismo turno)
	
	// Devuelve true si el hueco está ocupado, false si está libre
	public boolean comprobarReservaOcupada(Reserva r) {
		boolean esReservaOcupada=true;
		List<Reserva> listadoReservas = findAllByFecha(r.getFecha());
		
		// Comprobación de mesa y turno con el resto de reservas del mismo día
		esReservaOcupada = listadoReservas.stream().anyMatch(r2 -> 
        r.getMesa().getId_mesa().equals(r2.getMesa().getId_mesa()) && r.getTurno() == r2.getTurno());
		return esReservaOcupada;
	}
}
