package com.oauth2demo.basicsecurity.repository;

import com.oauth2demo.basicsecurity.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository  extends JpaRepository<RoleEntity, Long> {
}
