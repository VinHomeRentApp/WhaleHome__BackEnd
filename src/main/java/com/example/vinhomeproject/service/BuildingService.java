package com.example.vinhomeproject.service;


import com.example.vinhomeproject.dto.AreaDTO;
import com.example.vinhomeproject.dto.BuildingDTO;
import com.example.vinhomeproject.models.Area;
import com.example.vinhomeproject.models.Building;
import com.example.vinhomeproject.models.Post;
import com.example.vinhomeproject.repositories.BuildingRepository;
import com.example.vinhomeproject.response.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BuildingService {
    @Autowired
    private BuildingRepository buildingRepository;

    public ResponseEntity<ResponseObject> getAll(){
        List<Building> buildings = buildingRepository.findAll();
        return ResponseEntity.ok(new ResponseObject(
                "successfully",
                buildings
        ));
    }

    public ResponseEntity<ResponseObject> getById(Long id){
        Optional<Building> building = buildingRepository.findById(id);
        return ResponseEntity.ok(new ResponseObject(
                "successfully",
                building
        ));
    }

    public ResponseEntity<ResponseObject> create(BuildingDTO buildingDTO){
        Building building = new Building();
        building.setStatus(true);
        building.setCreateBy(String.valueOf(LocalDate.now()));
        building.setName(buildingDTO.getName());
        building.setZone(buildingDTO.getZone());
        buildingRepository.save(building);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                "Create building successfully",
                building
        ));
    }

    public ResponseEntity<String> delete(Long id){
        Optional<Building> building = buildingRepository.findById(id);
        if(building.isPresent()){
            building.get().setStatus(!building.get().isStatus());
            buildingRepository.save(building.get());
            return ResponseEntity.ok("successfully");
        }
        return ResponseEntity.badRequest().body("failed");
    }

    public ResponseEntity<ResponseObject> update(Long id, BuildingDTO buildingDTO){
        Optional<Building> building = buildingRepository.findById(id);
        if(building.isPresent()){
            if (buildingDTO.getName()!=null){building.get().setName(buildingDTO.getName());}
            if(buildingDTO.getZone()!=null){building.get().setZone(buildingDTO.getZone());}

            buildingRepository.save(building.get());
            return ResponseEntity.ok(new ResponseObject(
                    "Update successfully",
                    building
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                "Not found building",
                ""
        ));
    }
    public Page<Building> getPage(int currentPage, int pageSize, String field) {
        return buildingRepository.findAll(PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.ASC, field)));
    }
    public int count() {
        return buildingRepository.findAll().size();
    }

    public Page<Building> filterByArea(Long id,int currentPage, int pageSize, String field) {
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.ASC, field));
        return buildingRepository.findAllBuildingsByAreaId(id,pageable);
    }
}
