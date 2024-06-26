package br.com.fiap.fintrack.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.fintrack.model.Movimentacao;
import br.com.fiap.fintrack.repository.MovimentacaoRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("movimentacao")
@Slf4j
public class MovimentacaoController {

    @Autowired
    MovimentacaoRepository repository;
    
    @GetMapping
    public Page<Movimentacao> index(
        @PageableDefault(size = 5, sort = "data", direction = Direction.DESC) Pageable pageable,
        @RequestParam(required = false) String categoriaNome,
        @RequestParam(required = false) Integer mes

    ){
        if (categoriaNome != null && mes != null){
            return repository.findByCategoriaNomeIgnoreCaseAndDataMonth(categoriaNome, mes, pageable);
        }

        if(mes != null){
            return repository.findByDataMonth(mes, pageable);
        }

        if (categoriaNome != null){
            return repository.findByCategoriaNomeIgnoreCase(categoriaNome, pageable);
        }
        return repository.findAll(pageable);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Movimentacao create(@RequestBody @Valid Movimentacao movimentacao){
        return repository.save(movimentacao);
    }

    @GetMapping("/ultimas")
    public List<Movimentacao> ultimasMovimentacoes() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("data").descending());
        return repository.findAll(pageable).getContent();
    }

    @GetMapping("/maior")
    public Movimentacao maiorMovimentacao() {
        return repository.findTopByOrderByValorDesc();
    }

    @GetMapping("/menor")
    public Movimentacao menorMovimentacao() {
        return repository.findTopByOrderByValorAsc();
    }

    @GetMapping("/ultima")
    public Movimentacao ultimaMovimentacao() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("data").descending());
        return repository.findAll(pageable).getContent().get(0);
    }
}

