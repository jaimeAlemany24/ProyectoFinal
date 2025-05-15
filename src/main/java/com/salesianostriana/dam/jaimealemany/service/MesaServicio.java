package com.salesianostriana.dam.jaimealemany.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salesianostriana.dam.jaimealemany.modelo.Mesa;
import com.salesianostriana.dam.jaimealemany.modelo.Reserva;
import com.salesianostriana.dam.jaimealemany.repositorios.MesaRepository;
import com.salesianostriana.dam.jaimealemany.service.base.BaseServiceImpl;

@Service
public class MesaServicio extends BaseServiceImpl<Mesa, Long, MesaRepository>{
	
	@Autowired
	private MesaRepository repositorio;

	
	// Método que crea la estructura de la tabla de disponibilidad. Esto se usa para 
	// imprimir en la vista una tabla que te diga si está libre el turno que quieres reservar.
	/*
	* La idea es crear un map que tenga todas las mesas acompañadas por dos booleans
	* (true=ocupado, false=libre, y uno por cada turno). De esta manera, tienes, de un día específico,
	* una tabla dinámica que contiene cada mesa y la ocupación en cada turno.
	* Esto se hace siguiendo:
	* 
	* - Recuperar todas las mesas
	* - Crear HashMap con array de booleans 
	* [0] = turno mañana , [1] = turno tarde
	* - ForEach mesa comprueba con Stream si hay reservas que ocupen ese día en el turno [0]
	* - Hace lo mismo en el turno [1]
	* - Devuelve el Map, listo para pasar al Model y generar una tabla.
	* */ 

	public Map<Long, Boolean[]> comprobarOcupacionDia(List<Reserva> reservas){
		List<Mesa> mesas= findAll();
		
		Map<Long, Boolean[]> disponibilidad = new HashMap<>();
		
		mesas.forEach(mesa ->{
			Boolean[] turnos = new Boolean[2];
	        turnos[0] = reservas.stream().anyMatch(r -> 
	            r.getMesa().getId_mesa().equals(mesa.getId_mesa()) && r.getTurno() == 1);
	        turnos[1] = reservas.stream().anyMatch(r -> 
	            r.getMesa().getId_mesa().equals(mesa.getId_mesa()) && r.getTurno() == 2);
	        disponibilidad.put(mesa.getId_mesa(), turnos);
		});
		return disponibilidad;
	}
}
