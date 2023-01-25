package com.example.pigeon.repository;

import com.example.pigeon.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository  extends JpaRepository<Privilege, Long> {
    Privilege findByName(String name);
    @Override
    void delete(Privilege privilege);
}
