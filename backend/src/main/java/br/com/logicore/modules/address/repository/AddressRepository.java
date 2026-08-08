package br.com.logicore.modules.address.repository;

import br.com.logicore.modules.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {

	@SuppressWarnings("unused")
	@Query("select count(a) from Address a where a.latitude is not null and a.longitude is not null")
	long countWithCoordinates();

}