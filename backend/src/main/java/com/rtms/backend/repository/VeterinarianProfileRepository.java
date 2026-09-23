package com.rtms.backend.repository;

import com.rtms.backend.entity.VeterinarianProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarianProfileRepository extends JpaRepository<VeterinarianProfile, Long> {
}
