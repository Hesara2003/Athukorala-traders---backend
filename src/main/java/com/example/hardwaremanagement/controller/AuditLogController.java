package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.AuditLog;
import com.example.hardwaremanagement.repository.AuditLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "http://localhost:5173")
public class AuditLogController {

    private final AuditLogRepository repository;

    public AuditLogController(AuditLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AuditLog> list() {
        return repository.findAll();
    }

    @PostMapping
    public AuditLog create(@RequestBody AuditLog log) {
        return repository.save(log);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        var opt = repository.findById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
    }

    @GetMapping(value = "/export/csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exportCsv() {
        List<AuditLog> all = repository.findAll();
    String header = "id,timestamp,performedBy,action\n";
    String body = all.stream().map(a -> String.format("%s,%s,%s,%s",
        nullable(a.getId()), nullable(a.getTimestamp()), nullable(a.getPerformedBy()), nullable(a.getAction())))
        .collect(Collectors.joining("\n"));
        return header + body;
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<?> exportPdf() {
        // PDF export is not implemented in the MVP — return 501
        Map<String, String> resp = new HashMap<>();
        resp.put("error", "PDF export not implemented");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(resp);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<AuditLog> all = repository.findAll();
    Map<String, Long> byAction = all.stream().collect(Collectors.groupingBy(AuditLog::getAction, Collectors.counting()));
    Map<String, Long> byUser = all.stream().collect(Collectors.groupingBy(AuditLog::getPerformedBy, Collectors.counting()));
        Map<String, Object> resp = new HashMap<>();
        resp.put("total", all.size());
        resp.put("byAction", byAction);
        resp.put("byUser", byUser);
        return resp;
    }

    private String nullable(Object o) {
        return o == null ? "" : o.toString().replaceAll("[\r\n,]", " ");
    }
}
