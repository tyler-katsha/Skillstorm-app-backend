package com.skillstorm.skillstorm;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

  @Primary
  @Bean(name = "mysqlDataSource")
  public DataSource mysqlDataSource() {
    return DataSourceBuilder.create()
        .driverClassName("com.mysql.cj.jdbc.Driver")
        .url("jdbc:mysql://sql7.freesqldatabase.com:3306/sql7826363")
        .username("sql7826363")
        .password("DhG4WMqIm6")
        .build();
  }
}
