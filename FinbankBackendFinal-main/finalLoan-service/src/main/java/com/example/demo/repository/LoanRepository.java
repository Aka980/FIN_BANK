/*
 * package com.example.demo.repository;
 * 
 * import org.springframework.data.jpa.repository.JpaRepository; import
 * com.example.demo.entity.LoanApplication;
 * 
 * public interface LoanRepository extends JpaRepository<LoanApplication,Long> {
 * }
 */

package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.LoanApplication;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanApplication, Long> {

//    Optional<LoanApplication> findByApplicationNoAndUsername(Long applicationNo, String username);
//    List<LoanApplication> findAllByAccountNo(Long accountNo);
	List<LoanApplication> findByAccountNo(Long accountNo);


}