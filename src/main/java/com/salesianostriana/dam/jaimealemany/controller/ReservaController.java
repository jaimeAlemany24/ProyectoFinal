package com.salesianostriana.dam.jaimealemany.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		model.addAttribute("fecha", LocalDate.now());
		model.addAttribute("lista", reservaServicio.findAllByFecha(LocalDate.now()));
		model.addAttribute("reserva", new Reserva());
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
		
		return "crearReserva";
	}
	
	@PostMapping ("reservas/finalizar")
	public String finalizarReserva(@ModelAttribute Reserva r, SessionStatus status) { // Incluye la clase de SessionAttributes
		reservaServicio.save(r);
		status.setComplete(); // Método que limpia el objeto de la sesión. Lo amo. Pero no mucho, porque sino sería raro.
		return "redirect:/";
	}
	/*----------------------------------------------------------------------*/
	
	
	// PÁGINA CRUD DE RESERVAS
	/*----------------------------------------------------------------------*/
	
	@GetMapping ("/consulta-reservas")
	public String mostrarListaReservas(@RequestParam String fecha, Model model) {
		model.addAttribute("fechaSeleccionada", LocalDate.parse(fecha));
		model.addAttribute("lista", reservaServicio.findAllByFecha(LocalDate.parse(fecha)));
		return "consultaReservas";
	}
	
	// Editar reserva
	@GetMapping("/reservas/editar/{id}")
	public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
	    // Futuro formulario rellenado
		return "redirect:/";
	}

	// Eliminar reserva
	@PostMapping("/reservas/eliminar/{id}")
	public String eliminarReserva(@PathVariable Long id) {
	    // Implementación para eliminar reserva
		return "redirect:/";
	}
	
	/*----------------------------------------------------------------------*/
}