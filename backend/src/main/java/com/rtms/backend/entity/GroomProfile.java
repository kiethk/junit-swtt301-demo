package com.rtms.backend.entity;

import com.rtms.backend.enums.GroomShift;
import jakarta.persistence.*;

@Entity
@Table(name = "groom_profiles")
public class GroomProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;


    @Enumerated(EnumType.STRING)
    @Column(name = "shift", length = 50)
    private GroomShift shift;

    public Long getUserId() {
        return userId;
    }

    @Column(name = "trainer_id")
    private Long trainerId;

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public GroomShift getShift() {
        return shift;
    }

    public void setShift(GroomShift shift) {
        this.shift = shift;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }
}
