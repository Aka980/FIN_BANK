package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.dto.CustomerRequestDTO;
import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;

public class CustomerServiceTest {

//    @Mock
//    CustomerRepository repository;
//
//    @Mock
//    BCryptPasswordEncoder encoder;
//
//    @InjectMocks
//    CustomerService service;
//
//    public CustomerServiceTest() {
//
//        MockitoAnnotations.openMocks(this);
//
//    }
//    @Test
//    public void testRegisterCustomer(){
//
//    CustomerRequestDTO dto =
//    new CustomerRequestDTO();
//
//    dto.setCustomerName("Test");
//    dto.setEmail("test@gmail.com");
//    dto.setPhone("9999999999");
//    dto.setAddress("Bangalore");
//    dto.setAccountType("SAVINGS");
//    dto.setUsername("testuser");
//    dto.setPassword("1234");
//
//    when(repository.findByUsername("testuser"))
//    .thenReturn(null);
//
//    when(encoder.encode("1234"))
//    .thenReturn("encrypted");
//
//    Customer customer=new Customer();
//
//    customer.setAccountNo(1L);
//    customer.setUsername("testuser");
//
//    when(repository.save(any(Customer.class)))
//    .thenReturn(customer);
//
//    assertNotNull(
//    service.registerCustomer(dto));
//
//    }
//    
//    @Test
//    public void testLoginCustomer(){
//
//    Customer customer=new Customer();
//
//    customer.setUsername("testuser");
//
//    customer.setPassword("encrypted");
//
//    when(repository.findByUsername("testuser"))
//    .thenReturn(customer);
//
//    when(encoder.matches(
//    "1234",
//    "encrypted"))
//    .thenReturn(true);
//
//    Customer result=
//    service.loginCustomer(
//    "testuser",
//    "1234");
//
//    assertEquals(
//    "testuser",
//    result.getUsername());
//
//    }
//    
//    @Test
//    public void testGetCustomerById(){
//
//    Customer customer=new Customer();
//
//    customer.setAccountNo(1L);
//    customer.setCustomerName("Test User");
//    customer.setEmail("test@gmail.com");
//    customer.setPhone("9999999999");
//    customer.setAddress("Bangalore");
//    customer.setAccountType("SAVINGS");
//    customer.setUsername("testuser");
//    customer.setRole("CUSTOMER");
//
//    when(repository.findById(1L))
//    .thenReturn(java.util.Optional.of(customer));
//
//    assertEquals(
//    "Test User",
//    service.getCustomerById(1L)
//    .getCustomerName());
//
//    }
//    
//    @Test
//    public void testCustomerNotFound(){
//
//    when(repository.findById(99L))
//    .thenReturn(java.util.Optional.empty());
//
//    assertThrows(
//
//    com.example.demo.exception.CustomerNotFoundException.class,
//
//    ()-> service.getCustomerById(99L)
//
//    );
//
//    }
//    @Test
//    public void testUpdateCustomer(){
//
//    Customer customer=new Customer();
//
//    customer.setAccountNo(1L);
//    customer.setCustomerName("Old Name");
//
//    CustomerRequestDTO dto=
//    new CustomerRequestDTO();
//
//    dto.setCustomerName("New Name");
//    dto.setEmail("new@gmail.com");
//    dto.setPhone("9999999999");
//    dto.setAddress("Chennai");
//    dto.setAccountType("SAVINGS");
//
//    when(repository.findById(1L))
//    .thenReturn(java.util.Optional.of(customer));
//
//    when(repository.save(customer))
//    .thenReturn(customer);
//
//    assertEquals(
//
//    "New Name",
//
//    service.updateCustomer(dto,1L)
//    .getCustomerName()
//
//    );
//
//    }
//    @Test
//    public void testDeleteCustomer(){
//
//    Customer customer = new Customer();
//
//    customer.setAccountNo(1L);
//
//    when(repository.findById(1L))
//    .thenReturn(java.util.Optional.of(customer));
//
//    doNothing().when(repository)
//    .delete(customer);
//
//    service.deleteCustomer(1L);
//
//    verify(repository,
//    times(1))
//    .delete(customer);
//
//    }

}