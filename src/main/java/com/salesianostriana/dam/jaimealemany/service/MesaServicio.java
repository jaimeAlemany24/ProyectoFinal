package com.salesianostriana.dam.jaimealemany.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.salesianostriana.dam.jaimealemany.modelo.Mesa;
import com.salesianostriana.dam.jaimealemany.modelo.Reserva;
import com.salesianostriana.dam.jaimealemany.repositorios.MesaRepository;
import com.salesianostriana.dam.jaimealemany.service.base.BaseServiceImpl;

@Service
public class MesaServicio extends BaseServiceImpl<Mesa, Long, MesaRepository>{
	
	
	
	// Se le pasa como parámetro una lista de reservas de ese día, 
	// y descarta las mesas ocupadas que se solapen con la reserva que se quiere hacer
	public List<Mesa> buscarMesasDisponibles(List<Reserva> listaReservas, LocalDate dia, LocalTime horaInicio, LocalTime horaFin, boolean escenografia){
		
		List<Mesa> listaMesas=findAll();
		List<Mesa> listaMesasNoDisponibles=new ArrayList<Mesa>();
		
		listaReservas.stream().filter(reserva ->
				!reserva.isCancelada())
				.collect(Collectors.toList());
		
		if(escenografia) {
			listaReservas = listaReservas.stream()
				.filter(reserva ->
				reserva.isEscenografia())
				.collect(Collectors.toList());
			
			listaMesas.stream().forEach(mesa ->{
				if(!mesa.isTieneEsceno())
					listaMesasNoDisponibles.add(mesa);
			});
		}else {
			listaReservas= listaReservas.stream()
			.filter(reserva ->
			!reserva.isEscenografia())
			.collect(Collectors.toList());
			
			listaMesas.stream().forEach(mesa ->{
				if(mesa.isTieneEsceno())
					listaMesasNoDisponibles.add(mesa);
			});
		}
		
		listaReservas.stream()
			.filter(reserva ->
				reserva.getHoraInicio().isBefore(horaFin) &&
				horaInicio.isBefore(reserva.getHoraFin())
				).forEach(reserva -> {
					listaMesasNoDisponibles.add(reserva.getMesa());
				});
		
		listaMesas.removeAll(listaMesasNoDisponibles);
		return listaMesas;
	}
	
}
