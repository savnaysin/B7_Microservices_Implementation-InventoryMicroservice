package b7.savsi.implementation.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import b7.savsi.implementation.inventory.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
	Inventory findByProductIdOrderByUpdatedOn(Integer productId);

	void deleteByProductId(Integer productId);
}
