package com.assignment.sahaj.banking.repository;

import com.assignment.sahaj.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
