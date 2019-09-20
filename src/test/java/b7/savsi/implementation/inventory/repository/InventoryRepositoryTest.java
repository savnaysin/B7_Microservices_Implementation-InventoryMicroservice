package b7.savsi.implementation.inventory.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import b7.savsi.implementation.inventory.entity.Inventory;

@RunWith(SpringRunner.class)
@DataJpaTest
public class InventoryRepositoryTest {

	@Autowired
	private InventoryRepository priceRepository;
	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void testFindByProductIdOrderByUpdatedOn() {
		Inventory newInventory = new Inventory(1001, 20);
		Integer savedProductId = testEntityManager.persist(newInventory).getProductId();
		testEntityManager.flush();
		assertEquals("testFindById:TC1", new Integer(20),
				priceRepository.findByProductIdOrderByUpdatedOn(savedProductId).getQuantity());
		assertEquals("testFindById:TC2", null, priceRepository.findByProductIdOrderByUpdatedOn(123));
	}

	@Test
	public void testSave() {
		Inventory priceFound = priceRepository.save(new Inventory(1001, 10));
		assertThat(priceFound).hasFieldOrPropertyWithValue("quantity", 10);
		assertThat(priceFound).hasFieldOrPropertyWithValue("productId", 1001);

	}

}
