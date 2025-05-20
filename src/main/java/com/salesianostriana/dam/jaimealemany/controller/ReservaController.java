package com.salesianostriana.dam.jaimealemany.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.salesianostriana.dam.jaimealemany.modelo.Mesa;
import com.salesianostriana.dam.jaimealemany.modelo.Reserva;
import com.salesianostriana.dam.jaimealemany.service.MesaServicio;
import com.salesianostriana.dam.jaimealemany.service.ReservaServicio;

@Controller
@SessionAttributes("reserva") // Esto hace que el objeto reserva mantenga sus atributos entre peticiones, hasta borrarlo de la sesión
public class ReservaController {

	@Autowired
	private ReservaServicio reservaServicio;
	
	@Autowired
	private MesaServicio mesaServicio;
	
	// PORTADA
	/*----------------------------------------------------------------------*/
	/*
	 * Petición para pintar la portada. El Model se queda con la fecha 
	 * actual para pintarla en la vista.
	 * */
	
	@GetMapping ("/")
	public String mostrarPagPpal(Model model) {
		
		List<Reserva> reservas = reservaServicio.findAllByFecha(LocalDate.now());
		List<Reserva> reservasEnProceso = reservaServicio.filtrarPorEstadoReservas(reservas, 2);
		List<Reserva> reservasConfirmadas= reservaServicio.filtrarPorEstadoReservas(reservas, 1);
		List<Reserva> rvasPresentesList = reservaServicio.filtrarPorCanceladas(reservas, 2);
		List<Mesa> mesas=mesaServicio.findAll();
		double porcOcup=reservaServicio.calcularOcupacion(rvasPresentesList, mesas);
		
		Map<Reserva,Integer>reservasConEstado = reservaServicio.actualizarEstadosReservas(reservas);
		model.addAttribute("fecha", LocalDate.now());
		model.addAttribute("lista", reservasConEstado);
		model.addAttribute("reserva", new Reserva());
		model.addAttribute("mesas", mesas);
		// Estadísticas
		
		model.addAttribute("numReservas", reservas.size());
		model.addAttribute("numEnProceso",reservasEnProceso.size());
		model.addAttribute("totalPrecio", reservaServicio.calcularTotalIngresadoReservas(rvasPresentesList));
		model.addAttribute("porcOcup", porcOcup);
		return "indice";
	}
	/*----------------------------------------------------------------------*/
	
	
	
	// FORMULARIO DE RESERVAS
	/*----------------------------------------------------------------------*/
	@PostMapping ("reservas/crear")
	public String crearReserva(@ModelAttribute Reserva r, Model model, SessionStatus status) {
		boolean errorHoras=false;
		
		List<Mesa> disponibles = mesaServicio.buscarMesasDisponibles(
				reservaServicio.findAllByFecha(r.getFecha()),
		        r.getFecha(),
		        r.getHoraInicio(),
		        r.getHoraFin(),
		        r.isEscenografia()
		    );
		Map<Mesa, double[]> listaDescuentos=new HashMap<Mesa, double[]>();
		
		disponibles.forEach(mesa -> listaDescuentos.put(mesa,reservaServicio.aplicarDescuentos(r, mesa)));
		
		
		model.addAttribute("mesas", disponibles);
		model.addAttribute("reserva", r);
		model.addAttribute("precioMesas", listaDescuentos);
		model.addAttribute("noHayMesas", disponibles.isEmpty());
		if(!reservaServicio.comprobarHorasValidas(r)) {  // Si la hora de inicio y la de final no son válidas
			errorHoras=true;
			status.setComplete(); // En caso de que no sea válido, se limpia de la sesión para que no de problemas en las siguientes reservas
		}
		model.addAttribute("errorHoras", errorHoras);
		return "crearReserva";
	}
	
	@PostMapping ("reservas/finalizar")
	public String finalizarReserva(@ModelAttribute Reserva r,SessionStatus status) { // Incluye la clase de SessionAttributes
		double precio;
		
		Long idMesa = r.getMesa().getId_mesa();
	    Mesa mesaReal = mesaServicio.findById(idMesa).get();
	    
	    r.setMesa(mesaReal);
		
		precio=reservaServicio.aplicarDescuentos(r, r.getMesa())[1];
		System.out.println(r.getMesa());
		
		r.setPrecio(precio);
		reservaServicio.save(r);
		status.setComplete(); // Método que limpia el objeto de la sesión. Lo amo. Pero no mucho, porque sino sería raro.
		return "redirect:/";
	}
	
	@GetMapping ("reservas/error")
	public String mostrarErrorReservas() {
		return "errorReservas";
	}
	/*----------------------------------------------------------------------*/
	
	
	// PÁGINA CRUD DE RESERVAS
	/*----------------------------------------------------------------------*/
	
	@GetMapping ("/consulta-reservas")
	public String mostrarListaReservas(@RequestParam String fecha, @RequestParam(required=false) Integer canceladas, @RequestParam(required=false) Long mesa,Model model) {
		
		if(mesa==null) {
			mesa=(long) 0;
		}
		if (fecha==null) {
			fecha=LocalDate.now().toString();
		}
		List<Reserva> reservas = reservaServicio.findAllByFecha(LocalDate.parse(fecha));
		reservas = reservaServicio.filtrarPorCanceladas(reservas, canceladas);
		reservas = reservaServicio.filtrarPorMesa(reservas, mesa);
		Map<Reserva,Integer>reservasConEstado = reservaServicio.actualizarEstadosReservas(reservas);
		model.addAttribute("fechaSeleccionada", LocalDate.parse(fecha));
		model.addAttribute("lista", reservasConEstado);
		model.addAttribute("mesas", mesaServicio.findAll());
		return "consultaReservas";
	}
	
	// Editar reserva
	@GetMapping("/reservas/editar/{id}")
	public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
		boolean finalizada=false;
		
	    Reserva reserva = reservaServicio.findById(id).get();

	    List<Mesa> disponibles = mesaServicio.buscarMesasDisponibles(
	        reservaServicio.findAllByFecha(reserva.getFecha()),
	        reserva.getFecha(),
	        reserva.getHoraInicio(),
	        reserva.getHoraFin(),
	        reserva.isEscenografia()
	    );
	    disponibles.add(reserva.getMesa()); // Para que la mesa de la reserva no se marque como "no disponible"
	    
	    Map<Mesa, double[]> listaDescuentos=new HashMap<Mesa, double[]>();
		
		disponibles.forEach(mesa -> listaDescuentos.put(mesa,reservaServicio.aplicarDescuentos(reserva, mesa)));
	    
	    List<LocalTime> horasEnPunto = IntStream.rangeClosed(10, 22) // Para manejar horas mejor con Thymeleaf
	    	    .mapToObj(h -> LocalTime.of(h, 0))
	    	    .collect(Collectors.toList());

	    model.addAttribute("horas", horasEnPunto);
	    
	    model.addAttribute("reserva", reserva);
	    model.addAttribute("mesas", disponibles);
	    model.addAttribute("fechaSeleccionada", reserva.getFecha());
	    model.addAttribute("mesasDisponibles", listaDescuentos);
	    
	    // Gestión de reservas ya finalizadas
	    if(reservaServicio.comprobarEstadoReserva(reserva)==3||reservaServicio.comprobarEstadoReserva(reserva)==0) {
	    	finalizada=true;
	    }
	    model.addAttribute("finalizada",finalizada);
	    return "editarReserva"; 
	}

	@PostMapping ("/reservas/editar/{id}")
	public String enviarFormularioEdicion(@ModelAttribute Reserva r, SessionStatus status) {
		
		Reserva reservaOriginal=reservaServicio.findById(r.getId_reserva()).get();
		
		Long idMesa = r.getMesa().getId_mesa();
	    Mesa mesaReal = mesaServicio.findById(idMesa).get();
		
		reservaOriginal.setFecha(r.getFecha());
	    reservaOriginal.setHoraInicio(r.getHoraInicio());
	    reservaOriginal.setHoraFin(r.getHoraFin());
	    reservaOriginal.setNombre(r.getNombre());
	    reservaOriginal.setCorreo(r.getCorreo());
	    reservaOriginal.setComentario(r.getComentario());
	    reservaOriginal.setPrecio(r.getPrecio());
	    reservaOriginal.setMesa(mesaReal);
	    
	    if(!reservaServicio.comprobarHorasValidas(reservaOriginal)) {
			return "redirect:/reservas/error";
		}
		
	    reservaServicio.save(reservaOriginal);
	    
	    status.setComplete();
	    
		return"redirect:/consulta-reservas?fecha="+reservaOriginal.getFecha()+"&canceladas=0";
	}

	// Eliminar reserva
	@PostMapping("/reservas/eliminar/{id}")
	public String eliminarReserva(@PathVariable Long id) {
		Reserva r=reservaServicio.findById(id).get();
		if(reservaServicio.comprobarEstadoReserva(r)==3||reservaServicio.comprobarEstadoReserva(r)==0) {  // Gestiona error en caso de ser reserva finalizada
	    	return "redirect:/reservas/editar/"+id;
	    }
		r.setCancelada(true);
		reservaServicio.save(r);
		return"redirect:/consulta-reservas?fecha="+r.getFecha()+"&canceladas=0";
	}
	
	/*----------------------------------------------------------------------*/
}