package com.salesianostriana.dam.jaimealemany.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.salesianostriana.dam.jaimealemany.modelo.Mesa;
import com.salesianostriana.dam.jaimealemany.service.MesaServicio;

@Controller
@RequestMapping ("/mesas")
public class MesaController {
	
	@Autowired
	private MesaServicio mesaServicio;
	
	@GetMapping
	public String mostrarMesas(Model model) {
		model.addAttribute("mesas",mesaServicio.findAll());
		return "listaMesas";
	}
	
	@GetMapping("/nueva")
	public String mostrarFormNuevaMesa(Model model) {
		model.addAttribute("editar", false);
		model.addAttribute("mesa", new Mesa());
		return "formMesa";
	}
	
	@PostMapping ("/guardar")
	public String guardarMesa(@ModelAttribute Mesa mesa) {
		System.out.println(mesa.getId_mesa());
		mesaServicio.save(mesa);
		return "redirect:/mesas";
	}
	
	@GetMapping ("/editar/{id}")
	public String mostrarFormEdicion(@PathVariable Long id ,Model model) {
		model.addAttribute("editar", true);
		model.addAttribute("mesa", mesaServicio.findById(id));
		return "formMesa";
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminarMesa(@PathVariable Long id, Model model) {
		mesaServicio.deleteById(id);
		return "redirect:/mesas";
	}
}
