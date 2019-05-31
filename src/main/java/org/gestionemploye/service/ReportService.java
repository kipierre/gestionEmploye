package org.gestionemploye.service;


import org.gestionemploye.dto.LeaveReportDTO;
import org.gestionemploye.repository.LeaveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final LeaveRepository leaveRepository;

    public ReportService(final LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }


    public List<LeaveReportDTO> retrieveLeaveReports() {
        return leaveRepository.generateLeaveReport();
    }
}
