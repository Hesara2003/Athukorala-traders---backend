package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.Shift;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShiftRepository extends MongoRepository<Shift, String> {
    List<Shift> findByStaffIdOrderByStartTimeDesc(String staffId);
}
