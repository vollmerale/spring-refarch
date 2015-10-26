package org.refarch.conf;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@Profile("dev")
@PropertySource("classpath:application.properties")
public class HikariPersistenceConfig {

	@Autowired
	private Environment environment;

	@Bean
	public LocalContainerEntityManagerFactoryBean  entityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean sessionFactory = new LocalContainerEntityManagerFactoryBean();

        sessionFactory.setDataSource(dataSource());        

		String[] packachesToScan = this.getEnvironment().getProperty("jpa.packages.to.scan.entities").split(",");

		sessionFactory.setPackagesToScan(packachesToScan);
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		sessionFactory.setJpaVendorAdapter(vendorAdapter);
		
		Properties jpaProperties = new Properties();

		jpaProperties.setProperty("hibernate.hbm2ddl.auto", this.getEnvironment().getProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.setProperty("hibernate.dialect", this.getEnvironment().getProperty("hibernate.dialect"));
		jpaProperties.setProperty("hibernate.format_sql", this.getEnvironment().getProperty("hibernate.format_sql"));
		jpaProperties.setProperty("hibernate.show_sql", this.getEnvironment().getProperty("hibernate.show_sql"));
//		jpaProperties.setProperty("hibernate.connection.driver_class", this.getEnvironment().getProperty("hibernate.connection.driver_class"));
		
		sessionFactory.setJpaProperties(jpaProperties);

		return sessionFactory;
	}
	
	private DataSource dataSource() {
		
        final HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(50);
        ds.setDataSourceClassName(environment.getProperty("hikari.ds.connection.data_source_class_name"));
        ds.addDataSourceProperty("url", environment.getProperty("hikari.ds.connection.url"));
        ds.addDataSourceProperty("user", this.getEnvironment().getProperty("hikari.ds.connection.username"));
        ds.addDataSourceProperty("password", this.getEnvironment().getProperty("hikari.ds.connection.password"));
        //ds.addDataSourceProperty("cachePrepStmts", true);
        //ds.addDataSourceProperty("prepStmtCacheSize", 250);
        //ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        //ds.addDataSourceProperty("useServerPrepStmts", true);
        return ds;
    }

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}