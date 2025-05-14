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
}
