package com.salesianostriana.dam.jaimealemany.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
		Map<Reserva,Integer>reservasConEstado = reservaServicio.actualizarEstadosReservas(reservas);
		model.addAttribute("fecha", LocalDate.now());
		model.addAttribute("lista", reservasConEstado);
		model.addAttribute("reserva", new Reserva());
		model.addAttribute("mesas", mesaServicio.findAll());
		return "indice";
	}
	/*----------------------------------------------------------------------*/
	
	
	
	// FORMULARIO DE RESERVAS
	/*----------------------------------------------------------------------*/
	@PostMapping ("reservas/crear")
	public String crearReserva(@ModelAttribute Reserva r, Model model) {
		
		List<Mesa> disponibles = mesaServicio.buscarMesasDisponibles(
				reservaServicio.findAllByFecha(r.getFecha()),
		        r.getFecha(),
		        r.getHoraInicio(),
		        r.getHoraFin(),
		        r.isEscenografia()
		    );
		Map<Mesa, double[]> listaDescuentos=new HashMap<Mesa, double[]>();
		
		disponibles.forEach(mesa -> listaDescuentos.put(mesa,reservaServicio.aplicarDescuentos(r, mesa)));
		
		System.out.println(r.getFecha());
		model.addAttribute("mesas", disponibles);
		model.addAttribute("reserva", r);
		model.addAttribute("precioMesas", listaDescuentos);
		model.addAttribute("noHayMesas", disponibles.isEmpty());
		
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
	
	@GetMapping ("reservas/crear/error")
	public String mostrarErrorReservas() {
		return "errorReservas";
	}
	/*----------------------------------------------------------------------*/
	
	
	// PÁGINA CRUD DE RESERVAS
	/*----------------------------------------------------------------------*/
	
	@GetMapping ("/consulta-reservas")
	public String mostrarListaReservas(@RequestParam String fecha, @RequestParam(required=true) Integer canceladas, @RequestParam(required=true) int mesa,Model model) {
		
		Optional<Mesa> mesaBuscada = mesaServicio.findById((long)mesa);
		List<Reserva> reservas = reservaServicio.findAllByFecha(LocalDate.parse(fecha));
		reservas = reservaServicio.filtrarPorCanceladas(reservas, canceladas);
		reservas = reservaServicio.filtrarPorMesa(reservas, mesaBuscada);
		Map<Reserva,Integer>reservasConEstado = reservaServicio.actualizarEstadosReservas(reservas);
		model.addAttribute("fechaSeleccionada", LocalDate.parse(fecha));
		model.addAttribute("lista", reservasConEstado);
		model.addAttribute("mesas", mesaServicio.findAll());
		return "consultaReservas";
	}
	
	// Editar reserva
	@GetMapping("/reservas/editar/{id}")
	public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
	    Reserva reserva = reservaServicio.findById(id).get();

	    List<Mesa> disponibles = mesaServicio.buscarMesasDisponibles(
	        reservaServicio.findAllByFecha(reserva.getFecha()),
	        reserva.getFecha(),
	        reserva.getHoraInicio(),
	        reserva.getHoraFin(),
	        reserva.isEscenografia()
	    );
	    disponibles.add(reserva.getMesa()); // Para que la mesa de la reserva no se marque como "no disponible"
	    if (disponibles.isEmpty()) {
	        model.addAttribute("noHayMesas", true);
	    }
	    
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

	    return "editarReserva"; 
	}

	@PostMapping ("/reservas/editar/{id}")
	public String enviarFormularioEdicion(@ModelAttribute Reserva reserva, SessionStatus status) {
		
		Reserva reservaOriginal=reservaServicio.findById(reserva.getId_reserva()).get();
		
		Long idMesa = reserva.getMesa().getId_mesa();
	    Mesa mesaReal = mesaServicio.findById(idMesa).get();
		
		reservaOriginal.setFecha(reserva.getFecha());
	    reservaOriginal.setHoraInicio(reserva.getHoraInicio());
	    reservaOriginal.setHoraFin(reserva.getHoraFin());
	    reservaOriginal.setNombre(reserva.getNombre());
	    reservaOriginal.setCorreo(reserva.getCorreo());
	    reservaOriginal.setComentario(reserva.getComentario());
	    reservaOriginal.setPrecio(reserva.getPrecio());
	    reservaOriginal.setMesa(mesaReal);
		
	    reservaServicio.save(reservaOriginal);
	    
	    status.setComplete();
	    
		return"redirect:/consulta-reservas?fecha="+reservaOriginal.getFecha()+"&canceladas=0";
	}

	// Eliminar reserva
	@PostMapping("/reservas/eliminar/{id}")
	public String eliminarReserva(@PathVariable Long id) {
		Reserva reserva = reservaServicio.findById(id).get();
		reserva.setCancelada(true);
		reservaServicio.save(reserva);
		return"redirect:/consulta-reservas?fecha="+reserva.getFecha()+"&canceladas=0";
	}
	
	/*----------------------------------------------------------------------*/
}