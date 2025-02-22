package com.hubforo.hub_foro.controller;

import com.hubforo.hub_foro.ValidacionException;
import com.hubforo.hub_foro.topico.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    @Autowired
    TopicoRepository topicoRepository;
    private Long id;

    @PostMapping
    @Transactional
    public ResponseEntity registrarTopico(@RequestBody @Valid DatosRegistroTopico datosRegistroTopico,
                                          UriComponentsBuilder uriComponentsBuilder){
        Topico topico = topicoRepository.save( new Topico(datosRegistroTopico));

        DatosRespuestaTopico datosRespuestaTopico = new DatosRespuestaTopico(topico.getTitulo(), topico.getMensaje(),
                topico.getAutor(), topico.getCurso());

        URI url =uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaTopico);
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoTopico>> listarTopico(@PageableDefault(size=10) Pageable paginacion){
        return ResponseEntity.ok(topicoRepository.findAll(paginacion).map(DatosListadoTopico::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosListadoTopico> retornarDetallesTopico(@PathVariable Long id){
        var topico = topicoRepository.findById(id).orElseThrow(() -> new ValidacionException("El ID ingresado no existe"));
        var detallesTopico = new DatosListadoTopico(topico.getId(), topico.getTitulo(), topico.getMensaje(),
                topico.getFechaCreacion(), topico.getStatus(), topico.getAutor(), topico.getCurso());
        return ResponseEntity.ok(detallesTopico);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DatosListadoTopico> actualizarTopico(@RequestBody @Valid DatosActualizarTopico datosActualizarTopico){
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ValidacionException("El ID ingresado no existe"));

        topico.actualizarTopico(datosActualizarTopico);

        var detallesActualizados = new DatosListadoTopico(topico.getId(), topico.getTitulo(), topico.getMensaje(),
                topico.getFechaCreacion(), topico.getStatus(), topico.getAutor(), topico.getCurso());

        return ResponseEntity.ok(detallesActualizados);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity eliminarTopico(@PathVariable Long id){

        if (!topicoRepository.findById(id).isPresent()){
            throw new ValidacionException("El ID ingresado no existe");
        }

        topicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
