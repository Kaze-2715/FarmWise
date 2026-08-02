package com.farmwise.report.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.report.dto.GenerateReportRequest;
import com.farmwise.report.dto.ReportResponse;
import com.farmwise.report.dto.ReportSummaryResponse;
import com.farmwise.report.service.ReportService;
import com.farmwise.security.permission.RequiredPermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
        private final ReportService reportService;

        @GetMapping
        @RequiredPermission("report:read")
        public ResponseEntity<List<ReportSummaryResponse>> listReports(
                        @RequestParam(required = false) String landId,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @RequestParam(required = false) String keyword,
                        Authentication authentication) {
                List<ReportSummaryResponse> response = reportService.listReports(
                                authentication.getName(),
                                landId,
                                type,
                                status,
                                startDate,
                                endDate,
                                keyword);

                return ResponseEntity.ok(response);
        }

        @GetMapping("/{reportId}")
        @RequiredPermission("report:read")
        public ResponseEntity<ReportResponse> getReport(
                        @PathVariable String reportId,
                        Authentication authentication) {
                ReportResponse response = reportService.getReport(
                                authentication.getName(),
                                reportId);

                return ResponseEntity.ok(response);
        }

        @PostMapping("/{reportId}/archive")
        @RequiredPermission("report:archive")
        public ResponseEntity<ReportSummaryResponse> archiveReport(
                        @PathVariable String reportId,
                        Authentication authentication) {
                ReportSummaryResponse response = reportService.archiveReport(
                                authentication.getName(),
                                reportId);

                return ResponseEntity.ok(response);
        }

        @PostMapping
        @RequiredPermission("report:generate")
        public ResponseEntity<ReportResponse> generateReport(
                        @Valid @RequestBody GenerateReportRequest request,
                        Authentication authentication) {
                ReportResponse response = reportService.generateReport(
                                authentication.getName(),
                                request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }
}
