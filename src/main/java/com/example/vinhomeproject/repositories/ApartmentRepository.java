package com.example.vinhomeproject.repositories;

import com.example.vinhomeproject.dto.ApartmentDTO_2;
import com.example.vinhomeproject.models.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment,Long> {
    @Query("SELECT NEW com.example.vinhomeproject.dto.ApartmentDTO_2(" +
            "a.name, a.description, a.living_room, a.bed_room, a.kitchen, a.rest_room, " +
            "a.floor, a.area, a.status, a.air_conditioner, a.electric_fan, a.television, " +
            "a.electric_stoves, a.gas_stoves, b, z, ar) " +
            "FROM Apartment a " +
            "JOIN a.building b " +
            "JOIN b.zone z " +
            "JOIN z.area ar")
    Set<ApartmentDTO_2> findAllApartmentsWithDetails();

    @Query("SELECT NEW com.example.vinhomeproject.dto.ApartmentDTO_2(" +
            "a.name, a.description, a.living_room, a.bed_room, a.kitchen, a.rest_room, " +
            "a.floor, a.area, a.status, a.air_conditioner, a.electric_fan, a.television, " +
            "a.electric_stoves, a.gas_stoves, b, z, ar) " +
            "FROM Apartment a " +
            "JOIN a.building b " +
            "JOIN b.zone z " +
            "JOIN z.area ar " +
            "WHERE a.id = :apartmentId")
    ApartmentDTO_2 findApartmentByIdWithDetails(Long apartmentId);
}
