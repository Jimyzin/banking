package com.assignment.sahaj.banking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_number_generator")
    @SequenceGenerator(initialValue = 1001,
            allocationSize = 1000,
            name = "account_number_generator",
            sequenceName = "account_number_seq")
    @Column(name = "account_number")
    private Long accountNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    private Double balance;

    @Column(name = "daily_debit_count")
    private Integer dailyDebitCount;

    @Column(name = "last_debit_date", columnDefinition = "DATE")
    private LocalDate lastDebitDate;

    @Column(name = "daily_credit_count")
    private Integer dailyCreditCount;

    @Column(name = "last_credit_date", columnDefinition = "DATE")
    private LocalDate lastCreditDate;

    public Double debit(Double amount) {
        balance -= amount;
        return balance;
    }

    public Double credit(Double amount) {
        balance += amount;
        return balance;
    }

    public void resetDailyCreditCount() {
        dailyCreditCount = 0;
        lastCreditDate = LocalDate.now();
    }

    public void incrementDailyCreditCount() {
        dailyCreditCount += 1;
        lastCreditDate = LocalDate.now();
    }

    public void resetDailyDebitCount() {
        dailyDebitCount = 0;
        lastDebitDate = LocalDate.now();
    }

    public void incrementDailyDebitCount() {
        dailyDebitCount += 1;
        lastDebitDate = LocalDate.now();
    }

}
