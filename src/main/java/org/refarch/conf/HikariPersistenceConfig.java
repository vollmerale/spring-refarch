package org.refarch.conf;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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

@PropertySource({ "classpath:persistence.properties" })
@Configuration
@ComponentScan({ "de.vollmerale" })
@EnableTransactionManagement
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

		String prefix = System.getProperty("profile");
		
		if(StringUtils.isBlank(prefix)){
			prefix = "local.";
		}

		jpaProperties.setProperty("hibernate.hbm2ddl.auto", this.getEnvironment().getProperty(prefix + "hibernate.hbm2ddl.auto"));
		jpaProperties.setProperty("hibernate.dialect", this.getEnvironment().getProperty(prefix + "hibernate.dialect"));
		jpaProperties.setProperty("hibernate.connection.driver_class", this.getEnvironment().getProperty(prefix + "hibernate.connection.driver_class"));
		jpaProperties.setProperty("hibernate.format_sql", this.getEnvironment().getProperty(prefix + "hibernate.format_sql"));
		jpaProperties.setProperty("hibernate.show_sql", this.getEnvironment().getProperty(prefix + "hibernate.show_sql"));
		jpaProperties.setProperty("hibernate.connection.autoReconnect", "true");
		jpaProperties.setProperty("hibernate.connection.autoReconnectForPools", "true");
		
		sessionFactory.setJpaProperties(jpaProperties);

		return sessionFactory;
	}
	
	private DataSource dataSource() {
		
		String prefix = System.getProperty("profile");
		
		if(StringUtils.isBlank(prefix)){
			prefix = "local.";
		}
		
        final HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(50);
        ds.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        ds.addDataSourceProperty("url", this.getEnvironment().getProperty(prefix + "hibernate.connection.url"));
        ds.addDataSourceProperty("user", this.getEnvironment().getProperty(prefix + "hibernate.connection.username"));
        ds.addDataSourceProperty("password", this.getEnvironment().getProperty(prefix + "hibernate.connection.password"));
        ds.addDataSourceProperty("cachePrepStmts", true);
        ds.addDataSourceProperty("prepStmtCacheSize", 250);
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        ds.addDataSourceProperty("useServerPrepStmts", true);
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