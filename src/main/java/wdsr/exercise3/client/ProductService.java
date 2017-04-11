package wdsr.exercise3.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import wdsr.exercise3.model.Product;
import wdsr.exercise3.model.ProductType;

public class ProductService extends RestClientBase {
	protected ProductService(final String serverHost, final int serverPort, final Client client) {
		super(serverHost, serverPort, client);
	}
	
	/**
	 * Looks up all products of given types known to the server.
	 * @param types Set of types to be looked up
	 * @return A list of found products - possibly empty, never null.
	 */
	public List<Product> retrieveProducts(Set<ProductType> types) {
		Invocation.Builder request = baseTarget.path("products")
				.queryParam("type", types)
				.request(MediaType.APPLICATION_JSON);
		Response response = request.get();
				
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			return request.get(new GenericType<List<Product>>() {});
		} 
		return new ArrayList<Product>();
		/*
		WebTarget target = baseTarget.path("products");
		for (ProductType type : types) {
			target = target.queryParam("type", type);
		}
		Response response = target.request().get();
		
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			return target.request().get(new GenericType<List<Product>>() {});
		}
		
		if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
			return new ArrayList<Product>();
		}
		
		return new ArrayList<Product>(); */
	} 
	
	/**
	 * Looks up all products known to the server.
	 * @return A list of all products - possibly empty, never null.
	 */
	public List<Product> retrieveAllProducts() {
		List<Product> products = baseTarget.path("products")
				.request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Product>>() {});
		
		return products;
	}
	
	/**
	 * Looks up the product for given ID on the server.
	 * @param id Product ID assigned by the server
	 * @return Product if found
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public Product retrieveProduct(int id) {
		WebTarget target = baseTarget.path("products").path(String.valueOf(id));
		Invocation.Builder request = target.request(MediaType.APPLICATION_JSON);
		Response response = request.get();
		
		if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode())
		{
			throw new NotFoundException();
		}
		return response.readEntity(Product.class);
	}	
	
	/**
	 * Creates a new product on the server.
	 * @param product Product to be created. Must have null ID field.
	 * @return ID of the new product.
	 * @throws WebApplicationException if request to the server failed
	 */
	public int storeNewProduct(Product product) {
		Response response = baseTarget.path("products")
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(product, MediaType.APPLICATION_JSON),Response.class);
		
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			return response.readEntity(Product.class).getId();
		} 
		
		throw new WebApplicationException();
	}
	
	/**
	 * Updates the given product.
	 * @param product Product with updated values. Its ID must identify an existing resource.
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public void updateProduct(Product product) {
		WebTarget target = baseTarget.path("/products").path(String.valueOf(product.getId()));
		Invocation.Builder request = target.request();
		Response response = request.put(Entity.entity(product, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
			throw new NotFoundException();
		}
	}

	
	/**
	 * Deletes the given product.
	 * @param product Product to be deleted. Its ID must identify an existing resource.
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public void deleteProduct(Product product) {
		WebTarget target = baseTarget.path("/products").path(String.valueOf(product.getId()));
		Invocation.Builder request = target.request();
		Response response = request.delete();
		
		if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
			throw new NotFoundException();
		}
	}
}
