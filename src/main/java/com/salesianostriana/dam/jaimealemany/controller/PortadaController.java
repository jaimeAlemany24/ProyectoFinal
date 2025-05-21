package com.salesianostriana.dam.jaimealemany.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inicio")
public class PortadaController {
	
	@GetMapping("/")
	public String mostrarPortada() {
		return "cliente/portada";
	}
	
	@GetMapping("/quienes-somos")
	public String mostrarQuienesSomos() {
		return"cliente/quienes-somos";
	}

}
