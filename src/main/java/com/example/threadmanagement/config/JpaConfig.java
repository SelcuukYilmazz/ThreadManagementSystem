package com.example.threadmanagement.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration // Marks this class as a configuration class for Spring
@EnableTransactionManagement // Enables transaction management in the application
@EntityScan(basePackages = "com.example.threadmanagement.model.entity") // Scans for JPA entity classes in the specified package
@EnableJpaRepositories(basePackages = "com.example.threadmanagement.domain.repository.interfaces") // Enables JPA repositories in the specified package
public class JpaConfig {

    @Autowired
    private Environment env; // Spring's environment to access application properties

    /**
     * Configures the data source for the application.
     * @return A DataSource object set up for connecting to the database.
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // Setting the database driver class
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        // Reading the database URL from application properties
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        // Reading the database username and password from application properties
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    /**
     * Configures the EntityManagerFactory for JPA.
     * This factory creates and manages entity managers for interacting with the database.
     * @return A configured LocalContainerEntityManagerFactoryBean.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        // Setting the data source for the entity manager
        em.setDataSource(dataSource());
        // Specifying the package where JPA entity classes are located
        em.setPackagesToScan("com.example.threadmanagement.model.entity");

        // Configuring Hibernate as the JPA provider
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Setting JPA-specific properties
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update"); // Automatically updates the database schema
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect"); // SQL Server dialect for Hibernate
        properties.put("hibernate.show_sql", "true"); // Enables logging of SQL queries for debugging
        em.setJpaPropertyMap(properties);

        return em;
    }

    /**
     * Configures the transaction manager to handle database transactions.
     * @param entityManagerFactory The factory used to create entity managers.
     * @return A configured PlatformTransactionManager.
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        // Linking the transaction manager to the entity manager factory
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
