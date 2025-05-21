package com.salesianostriana.dam.jaimealemany.service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.salesianostriana.dam.jaimealemany.JaimeAlemanyApplication;
import com.salesianostriana.dam.jaimealemany.modelo.Mesa;
import com.salesianostriana.dam.jaimealemany.modelo.Reserva;
import com.salesianostriana.dam.jaimealemany.repositorios.ReservaRepository;
import com.salesianostriana.dam.jaimealemany.service.base.BaseServiceImpl;

@Service
public class ReservaServicio extends BaseServiceImpl<Reserva, Long, ReservaRepository>{

    private final JaimeAlemanyApplication jaimeAlemanyApplication;
	
	@Autowired
	private ReservaRepository repositorio;
	
	// Definición de constantes
	public static final int HORA_MINIMA_DESCUENTO = 3;
	public static final double DESCUENTO_POR_TIEMPO_RESERVA = 20.0;
	public static final double DESCUENTO_PROMOCIONES = 30.0;
	public static final double PRECIO_MAX_RESERVA = 25.0;
	public static final String DIA_OFERTA = "FRIDAY";
	public static final int HORA_APERTURA = 9;
	public static final int HORA_CIERRE = 22;


    ReservaServicio(JaimeAlemanyApplication jaimeAlemanyApplication) {
        this.jaimeAlemanyApplication = jaimeAlemanyApplication;
    }
	
	
	public List<Reserva> findAllByFecha(LocalDate ld){
		return repositorio.findByFecha(ld);
	}
	
	public List<Reserva> filtrarPorMesa(List<Reserva> reservas, Long mesa){
		
		if(mesa!=0) {
			reservas=reservas.stream().filter(reserva -> reserva.getMesa().getId_mesa().equals(mesa)).collect(Collectors.toList());
		}
		return reservas;
	}
	
	// tipo=1, te devuelve sólo las canceladas
	// tipo=2, te devuelve el resto
	public List<Reserva> filtrarPorCanceladas(List<Reserva> reservas, int tipo){
		if(tipo==1) {
			reservas=reservas.stream()
			.filter(reserva ->
			reserva.isCancelada())
			.collect(Collectors.toList());
		}
		if(tipo==2) {
			reservas=reservas.stream()
			.filter(reserva ->
			!reserva.isCancelada())
			.collect(Collectors.toList());
		}
		return reservas;
		
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
	
	/*
	 * Método que devuelve un array de 3 posiciones:
	 * - [0]: Precio calculado con mesa.precioBase * horas de reserva
	 * - [1]: Precio calculado añadiendo descuentos sobre el precio base
	 * - [2]: El porcentaje sobre 100 del descuento final
	 * 
	 * 				La lógica es la siguiente:
	 * - Cada mesa tiene un precio por hora (p. ej. 6.00€)
	 * - Si reservas de 10:00 a 12:00, se te aplica 6.00€*2
	 * - Si la reserva es de 3+ horas, se aplica un descuento constante (DESCUENTO_POR_TIEMPO_RESERVA)
	 * - Un día de la semana (ej. viernes) se aplica un descuento fijo (DESCUENTO_PROMOCIONES)
	 * - Los descuentos se acumulan
	 * - El precio nunca puede superar un precio máximo (PRECIO_MAX_RESERVA)
	 * 
	 * */
	
	public double[] aplicarDescuentos(Reserva reserva, Mesa mesa) {
		
		double precioFinal;
		int horas;
		double[] listaDescuentos = new double[3];
		
		horas=calcularHorasReserva(reserva);
		precioFinal=calcularPrecioBase(reserva, mesa);
		listaDescuentos[0]=precioFinal;
		listaDescuentos[1]=precioFinal;
		listaDescuentos[2]=0.0;
		
		if(horas>=HORA_MINIMA_DESCUENTO) {
			precioFinal=precioFinal-((precioFinal*DESCUENTO_POR_TIEMPO_RESERVA)/100);
			listaDescuentos[1]=precioFinal;
			listaDescuentos[2]=DESCUENTO_POR_TIEMPO_RESERVA;
		}
		if(reserva.getFecha().getDayOfWeek().toString().equals(DIA_OFERTA)) {
			precioFinal=precioFinal-((precioFinal*DESCUENTO_PROMOCIONES)/100);
			listaDescuentos[1]=precioFinal;
			listaDescuentos[2]=listaDescuentos[2]+DESCUENTO_PROMOCIONES;
		}
		
		listaDescuentos[1]=Math.min(precioFinal, PRECIO_MAX_RESERVA);
		
		if(listaDescuentos[1]==PRECIO_MAX_RESERVA) {
			listaDescuentos[2]=100-(PRECIO_MAX_RESERVA*100/listaDescuentos[0]);
		}
		return listaDescuentos;
	}
	
	
	/*
	 * Mapa que actualiza los estados de la reserva sólo para la vista.
	 * 1 = confirmada
	 * 2 = en proceso
	 * 3 = finalizada
	 * 0 = cancelada
	 * */
	
	public Map<Reserva, Integer> actualizarEstadosReservas(List<Reserva> reservas) {
		Map<Reserva, Integer> lista= new HashMap<Reserva, Integer>();
		reservas.stream().forEach(reserva -> {
			lista.put(reserva, comprobarEstadoReserva(reserva));
		});
		return lista;
	}
	
	public int comprobarEstadoReserva(Reserva reserva) {
		int estado=0;
		LocalDateTime hora = LocalDateTime.now();
		LocalDateTime hInicio=reserva.getFecha().atTime(reserva.getHoraInicio());
		LocalDateTime hFinal=reserva.getFecha().atTime(reserva.getHoraFin());
		if(hora.isBefore(hInicio)) {
			estado=1;
		}
		if(hora.isAfter(hInicio)&&hora.isBefore(hFinal)) {
			estado=2;
		}
		if(hora.isAfter(hFinal)) {
			estado=3;
		}
		if (reserva.isCancelada()) {
			estado=0;
		}
		return estado;
	}
	
	public boolean comprobarHorasValidas(Reserva r) {
		LocalTime horaInicio=r.getHoraInicio();
		LocalTime horaFin=r.getHoraFin();
		
		if(horaInicio.isAfter(horaFin)||horaInicio.equals(horaFin)) {
			return false;
		}
		return true;
	}
	
	public List<Reserva> filtrarPorEstadoReservas(List<Reserva> reservas, int estado){
		
		reservas=reservas.stream().filter(reserva -> comprobarEstadoReserva(reserva)==estado).collect(Collectors.toList());
		
		return reservas;
	}
	
	public double calcularTotalIngresadoReservas(List<Reserva> reservas) {
		double total=0.0;
		
		for(Reserva r:reservas) {
			total+=r.getPrecio();
		}
		
		return total;
	}
	
	/*
	 * Calcula el porcentaje de ocupación mediante la fórmula:
	 * - HorasOcupadas/HorasTotales
	 * 
	 * */
	
	public double calcularOcupacion(List<Reserva> reservas, List<Mesa> mesas) {
		int horasTotales;
		int horasOcupadas=0;
		int horasPorMesa;
		
		double porcentajeOcup;
		
		horasPorMesa=HORA_CIERRE-HORA_APERTURA;
		horasTotales=mesas.size()*horasPorMesa;
		for(Reserva r:reservas) {
			horasOcupadas+=calcularHorasReserva(r);
		}
		porcentajeOcup=((double)horasOcupadas/(double)horasTotales)*100;
		return porcentajeOcup;
	}
}
