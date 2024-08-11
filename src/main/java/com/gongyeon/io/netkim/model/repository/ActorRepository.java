package com.gongyeon.io.netkim.model.repository;

import com.gongyeon.io.netkim.model.entity.ActorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<ActorEntity, Integer> {
}
