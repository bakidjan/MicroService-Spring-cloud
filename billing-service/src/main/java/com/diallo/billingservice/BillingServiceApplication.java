package com.diallo.billingservice;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

	@Entity @Data @AllArgsConstructor @NoArgsConstructor @ToString
	class Billing{
	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 /*
	 Temporoly permet de personaliser la date
	  */
	 @Temporal(TemporalType.DATE)
	 private Date billingDate;
	 private Long customerID;
	 @OneToMany(mappedBy = "billing")
	 private Collection<ProductItem> productItems;
	}
	@RepositoryRestResource
	interface BillingRepository extends JpaRepository<Billing, Long>{}

	@Entity @Data @AllArgsConstructor @NoArgsConstructor @ToString
	class ProductItem{
		@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
		private Long productID;
		private double quantity;
		private double price;
		@ManyToOne
		private Billing billing;
	}
	@RepositoryRestResource
	interface ProductItemRepository extends JpaRepository<ProductItem, Long>{}

	/*
	Cette projection retourne les infos de la facture avec
	la liste des produit assiciés
	 */
	@Projection(name = "fullBilling", types = Billing.class)
	interface BillingProjection{
		public Long getId();
		public Date getBillingDate();
		public Long getCustomerID();
		public Collection<ProductItem> getProductItems();

	}

/**
 * cette class permet de serialiser et deserialiser des infos customers
 * sans etre persistant dans la db de billing en utilisant la db distante de customer
 */
	@Data
	class Customer{
		private Long id;
		private String name;
		private String address;
		private String email;
	}
/**
 * FeignClient permet de contacter le service customer à distance et la
 * methode findCustomerById() envoie une req get sur "customer-service/customers/{id}"
 * Ne pas oublier d'activer "@EnableFeignClients" au niveau de l'application
 */
@FeignClient(name = "customer-service")
	interface CustomerService{
		@GetMapping ("/customers/{id}")
		public Customer findCustomerById(@PathVariable(name = "id") Long id);
	}

	@Data
	class Product{
		private Long id;
		private String name;
		private int quantity;
		private double price;
	}
	@FeignClient(name = "product-service")
	interface ProductService{
	@GetMapping("/products/{id}")
		public Product findProductById(@PathVariable(name = "id") Long id);
	}

	@SpringBootApplication
	@EnableFeignClients
	public class BillingServiceApplication implements CommandLineRunner{
		@Autowired
		BillingRepository billingRepository;
		@Autowired
		ProductItemRepository productItemRepository;
		@Autowired
		RepositoryRestConfiguration restConfiguration;
		@Autowired
		CustomerService customerService;
		@Autowired
		ProductService productService;

		public static void main(String[] args) {
			SpringApplication.run(BillingServiceApplication.class, args);
		}

		//@Bean
		CommandLineRunner start(BillingRepository billingRepository, ProductItemRepository productItemRepository,
								RepositoryRestConfiguration restConfiguration, CustomerService customerService,
								ProductService productService){
			return args -> {
				/*
				restConfiguration.exposeIdsFor(Billing.class);
				Billing bill1 = billingRepository.save(new Billing(null, new Date(), 1L, null));
				Customer c1 = customerService.findCustomerById(1L);
				System.out.println("*************info Customer*******************");
				System.out.println("ID = "+c1.getId()+ ", Name = "+c1.getName()+ ", Adresse = "+c1.getAddress()+ ", Email = "+c1.getEmail());

				Product p1 = productService.findProductById(1L);
				System.out.println("*************info Product*******************");
				System.out.println("ProductID = "+p1.getId()+ ", ProductName = "+p1.getName()+ ", ProductPrice = "+p1.getPrice());

				ProductItem pIt = productItemRepository.save(new ProductItem(null, p1.getId(), 20, 900, bill1));
				productItemRepository.save(new ProductItem(null, 2L, 32, 400, bill1));
				productItemRepository.save(new ProductItem(null, 3L, 67, 600, bill1));
				System.out.println("*************info ProductItems*******************");
				System.out.println(pIt);
				*/
			};
		}

		@Override
		public void run(String... args) throws Exception {
			restConfiguration.exposeIdsFor(Billing.class);
			Billing bill1 = billingRepository.save(new Billing(null, new Date(), 1L, null));
			Customer c1 = customerService.findCustomerById(1L);
			System.out.println("*************info Customer*******************");
			System.out.println("ID = "+c1.getId()+ ", Name = "+c1.getName()+ ", Adresse = "+c1.getAddress()+ ", Email = "+c1.getEmail());

			Product p1 = productService.findProductById(1L);
			System.out.println("*************info Product*******************");
			System.out.println("ProductID = "+p1.getId()+ ", ProductName = "+p1.getName()+ ", ProductPrice = "+p1.getPrice());

			productItemRepository.save(new ProductItem(null, p1.getId(), p1.getQuantity(), p1.getPrice(), bill1));
			Product p2 = productService.findProductById(2L);
			productItemRepository.save(new ProductItem(null, p2.getId(), p2.getQuantity(), p2.getPrice(), bill1));
			Product p3 = productService.findProductById(3L);
			productItemRepository.save(new ProductItem(null, p3.getId(), p3.getQuantity(), p3.getPrice(), bill1));
			System.out.println("*************info ProductItems*******************");
			//System.out.println(pIt1);

		}
	}
