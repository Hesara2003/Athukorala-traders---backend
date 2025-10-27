package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.Shift;
import com.example.hardwaremanagement.service.ShiftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@CrossOrigin(origins = "http://localhost:5173")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping("/clock-in")
    public ResponseEntity<?> clockIn(@RequestParam String staffId) {
        if (staffId == null || staffId.isBlank()) return ResponseEntity.badRequest().body("staffId required");
        return ResponseEntity.ok(shiftService.clockIn(staffId));
    }

    @PostMapping("/clock-out")
    public ResponseEntity<?> clockOut(@RequestParam String shiftId) {
        if (shiftId == null || shiftId.isBlank()) return ResponseEntity.badRequest().body("shiftId required");
        try {
            return ResponseEntity.ok(shiftService.clockOut(shiftId));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @GetMapping("/current/{staffId}")
    public ResponseEntity<?> current(@PathVariable String staffId) {
        return ResponseEntity.ok(shiftService.getCurrentForStaff(staffId));
    }

    @GetMapping("/history/{staffId}")
    public ResponseEntity<List<Shift>> history(@PathVariable String staffId) {
        return ResponseEntity.ok(shiftService.history(staffId));
    }

    @GetMapping
    public ResponseEntity<List<Shift>> list() { return ResponseEntity.ok(shiftService.listAll()); }
}
