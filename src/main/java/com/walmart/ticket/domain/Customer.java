package com.walmart.ticket.domain;


import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


@Entity
@Table(name = "CUSTOMER")
public class Customer extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Email
    @Size(max = 100)
    @Column(name = "CUSTOMER_EMAIL", length = 100, nullable = false, unique = true)
    private String customerEmail;

    @Column(name = "FIRST_NAME", length = 50)
    private String firstName;

    @Column(name = "LAST_NAME", length = 50)
    private String lastName;

    private Customer() {
        super();
    }

    @PrePersist
    private void preCreate() {
        if (StringUtils.isBlank(firstName)) {
            firstName = "NOT USED";
        }
        if (StringUtils.isBlank(lastName)) {
            lastName = "NOT USED";
        }
    }

    public Customer(String firstName, String lastName, String customerEmail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerEmail = customerEmail;
    }

    public Customer(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Customer customer = (Customer) o;

        return customerEmail.equals(customer.customerEmail);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + customerEmail.hashCode();
        return result;
    }
}
