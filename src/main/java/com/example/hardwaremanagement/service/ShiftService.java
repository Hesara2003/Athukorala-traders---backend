package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.Shift;
import com.example.hardwaremanagement.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository repository;

    public ShiftService(ShiftRepository repository) {
        this.repository = repository;
    }

    public Shift clockIn(String staffId) {
        Shift s = new Shift();
        s.setStaffId(staffId);
        s.setStartTime(LocalDateTime.now());
        return repository.save(s);
    }

    public Shift clockOut(String shiftId) {
        Shift s = repository.findById(shiftId).orElseThrow(() -> new RuntimeException("Shift not found: " + shiftId));
        s.setEndTime(LocalDateTime.now());
        return repository.save(s);
    }

    public Shift getCurrentForStaff(String staffId) {
        return repository.findByStaffIdOrderByStartTimeDesc(staffId).stream().findFirst().orElse(null);
    }

    public List<Shift> history(String staffId) {
        return repository.findByStaffIdOrderByStartTimeDesc(staffId);
    }

    public List<Shift> listAll() { return repository.findAll(); }
}
