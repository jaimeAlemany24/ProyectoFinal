package com.salesianostriana.dam.jaimealemany.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.salesianostriana.dam.jaimealemany.modelo.Mesa;
import com.salesianostriana.dam.jaimealemany.modelo.Reserva;
import com.salesianostriana.dam.jaimealemany.service.MesaServicio;
import com.salesianostriana.dam.jaimealemany.service.ReservaServicio;

@Controller
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
	public String showMainPage(Model model) {
		model.addAttribute("fecha", LocalDate.now());
		return "indice";
	}
	/*----------------------------------------------------------------------*/
	
	
	
	// FORMULARIO DE RESERVAS
	/*----------------------------------------------------------------------*/

	// Trabaja con el primer parámetro de la URL (fecha) y rellena el formulario con esa fecha.
	// Además, imprime tabla de disponibilidad de esa fecha
	@GetMapping ("/reservar")
	public String showResForm(@RequestParam String fecha, Model model) {
		
		// Creamos una reserva en blanco para rellenar con el form
		Reserva r=new Reserva();
		// La fecha se guarda directamente del parámetro de la URL
		r.setFecha(LocalDate.parse(fecha));
		r.setMesa(new Mesa());
		
		// Obtenemos la lista de mesas disponibles
		List<Mesa> mesas = mesaServicio.findAll();
		// Recupera las reservas del día y llama al mesaService para 
		// mapear la disponibilidad y pasársela al model
		List<Reserva> reservasDelDia = reservaServicio.findAllByFecha(LocalDate.parse(fecha));
		
		model.addAttribute("fechaSeleccionada", fecha);
		model.addAttribute("reserva", r);
		model.addAttribute("mesas", mesas);
		model.addAttribute("turnos", List.of(1,2));
		model.addAttribute("disponibilidad", mesaServicio.comprobarOcupacionDia(reservasDelDia));
		
		return "formRes";
	}
	
	@PostMapping ("/reservar/enviar")
	public String uploadReservation(@ModelAttribute ("reserva") Reserva r) {
		reservaServicio.save(r);
		return "redirect:/";
	}
	@GetMapping ("/reservar/enviar")
	public String showUploadReservation() {
		return "redirect:/";
	}
	
	/*----------------------------------------------------------------------*/
	
	
	// PÁGINA CRUD DE RESERVAS
	/*----------------------------------------------------------------------*/
	
	@GetMapping ("/consulta-reservas")
	public String showCurrentRes(@RequestParam String fecha, Model model) {
		model.addAttribute("fechaSeleccionada", LocalDate.parse(fecha));
		model.addAttribute("lista", reservaServicio.findAllByFecha(LocalDate.parse(fecha)));
		return "consultaReservas";
	}
	
	/*----------------------------------------------------------------------*/
}