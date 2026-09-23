package com.rtms.backend.service;

import com.rtms.backend.dto.CreateHorsePedigreeRequest;
import com.rtms.backend.dto.HorsePedigreeResponse;
import com.rtms.backend.dto.HorseSummaryResponse;
import com.rtms.backend.entity.Horse;
import com.rtms.backend.entity.HorsePedigree;
import com.rtms.backend.repository.HorsePedigreeRepository;
import com.rtms.backend.repository.HorseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class HorsePedigreeService {
    private final HorsePedigreeRepository horsePedigreeRepository;
    private final HorseRepository horseRepository;

    public HorsePedigreeService(HorsePedigreeRepository horsePedigreeRepository, HorseRepository horseRepository) {
        this.horsePedigreeRepository = horsePedigreeRepository;
        this.horseRepository = horseRepository;
    }

    public HorsePedigreeResponse getByHorseId(Long horseId) {
        Horse horse = horseRepository.findById(horseId).orElseThrow(() -> new RuntimeException("Horse not found"));

        HorsePedigree pedigree = horsePedigreeRepository.findByHorseId(horseId)
                .orElseThrow(() -> new RuntimeException("Pedigree not found"));

        Horse sire = null;
        Horse dam = null;

        if (pedigree.getSireId() != null) {
            sire = horseRepository.findById(pedigree.getSireId()).orElse(null);

        }

        if (pedigree.getDamId() != null) {
            dam = horseRepository.findById(pedigree.getDamId()).orElse(null);

        }

        return toResponse(pedigree, horse, sire, dam);
    }

    @Transactional
    public HorsePedigreeResponse createOrUpdate(Long horseId, CreateHorsePedigreeRequest request) {
        Horse horse = horseRepository.findById(horseId).orElseThrow(() -> new RuntimeException("Horse not found"));

        validateParents(horseId, request);

        Horse sire = null;
        Horse dam = null;

        if (request.getSireId() != null) {
            sire = horseRepository.findById(request.getSireId())
                    .orElseThrow(() -> new RuntimeException("Sire not found"));
        }

        if (request.getDamId() != null) {
            dam = horseRepository.findById(request.getDamId()).orElseThrow(() -> new RuntimeException("Dam not found"));
        }

        HorsePedigree pedigree = horsePedigreeRepository.findByHorseId(horseId).orElseGet(HorsePedigree::new);

        pedigree.setHorseId(horseId);
        pedigree.setSireId(request.getSireId());
        pedigree.setDamId(request.getDamId());
        pedigree.setPedigreeNotes(request.getPedigreeNotes());

        horse.setRegistrationNumber(request.getRegistrationNumber());
        horse.setRegistryName(request.getRegistryName());
        horse = horseRepository.save(horse);

        HorsePedigree saved = horsePedigreeRepository.save(pedigree);

        return toResponse(saved, horse, sire, dam);
    }

    private void validateParents(Long horseId, CreateHorsePedigreeRequest request) {
        if (Objects.equals(horseId, request.getSireId())) {
            throw new RuntimeException("Horse cannot be its own sire");
        }

        if (Objects.equals(horseId, request.getDamId())) {
            throw new RuntimeException("Horse cannot be its own dam");
        }

        if (request.getSireId() != null &&
                Objects.equals(request.getSireId(), request.getDamId())) {
            throw new RuntimeException("Sire and dam cannot be the same horse");
        }
    }

    private HorsePedigreeResponse toResponse(HorsePedigree pedigree, Horse horse, Horse sire, Horse dam) {
        return new HorsePedigreeResponse(
                pedigree.getId(),
                toSummary(horse),
                toSummary(sire),
                toSummary(dam),
                horse.getRegistrationNumber(),
                horse.getRegistryName(),
                pedigree.getPedigreeNotes());
    }

    private HorseSummaryResponse toSummary(Horse horse) {
        if (horse == null) {
            return null;
        }

        return new HorseSummaryResponse(
                horse.getId(),
                horse.getName(),
                horse.getBreed(),
                horse.getDateOfBirth(),
                horse.getCurrentStatus().name());
    }
}
