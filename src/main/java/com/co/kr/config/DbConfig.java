package com.co.kr.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@Configuration
@EnableTransactionManagement
public class DbConfig {

	@Bean(destroyMethod="close")
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://musicsns.cttt27ydojli.ap-northeast-2.rds.amazonaws.com:3306/musicsns?autoReconnect=true&serverTimezone=UTC&characterEncoding=UTF-8");
		dataSource.setUsername("admin");
		dataSource.setPassword("adminadmin!");
		dataSource.setMaxIdle(5);
		dataSource.setMinIdle(0);
		dataSource.setDefaultAutoCommit(false);
		return dataSource;
	}

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}