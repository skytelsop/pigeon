package com.example.pigeon.repository;

import com.example.pigeon.model.NewLocationToken;
import com.example.pigeon.model.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewLocationTokenRepository extends JpaRepository<NewLocationToken, Long> {
    NewLocationToken findByToken(String token);
    NewLocationToken findByUserLocation(UserLocation userLocation);

}
