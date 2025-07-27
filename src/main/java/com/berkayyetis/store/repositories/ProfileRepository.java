package com.berkayyetis.store.repositories;

import com.berkayyetis.store.entities.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}