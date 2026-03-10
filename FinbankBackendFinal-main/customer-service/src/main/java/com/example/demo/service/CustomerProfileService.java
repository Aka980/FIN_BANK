package com.example.demo.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerProfile;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.repository.CustomerProfileRepository;
import com.example.demo.repository.CustomerRepository;

@Service
public class CustomerProfileService {

    @Autowired
    private CustomerProfileRepository profileRepository;

    @Autowired
    private CustomerRepository customerRepository;



    /*
     * Create Profile
     * accountNo = Primary Key + Foreign Key
     */
    public CustomerProfile createProfile(

            Long accountNo,

            CustomerProfile profile) {


        Customer customer =
                customerRepository.findById(accountNo)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer Not Found"));


        /*
         * Check Existing Profile
         */
        CustomerProfile existingProfile =
                profileRepository.findByAccountNo(accountNo);

        if (existingProfile != null) {

            throw new RuntimeException(
                    "Profile already exists");
        }


        /*
         * Check Duplicate PAN
         */
        CustomerProfile existingPan =
                profileRepository.findByPan(profile.getPan());

        if (existingPan != null) {

            throw new RuntimeException(
                    "PAN already registered");
        }


        /*
         * IMPORTANT
         * Set Customer → MapsId handles accountNo
         */
        profile.setCustomer(customer);


        profile.setCreatedDate(
                LocalDate.now());


        return profileRepository.save(profile);

    }



    /*
     * Get Profile By AccountNo
     */
    public CustomerProfile getProfile(

            Long accountNo) {


        CustomerProfile profile =
                profileRepository.findByAccountNo(accountNo);

        if (profile == null) {

            throw new RuntimeException(
                    "Profile Not Found");
        }

        return profile;

    }
    public CustomerProfile updateProfile(

            Long accountNo,

            CustomerProfile updatedProfile){

        CustomerProfile existing =
                profileRepository.findByAccountNo(accountNo);

        if(existing==null){

            throw new RuntimeException(
                    "Profile Not Found");
        }

        existing.setPan(
                updatedProfile.getPan());

        existing.setDob(
                updatedProfile.getDob());

        existing.setAddress(
                updatedProfile.getAddress());

        existing.setIfsc(
                updatedProfile.getIfsc());

        existing.setAnnualIncome(
                updatedProfile.getAnnualIncome());

        existing.setOccupation(
                updatedProfile.getOccupation());

        return profileRepository.save(existing);

    }

    public CustomerProfile getByPan(

            String pan) {


        CustomerProfile profile =
                profileRepository.findByPan(pan);

        if (profile == null) {

            throw new RuntimeException(
                    "PAN Not Found");
        }

        return profile;

    }

}