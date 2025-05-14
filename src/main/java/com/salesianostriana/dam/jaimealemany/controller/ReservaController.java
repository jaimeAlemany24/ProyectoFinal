package com.salesianostriana.dam.jaimealemany.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.salesianostriana.dam.jaimealemany.modelo.Reserva;
import com.salesianostriana.dam.jaimealemany.service.ReservaServicio;

@Controller
public class ReservaController {

	// Se inserta un ReservaServicio para usar los métodos de BBDD
	@Autowired
	private ReservaServicio reservaServicio;
	
	/*
	  * Petición para pintar la portada. El Model se queda con la fecha 
	 * actual para pintarla en la vista.
	 * */
	
	@GetMapping ("/")
	public String showMainPage(Model model) {
		model.addAttribute("fecha", LocalDate.now());
		return "indice";
	}
	
	/*
	 * Trabaja con el primer parámetro de la URL (fecha) y rellena el formulario con esa fecha.
	 * */
	@GetMapping ("/reservar")
	public String showResForm(@RequestParam String fecha, Model model) {
		Reserva r=new Reserva();
		r.setFecha(LocalDate.parse(fecha));
		
		model.addAttribute("fechaSeleccionada", fecha);
		model.addAttribute("reserva", r);
		// ReservaServicio devuelve una lista de reservas de ese día, y la tabla se pinta según
		// la disponibilidad "libre" u "ocupado".
		
		return "formRes";
	}
	
	@GetMapping ("/consulta-reservas")
	public String showCurrentRes(@RequestParam String fecha, Model model) {
		model.addAttribute("fechaSeleccionada", LocalDate.parse(fecha));
		model.addAttribute("lista", reservaServicio.findAllByFecha(LocalDate.parse(fecha)));
		return "consultaReservas";
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
}