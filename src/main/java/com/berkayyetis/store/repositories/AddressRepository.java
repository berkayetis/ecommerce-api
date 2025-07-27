package com.berkayyetis.store.repositories;

import com.berkayyetis.store.entities.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}