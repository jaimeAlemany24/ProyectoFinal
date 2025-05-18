package com.salesianostriana.dam.jaimealemany.service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.Jsr310Converters.LocalDateTimeToDateConverter;
import org.springframework.stereotype.Service;

import com.salesianostriana.dam.jaimealemany.modelo.Mesa;
import com.salesianostriana.dam.jaimealemany.modelo.Reserva;
import com.salesianostriana.dam.jaimealemany.repositorios.ReservaRepository;
import com.salesianostriana.dam.jaimealemany.service.base.BaseServiceImpl;

@Service
public class ReservaServicio extends BaseServiceImpl<Reserva, Long, ReservaRepository>{
	
	@Autowired
	private ReservaRepository repositorio;
	
	// Definición de constantes
	public static final int HORA_MINIMA_DESCUENTO = 3;
	public static final double DESCUENTO_POR_TIEMPO_RESERVA = 20.0;
	public static final double DESCUENTO_PROMOCIONES = 30.0;
	public static final double PRECIO_MAX_RESERVA = 25.0;
	public static final String DIA_OFERTA = "FRIDAY";
	
	public List<Reserva> findAllByFecha(LocalDate ld){
		return repositorio.findByFecha(ld);
	}
	
	public int calcularHorasReserva(Reserva reserva) {
		return reserva.getHoraFin().getHour() - reserva.getHoraInicio().getHour();
	}
	
	public double calcularPrecioBase(Reserva reserva, Mesa mesa) {
		
		double precioBase;
		int horas;
		
		horas= calcularHorasReserva(reserva);
		
		precioBase = mesa.getPrecioBase()*horas;
		return precioBase;
	}
	
	public double[] aplicarDescuentos(Reserva reserva, Mesa mesa) {
		
		double precioBase, precioFinal;
		int horas;
		double[] listaDescuentos = new double[3];
		
		horas=calcularHorasReserva(reserva);
		precioFinal=calcularPrecioBase(reserva, mesa);
		listaDescuentos[0]=precioFinal;
		listaDescuentos[1]=precioFinal;
		listaDescuentos[2]=0.0;
		
		if(horas>=HORA_MINIMA_DESCUENTO) {
			precioFinal=precioFinal*DESCUENTO_POR_TIEMPO_RESERVA;
			listaDescuentos[1]=precioFinal;
			listaDescuentos[2]=DESCUENTO_POR_TIEMPO_RESERVA;
		}
		if(reserva.getFecha().getDayOfWeek().toString().equals(DIA_OFERTA)) {
			System.out.println("Es viernes WOOOOOOOOOOOOO");
			precioFinal=precioFinal*DESCUENTO_PROMOCIONES;
			listaDescuentos[1]=precioFinal;
			listaDescuentos[2]=listaDescuentos[2]+DESCUENTO_PROMOCIONES;
		}
		
		listaDescuentos[0]=Math.min(precioFinal, PRECIO_MAX_RESERVA);
		return listaDescuentos;
	}
	
}
