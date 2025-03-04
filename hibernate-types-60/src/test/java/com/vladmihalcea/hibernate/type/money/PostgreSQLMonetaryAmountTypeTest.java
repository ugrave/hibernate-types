package com.vladmihalcea.hibernate.type.money;

import com.vladmihalcea.hibernate.util.AbstractPostgreSQLIntegrationTest;
import jakarta.persistence.*;
import org.hibernate.annotations.CompositeType;
import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author Piotr Olaszewski
 */
public class PostgreSQLMonetaryAmountTypeTest extends AbstractPostgreSQLIntegrationTest {
    @Override
    protected Class<?>[] entities() {
        return new Class[]{
                Salary.class
        };
    }

    @Test
    public void testPersistAndReadMoney() {
        Salary _salary = doInJPA(entityManager -> {
            Salary salary = new Salary();
            salary.setSalary(Money.of(new BigDecimal("10.23"), "USD"));

            entityManager.persist(salary);

            return salary;
        });

        doInJPA(entityManager -> {
            Salary salary = entityManager.find(Salary.class, _salary.getId());

            assertEquals(salary.getSalary(), Money.of(new BigDecimal("10.23"), "USD"));
        });
    }

    @Test
    public void testSearchByMoney() {
        doInJPA(entityManager -> {
            Salary salary1 = new Salary();
            salary1.setSalary(Money.of(new BigDecimal("10.23"), "USD"));
            entityManager.persist(salary1);

            Salary salary2 = new Salary();
            salary2.setSalary(Money.of(new BigDecimal("20.23"), "EUR"));
            entityManager.persist(salary2);
        });

        doInJPA(entityManager -> {
            Money money = Money.of(new BigDecimal("10.23"), "USD");
            Salary salary = entityManager.createQuery("select s from Salary s where salary = :salary", Salary.class)
                    .setParameter("salary", money)
                    .getSingleResult();

            assertEquals(Long.valueOf(1), salary.getId());
        });
    }

    @Entity(name = "Salary")
    @Table(name = "salary")
    public static class Salary {
        @Id
        @GeneratedValue
        private Long id;

        @AttributeOverride(name = "amount", column = @Column(name = "salary_amount"))
        @AttributeOverride(name = "currency", column = @Column(name = "salary_currency"))
        @CompositeType(MonetaryAmountType.class)
        private MonetaryAmount salary;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public MonetaryAmount getSalary() {
            return salary;
        }

        public void setSalary(MonetaryAmount salary) {
            this.salary = salary;
        }
    }
}
