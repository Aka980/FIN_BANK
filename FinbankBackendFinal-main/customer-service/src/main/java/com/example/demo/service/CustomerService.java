package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CustomerRequestDTO;
import com.example.demo.dto.CustomerResponseDTO;
import com.example.demo.entity.Customer;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.exception.DuplicateUserException;
import com.example.demo.repository.CustomerRepository;

/*
 * CustomerService
 * Production Ready
 */

@Service
public class CustomerService {

				@Autowired
				private CustomerRepository repository;
				
				@Autowired
				private BCryptPasswordEncoder encoder;
				
				@Autowired
				EmailService emailService;
				
				
				/*
				Register Customer
				*/
				public CustomerResponseDTO registerCustomer(CustomerRequestDTO dto){
				
				Customer existingUser =
				repository.findByUsername(dto.getUsername());
				
				if(existingUser!=null){
				
				throw new DuplicateUserException(
				"Username already exists");
				}
				
				
				Customer existingEmail =
				repository.findByEmail(dto.getEmail());
				
				if(existingEmail!=null){
				
				throw new DuplicateUserException(
				"Email already exists");
				}
				
				
				Customer customer = new Customer();
				
				customer.setCustomerName(
				dto.getCustomerName());
				
				customer.setUsername(
				dto.getUsername());
				
				customer.setEmail(
				dto.getEmail());
				
				customer.setPhone(
				dto.getPhone());
				
				customer.setPassword(
				encoder.encode(
				dto.getPassword()));
				
				customer.setRole("CUSTOMER");
				
				customer.setCreatedDate(
				LocalDate.now());
				
				
				Customer saved=
				repository.save(customer);
				
				 emailService.sendRegistrationMail(
			                saved.getEmail(),
			                saved.getAccountNo()
			        );

				
				return convertToDTO(saved);
				
				}
			
				
				/*
				Login Customer
				*/
				public Customer loginCustomer(
				String username,
				String password){
				
				Customer customer =
				repository.findByUsername(username);
				
				if(customer==null){
				
				throw new CustomerNotFoundException(
				"Username not found");
				}
				
				if(!encoder.matches(
				password,
				customer.getPassword())){
				
				throw new RuntimeException(
				"Invalid Password");
				}
				
				return customer;
				
				}
				
				
				/*
				Get Customer By ID
				*/
				public CustomerResponseDTO getCustomerById(
				Long id){
				
				Customer customer=
				repository.findById(id)
				.orElseThrow(()->
				new CustomerNotFoundException(
				"Customer Not Found"));
				
				return convertToDTO(customer);
				
				}
				
				
				/*
				Get Customer By Username
				*/
				public CustomerResponseDTO
				getCustomerByUsernameDTO(
				String username){
				
				Customer customer=
				repository.findByUsername(username);
				
				if(customer==null){
				
				throw new CustomerNotFoundException(
				"Customer Not Found");
				}
				
				return convertToDTO(customer);
				
				}
				
				
				/*
				Admin Only
				*/
				public List<CustomerResponseDTO>
				getAllCustomers(){
				
				return repository.findAll()
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
				
				}
				/*
				 * Update Customer
				 * Only basic details are updated
				 */
				public CustomerResponseDTO updateCustomer(

				        CustomerRequestDTO dto,

				        Long accountNo){

				    Customer customer =
				            repository.findById(accountNo)
				            .orElseThrow(() ->
				                    new CustomerNotFoundException(
				                            "Customer Not Found"));


				    /*
				     * Check Username Duplicate
				     */
				    Customer existingUser =
				            repository.findByUsername(
				                    dto.getUsername());

				    if(existingUser!=null &&
				       !existingUser.getAccountNo()
				       .equals(accountNo)){

				        throw new DuplicateUserException(
				                "Username already exists");
				    }


				    /*
				     * Check Email Duplicate
				     */
				    Customer existingEmail =
				            repository.findByEmail(
				                    dto.getEmail());

				    if(existingEmail!=null &&
				       !existingEmail.getAccountNo()
				       .equals(accountNo)){

				        throw new DuplicateUserException(
				                "Email already exists");
				    }


				    customer.setCustomerName(
				            dto.getCustomerName());

				    customer.setUsername(
				            dto.getUsername());

				    customer.setEmail(
				            dto.getEmail());

				    customer.setPhone(
				            dto.getPhone());


				    if(dto.getPassword()!=null &&
				       !dto.getPassword().isEmpty()){

				        customer.setPassword(
				                encoder.encode(
				                        dto.getPassword()));
				    }


				    Customer updated =
				            repository.save(customer);

				    return convertToDTO(updated);

				}
				
				/*
				Delete Customer
				*/
				public void deleteCustomer(Long id){
				
				Customer customer=
				repository.findById(id)
				.orElseThrow(()->
				new CustomerNotFoundException(
				"Customer Not Found"));
				
				repository.delete(customer);
				
				}
				
				
				/*
				Entity → DTO
				*/
				private CustomerResponseDTO convertToDTO(
				Customer customer){
				
				CustomerResponseDTO dto=
				new CustomerResponseDTO();
				
				dto.setAccountNo(
				customer.getAccountNo());
				
				dto.setCustomerName(
				customer.getCustomerName());
				
				dto.setUsername(
				customer.getUsername());
				
				dto.setEmail(
				customer.getEmail());
				
				dto.setPhone(
				customer.getPhone());
				
				dto.setRole(
				customer.getRole());
				
				dto.setCreatedDate(
				customer.getCreatedDate());
				
				return dto;
				
				}

}