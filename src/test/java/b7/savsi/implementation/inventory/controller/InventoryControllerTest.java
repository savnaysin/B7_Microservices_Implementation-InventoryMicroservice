package b7.savsi.implementation.inventory.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import b7.savsi.implementation.inventory.entity.Inventory;
import b7.savsi.implementation.inventory.repository.InventoryRepository;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@WebMvcTest(value = InventoryController.class, secure = false)
public class InventoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	Inventory mockInventoryInfo;
	@MockBean
	InventoryRepository mockInventoryRepository;

	@Test
	public void testGetInventoryInfoSuccess() throws Exception {
		Inventory mockInventoryInfo = new Inventory(1003, 20);
		Mockito.when(mockInventoryRepository.findByProductIdOrderByUpdatedOn(Mockito.anyInt()))
				.thenReturn(mockInventoryInfo);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/inventory/inventoryInfo/1003")
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expectedInventoryJson = "{\"productId\": 1003,\"quantity\": 20}";

		JSONAssert.assertEquals(expectedInventoryJson, result.getResponse().getContentAsString(), false);
	}

	@Test
	public void testGetInventoryInfoNotFound() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/inventory/inventoryInfo/1004")
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	public void testGetInventoryInfoListSuccess() throws Exception {
		Mockito.when(mockInventoryRepository.findByProductIdOrderByUpdatedOn(Mockito.anyInt()))
				.thenReturn(new Inventory(1003, 20)).thenReturn(new Inventory(1004, 30));
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/inventory/inventoryInfoList/1003,1004")
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expectedInventoryJson = "[{\"productId\": 1003,\"quantity\":20},{\"productId\": 1004,\"quantity\":30}]";

		JSONAssert.assertEquals(expectedInventoryJson, result.getResponse().getContentAsString(), false);
	}

	@Test
	public void testGetInventoryInfoListNotFound() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/inventory/inventoryInfoList/1005,1006")
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	public void testAddInventoryInfoSuccess() throws Exception {
		String inventoryJson = "{\"productId\": 1002,\"quantity\": 10}";
		Mockito.when(mockInventoryRepository.save(Mockito.any(Inventory.class))).thenReturn(new Inventory(1002, 10));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/inventory/addInventory")
				.accept(MediaType.APPLICATION_JSON).content(inventoryJson).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals("testCreateNewAccount:TC1", HttpStatus.CREATED.value(), response.getStatus());

	}


	@Test
	public void testAddInventoryInfoNoContent() throws Exception {
		String inventoryJson = "{\"productId\": 1002,\"quantity\": 10}";
		Mockito.when(mockInventoryRepository.save(Mockito.any(Inventory.class))).thenReturn(null);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/inventory/addInventory")
				.accept(MediaType.APPLICATION_JSON).content(inventoryJson).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
	}
}
