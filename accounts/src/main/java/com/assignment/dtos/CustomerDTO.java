package com.assignment.dtos;
import java.util.List;

/**
 * A DTO (Data Transfer Object) representing customer information.
 *
 * @param name       the customer's first name
 * @param surName    the customer's surname
 * @param accountList a list of the customer's accounts
 */
public record CustomerDTO(String name,
                          String surName,
                          List<AccountDTO> accountList)
{

}
