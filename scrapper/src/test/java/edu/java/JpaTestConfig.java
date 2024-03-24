package edu.java;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.java.domain.jpa.JpaChatRepository;
import edu.java.domain.jpa.JpaLinkDao;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.jpa.JpaChatService;
import edu.java.service.jpa.JpaLinkService;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import static edu.java.IntegrationTest.POSTGRES;

@Configuration
@ComponentScan("edu.java.domain.jpa")
@EnableJpaRepositories("edu.java.domain.jpa")
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class JpaTestConfig {

    @Bean("transactionManager")
    protected PlatformTransactionManager transactionManager(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig dataSourceProperties = new HikariConfig();
        dataSourceProperties.setJdbcUrl(POSTGRES.getJdbcUrl());
        dataSourceProperties.setUsername(POSTGRES.getUsername());
        dataSourceProperties.setPassword(POSTGRES.getPassword());
        return new HikariDataSource(dataSourceProperties);
    }

    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("edu.java.domain.jpa");
        factory.setDataSource(dataSource);

        return factory;
    }

    @Bean
    public LinkService linkService(
        JpaLinkDao linkDao,
        JpaChatRepository chatRepository
    ) {
        return new JpaLinkService(linkDao, chatRepository);
    }

    @Bean
    public ChatService chatService(
        JpaChatRepository chatRepository
    ) {
        return new JpaChatService(chatRepository);
    }

}
