//package com.example.demo.controller;
//
//import com.example.demo.dto.CustomerRequestDTO;
//import com.example.demo.dto.CustomerResponseDTO;
//import com.example.demo.dto.LoginDTO;
//import com.example.demo.entity.Customer;
//import com.example.demo.jwt.JwtUtil;
//import com.example.demo.service.CustomerService;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.security.core.Authentication;
//
//import java.util.Arrays;
//
//import static org.mockito.Mockito.*;
//import static org.assertj.core.api.Assertions.*;
//
//public class CustomerControllerTest {
//
//CustomerService service =
//Mockito.mock(CustomerService.class);
//
//JwtUtil jwtUtil =
//Mockito.mock(JwtUtil.class);
//
///* Using Constructor Injection */
//CustomerController controller =
//new CustomerController(service, jwtUtil);
//
//
///*
//Register Customer Test
//*/
//@Test
//public void testRegisterCustomer(){
//
//CustomerRequestDTO dto =
//new CustomerRequestDTO();
//
//dto.setUsername("testuser");
//
//CustomerResponseDTO response =
//new CustomerResponseDTO();
//
//response.setUsername("testuser");
//
//when(service.registerCustomer(dto))
//.thenReturn(response);
//
//CustomerResponseDTO result =
//controller.registerCustomer(dto);
//
//assertThat(result.getUsername())
//.isEqualTo("testuser");
//
//}
//
//
//
///*
//Login Customer Test
//*/
//@Test
//public void testLoginCustomer(){
//
//LoginDTO dto =
//new LoginDTO();
//
//dto.setUsername("user");
//dto.setPassword("123");
//
//Customer c = new Customer();
//
//c.setUsername("user");
//c.setRole("CUSTOMER");
//
//when(service.loginCustomer(
//"user","123"))
//.thenReturn(c);
//
//when(jwtUtil.generateToken(
//"user","CUSTOMER"))
//.thenReturn("token123");
//
//String token =
//controller.loginCustomer(dto);
//
//assertThat(token)
//.isEqualTo("token123");
//
//}
//
//
//
///*
//Get Customer By Id Test
//*/
//@Test
//public void testGetCustomerById(){
//
//CustomerResponseDTO dto =
//new CustomerResponseDTO();
//
//dto.setAccountNo(1L);
//dto.setUsername("user");
//
//when(service.getCustomerById(1L))
//.thenReturn(dto);
//
//Authentication auth =
//Mockito.mock(Authentication.class);
//
//when(auth.getName())
//.thenReturn("user");
//
//CustomerResponseDTO result =
//controller.getCustomerById(1L,auth);
//
//assertThat(result.getAccountNo())
//.isEqualTo(1L);
//
//}
//
//
//
///*
//List Customers Test (Admin)
//*/
//@Test
//public void testListCustomers(){
//
//CustomerResponseDTO dto =
//new CustomerResponseDTO();
//
//dto.setUsername("admin");
//
//when(service.getAllCustomers())
//.thenReturn(java.util.Arrays.asList(dto));
//
//Authentication auth =
//Mockito.mock(Authentication.class);
//
///* Cast directly inside Mockito */
//when(auth.getAuthorities())
//.thenAnswer(invocation ->
//(java.util.Collection)
//java.util.Collections.singletonList(
//new org.springframework.security.core.authority.SimpleGrantedAuthority("ADMIN")
//));
//
//assertThat(
//controller.getAllCustomers(auth).size())
//.isEqualTo(1);
//
//}
///*
//Delete Customer Test (Admin)
//*/
//@Test
//public void testDeleteCustomer(){
//
//CustomerResponseDTO dto =
//new CustomerResponseDTO();
//
//dto.setUsername("admin");
//
//when(service.getCustomerById(1L))
//.thenReturn(dto);
//
//doNothing()
//.when(service)
//.deleteCustomer(1L);
//
//Authentication auth =
//Mockito.mock(Authentication.class);
//
//when(auth.getName())
//.thenReturn("admin");
//
//String result =
//controller.deleteCustomer(1L,auth);
//
//assertThat(result)
//.isEqualTo(
//"Customer Deleted Successfully");
//
//}
//
//}