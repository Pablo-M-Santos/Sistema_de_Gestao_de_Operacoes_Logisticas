package br.com.logicore.modules.address.repository;

import br.com.logicore.modules.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;



public interface AddressRepository extends JpaRepository<Address, Long> {

}