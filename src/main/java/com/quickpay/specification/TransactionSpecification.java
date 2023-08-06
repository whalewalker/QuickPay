package com.quickpay.specification;

import com.quickpay.data.model.Account;
import com.quickpay.data.model.Transaction;
import jakarta.persistence.criteria.Join;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static com.quickpay.utils.Utils.convertToLastHourOfDay;
import static com.quickpay.utils.Utils.convertToStartOfDay;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionSpecification {

    private static final String CREATED_AT = "createdAt";

    public static Specification<Transaction> withTransactionId(String transactionId) {
        if (transactionId == null) return null;
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("transactionId"), transactionId);
    }

    public static Specification<Transaction> withBetweenDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return null;
        } else if (startDate == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(CREATED_AT), convertToLastHourOfDay(endDate));
        } else if (endDate == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(CREATED_AT), convertToStartOfDay(startDate));
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(CREATED_AT), convertToStartOfDay(startDate), convertToLastHourOfDay(endDate));
        }
    }

    public static Specification<Transaction> withTransactionType(String transactionType) {
        if (transactionType == null) return null;
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("transactionType"), transactionType);
    }

    public static Specification<Transaction> withAccount(String accountNumber) {
        if (accountNumber == null) return null;
        return (root, query, criteriaBuilder) -> {
            Join<Transaction, Account> accountJoin = root.join("account");
            return criteriaBuilder.equal(accountJoin.get("accountNumber"), accountNumber);
        };
    }
    
}
