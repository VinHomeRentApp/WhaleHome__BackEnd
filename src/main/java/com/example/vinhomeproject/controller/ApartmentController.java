package com.example.vinhomeproject.controller;

import com.example.vinhomeproject.dto.ApartmentDTO;
import com.example.vinhomeproject.response.ResponseObject;
import com.example.vinhomeproject.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/apartments")
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAll(){
        return apartmentService.getAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getById(@PathVariable Long id){
        return apartmentService.getById(id);
    }
    @PostMapping
    public ResponseEntity<String> create(@RequestBody ApartmentDTO apartmentDTO){
        return apartmentService.create(apartmentDTO);
    }
    @PutMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return apartmentService.delete(id);
    }
    @PutMapping("/udpate/{id}")
    public ResponseEntity<String> update(@PathVariable Long id,@RequestBody ApartmentDTO apartmentDTO){
        return apartmentService.update(id, apartmentDTO);
    }
    @GetMapping("/count-all")
    public ResponseEntity<ResponseObject> countAll(){
        return apartmentService.countAll();
    }

    @GetMapping("/get-all-details")
    public ResponseEntity<ResponseObject> getAllDetails(){
        return apartmentService.findAllApartmentsWithDetails();
    }
    @GetMapping("/get-details/{id}")
    public ResponseEntity<ResponseObject> getByIdDetail(@PathVariable Long id){
        return apartmentService.findApartmentByIdWithDetails(id);
    }
}
