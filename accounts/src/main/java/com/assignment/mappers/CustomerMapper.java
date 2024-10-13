package com.assignment.mappers;

import com.assignment.dtos.AccountDTO;
import com.assignment.dtos.CustomerDTO;
import com.assignment.dtos.TransactionDTO;
import com.assignment.entities.Account;
import com.assignment.entities.Customers;
import com.assignment.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(source = "accounts", target = "accountList")
    CustomerDTO toCustomerDTO(Customers customer);

    @Mapping(source = "transactions", target = "transactions")
    @Mapping(source = "accountType", target = "accountType")
    AccountDTO toAccountDTO(Account account);

    @Mapping(source = "transferAmount", target = "amount")
    @Mapping(source = "type", target = "transactionType")
    TransactionDTO toTransactionDTO(Transaction transaction);
}
