package com.gongyeon.io.netkim.model.repository;

import com.gongyeon.io.netkim.model.entity.TheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<TheaterEntity, Integer> {
}
