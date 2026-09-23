package com.rtms.backend.service;

import com.rtms.backend.dto.CreatePrescriptionRequest;
import com.rtms.backend.dto.CreateTreatmentPlanRequest;
import com.rtms.backend.dto.PrescriptionResponse;
import com.rtms.backend.dto.TreatmentPlanResponse;
import com.rtms.backend.entity.Prescription;
import com.rtms.backend.entity.TreatmentPlan;
import com.rtms.backend.repository.HealthRecordRepository;
import com.rtms.backend.repository.PrescriptionRepository;
import com.rtms.backend.repository.TreatmentPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class TreatmentPlanService {
    private final HealthRecordRepository healthRecordRepository;
    private final TreatmentPlanRepository treatmentPlanRepository;
    private final PrescriptionRepository prescriptionRepository;

    public TreatmentPlanService(
            HealthRecordRepository healthRecordRepository,
            TreatmentPlanRepository treatmentPlanRepository,
            PrescriptionRepository prescriptionRepository)
    {
        this.healthRecordRepository = healthRecordRepository;
        this.treatmentPlanRepository = treatmentPlanRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public TreatmentPlanResponse createTreatmentPlan(
            Long healthRecordId,
            CreateTreatmentPlanRequest request
    ) {
        healthRecordRepository.findById(healthRecordId).orElseThrow(() -> new RuntimeException("Health Record Not Found"));

        validateDates(
                request.getStartDate(),
                request.getEndDate()
        );

        TreatmentPlan plan = new TreatmentPlan();
        plan.setHealthRecordId(healthRecordId);
        plan.setTreatmentName(request.getTreatmentName());
        plan.setInstructions(request.getInstructions());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());

        return toTreatmentPlanResponse(treatmentPlanRepository.save(plan));
    }

    @Transactional
    public PrescriptionResponse createPrescription(
            Long treatmentPlanId,
            CreatePrescriptionRequest request
    ) {
        TreatmentPlan plan = treatmentPlanRepository.findById(treatmentPlanId).orElseThrow(() -> new RuntimeException("Treatment plan not found"));

        validateDates(request.getStartDate(), request.getEndDate());

        Prescription prescription = new Prescription();

        prescription.setTreatmentPlanId(plan.getId());
        prescription.setMedicationName(request.getMedicationName());
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setRoute(request.getRoute());
        prescription.setStartDate(request.getStartDate());
        prescription.setEndDate(request.getEndDate());
        prescription.setInstructions(request.getInstructions());

        Prescription saved = prescriptionRepository.save(prescription);

        return toPrescriptionResponse(saved);
    }

    public List<TreatmentPlanResponse> getByHealthRecordId(Long healthRecordId) {
        healthRecordRepository.findById(healthRecordId).orElseThrow(() -> new RuntimeException("Health Record Not Found"));
        return treatmentPlanRepository
                .findByHealthRecordId(healthRecordId)
                .stream()
                .map(this::toTreatmentPlanResponse)
                .toList();
    }

    private TreatmentPlanResponse toTreatmentPlanResponse(TreatmentPlan plan) {
        List<PrescriptionResponse> prescriptions =
                prescriptionRepository
                        .findByTreatmentPlanId(plan.getId())
                        .stream()
                        .map(this::toPrescriptionResponse)
                        .toList();
        return new TreatmentPlanResponse(
                plan.getId(),
                plan.getHealthRecordId(),
                plan.getTreatmentName(),
                plan.getInstructions(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getStatus(),
                prescriptions
        );
    }

    private PrescriptionResponse toPrescriptionResponse(Prescription prescription) {
        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getRoute(),
                prescription.getStartDate(),
                prescription.getEndDate(),
                prescription.getInstructions()
        );
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new RuntimeException("Start date cannot be after end date");
        }
    }
}
