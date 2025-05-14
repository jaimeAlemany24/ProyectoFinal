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
		model.addAttribute("fechaSeleccionada", fecha);
		System.out.println("Fecha enviada: "+fecha);
		model.addAttribute("reserva", new Reserva());
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

	

	

	// Los metodos post necesitan un método get
	// //action, method post y th:object
	// Esto es un buscar ! Debe devolver un Optional, sí o sí
	

	// !!!!! Este método es el que envía los datos a la base de datos !!!!!
	// Y además, te devuelve la id de la reserva como confirmación (por lo menos esa es la intención)

		// Código que llame al servicio para guardar
		// Imprime el ID de reserva en pantalla
}