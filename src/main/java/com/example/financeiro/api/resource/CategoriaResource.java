package com.example.financeiro.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.financeiro.api.event.RecursoCriadoEvent;
import com.example.financeiro.api.model.Categoria;
import com.example.financeiro.api.repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ApplicationEventPublisher publisher;

//	@CrossOrigin(maxAge = 10, origins = { "Http://localhost:8000" })
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and hasAuthority('SCOPE_read')")
	public List<Categoria> lista() {
		return categoriaRepository.findAll();
	}

	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and hasAuthority('SCOPE_write')")
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaRepository.save(categoria);

		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));

		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);

	}

	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and hasAuthority('SCOPE_write')")
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		Optional<Categoria> categoria = this.categoriaRepository.findById(codigo);
		return categoria.isPresent() ?
				ResponseEntity.ok(categoria.get()) : ResponseEntity.notFound().build();
	}
	
//	@GetMapping("/{codigo}")
//	public Categoria buscarPeloCodigo(@PathVariable Long codigo) {
//	  return this.categoriaRepository.findById(codigo).orElse(null);
//	}

//	@GetMapping("/{codigo}")
//	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and hasAuthority('SCOPE_read')")
//	public ResponseEntity<Categoria> buscarId(@PathVariable Long codigo) {
//		return this.categoriaRepository.findById(codigo)
//        .map(categoria -> ResponseEntity.ok(categoria)) 
//        .orElse(ResponseEntity.notFound().build());
//	}

}

