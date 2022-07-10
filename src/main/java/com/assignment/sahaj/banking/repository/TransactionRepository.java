package com.assignment.sahaj.banking.repository;

import com.assignment.sahaj.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
