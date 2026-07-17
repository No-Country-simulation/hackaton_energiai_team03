package dev.team3.chillywatts.controller;

import dev.team3.chillywatts.entity.Freezer;
import dev.team3.chillywatts.repository.FreezerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FreezerController {

    @Autowired
    private FreezerRepository freezerRepositorio;

    @GetMapping("/{id}")
    public Freezer getFreezer(@PathVariable("id") Long id){
        return freezerRepositorio.findById(id).get();
    }

}
