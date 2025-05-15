package com.salesianostriana.dam.jaimealemany.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salesianostriana.dam.jaimealemany.modelo.Mesa;

public interface MesaRepository 
	extends JpaRepository<Mesa, Long>{
}
