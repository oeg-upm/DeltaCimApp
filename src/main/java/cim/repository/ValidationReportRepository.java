package cim.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cim.model.ValidationReport;

public interface ValidationReportRepository extends JpaRepository<ValidationReport, Integer>{

}
