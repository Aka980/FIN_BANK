package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.dto.CustomerRequestDTO;
import com.example.demo.dto.CustomerResponseDTO;
import com.example.demo.entity.Customer;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.exception.DuplicateUserException;
import com.example.demo.exception.InvalidPasswordException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CustomerService service;

    private CustomerRequestDTO testDto;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testDto = new CustomerRequestDTO();
        testDto.setCustomerName("Test User");
        testDto.setEmail("test@gmail.com");
        testDto.setPhone("9999999999");
        testDto.setUsername("testuser");
        testDto.setPassword("password123");

        testCustomer = new Customer();
        testCustomer.setAccountNo(1L);
        testCustomer.setCustomerName("Test User");
        testCustomer.setEmail("test@gmail.com");
        testCustomer.setPhone("9999999999");
        testCustomer.setUsername("testuser");
        testCustomer.setPassword("encodedPassword");
    }

    @Test
    void testRegisterCustomer_Success() {
        when(repository.findByUsername(testDto.getUsername())).thenReturn(null);
        when(encoder.encode(testDto.getPassword())).thenReturn("encodedPassword");
        when(repository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponseDTO response = service.registerCustomer(testDto);

        assertNotNull(response);
        assertEquals(testDto.getUsername(), response.getUsername());
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void testRegisterCustomer_DuplicateUser() {
        when(repository.findByUsername(testDto.getUsername())).thenReturn(testCustomer);

        assertThrows(DuplicateUserException.class, () -> service.registerCustomer(testDto));
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void testRegisterCustomer_DuplicateEmail() {
        when(repository.findByUsername(testDto.getUsername())).thenReturn(null);
        when(repository.findByEmail(testDto.getEmail())).thenReturn(testCustomer);

        assertThrows(DuplicateUserException.class, () -> service.registerCustomer(testDto));
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void testLoginCustomer_Success() {
        when(repository.findByUsername("testuser")).thenReturn(testCustomer);
        when(encoder.matches("password123", "encodedPassword")).thenReturn(true);

        Customer result = service.loginCustomer("testuser", "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testLoginCustomer_InvalidCredentials() {
        when(repository.findByUsername("testuser")).thenReturn(testCustomer);
        when(encoder.matches("wrong", "encodedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.loginCustomer("testuser", "wrong"));
    }

    @Test
    void testGetCustomerById_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerResponseDTO response = service.getCustomerById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getAccountNo());
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.getCustomerById(99L));
    }

    @Test
    void testGetCustomerByUsernameDTO_Success() {
        when(repository.findByUsername("testuser")).thenReturn(testCustomer);

        CustomerResponseDTO response = service.getCustomerByUsernameDTO("testuser");

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void testGetCustomerByUsernameDTO_NotFound() {
        when(repository.findByUsername("unknown")).thenReturn(null);

        assertThrows(CustomerNotFoundException.class, () -> service.getCustomerByUsernameDTO("unknown"));
    }

    @Test
    void testGetAllCustomers() {
        when(repository.findAll()).thenReturn(Arrays.asList(testCustomer));

        List<CustomerResponseDTO> list = service.getAllCustomers();

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void testUpdateCustomer_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(repository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponseDTO response = service.updateCustomer(testDto, 1L);

        assertNotNull(response);
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_WithoutPassword() {
        testDto.setPassword(null);
        when(repository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(repository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponseDTO response = service.updateCustomer(testDto, 1L);

        assertNotNull(response);
        verify(encoder, never()).encode(anyString());
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.updateCustomer(testDto, 1L));
    }

    @Test
    void testDeleteCustomer_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(testCustomer));
        doNothing().when(repository).delete(testCustomer);

        service.deleteCustomer(1L);

        verify(repository, times(1)).delete(testCustomer);
    }

    @Test
    void testLoginCustomer_InvalidPassword() {
        Customer c = new Customer();
        c.setPassword("encoded_password");
        when(repository.findByUsername("testuser")).thenReturn(c);
        when(encoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> service.loginCustomer("testuser", "wrong_password"));
    }

    @Test
    void testDeleteCustomer_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> 
            service.deleteCustomer(1L));
    }
}
