package b7.savsi.implementation.inventory.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import b7.savsi.implementation.inventory.entity.Inventory;
import b7.savsi.implementation.inventory.exception.NotFoundException;
import b7.savsi.implementation.inventory.repository.InventoryRepository;

@RefreshScope
@RestController
public class InventoryController {
	@Autowired
	private Environment env;
	@Autowired
	InventoryRepository inventoryRepository;

	@GetMapping(path = "/inventory/inventoryInfo/{productId}")
	public ResponseEntity<Inventory> getInventoryInfo(@PathVariable("productId") Integer productId)
			throws NotFoundException {
		Inventory inventory = inventoryRepository.findByProductIdOrderByUpdatedOn(productId);
		if (inventory == null)
			throw new NotFoundException("No Inventory Found for the product id:: " + productId);
		return ResponseEntity.ok().body(inventory);
	}

	@GetMapping(path = "/inventory/inventoryInfoList/{productIds}")
	public ResponseEntity<List<Inventory>> getInventoryList(@PathVariable List<Integer> productIds)
			throws NotFoundException {
		List<Inventory> inventoryList = new ArrayList<Inventory>();
		Inventory inventory = null;
		for (Integer productId : productIds) {
			inventory = inventoryRepository.findByProductIdOrderByUpdatedOn(productId);
			if (inventory != null)
				inventoryList.add(inventory);
		}
		if (inventoryList.isEmpty())
			throw new NotFoundException("No Inventory Found for any of the product ids");
		return ResponseEntity.ok().body(inventoryList);
	}

	@DeleteMapping(path = "/inventory/removeInventory/{productId}")
	public Void deleteInventoryInfo(@PathVariable("productId") Integer productId) throws NotFoundException {
		Inventory inventory = inventoryRepository.findByProductIdOrderByUpdatedOn(productId);
		if (inventory == null)
			throw new NotFoundException("No Inventory Found for the product id:: " + productId);
		inventoryRepository.delete(inventory);
		return null;
	}

	@PostMapping(path = "/inventory/addInventory", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Void> addInventoryInfo(@RequestBody Inventory inventory) {
		Inventory newInventory = null;
		if (inventory.getQuantity() != null)
			newInventory = inventoryRepository.save(new Inventory(inventory.getProductId(), inventory.getQuantity()));
		else
			newInventory = inventoryRepository.save(
					new Inventory(inventory.getProductId(), new Integer(env.getProperty("inventory.defaultQuantity"))));

		if (newInventory == null)
			return ResponseEntity.noContent().build();

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newInventory.getInventoryId()).toUri();
		return ResponseEntity.created(location).build();
	}

}
