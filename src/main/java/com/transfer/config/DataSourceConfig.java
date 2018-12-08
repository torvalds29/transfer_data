package com.transfer.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.transfer.vo.DataSourceSourceVo;
import com.transfer.vo.DataSourceTargetVo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    DataSource sourceDataSource(DataSourceSourceVo dataSourceSourceVo) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(dataSourceSourceVo.getUrl());
        druidDataSource.setDriverClassName(dataSourceSourceVo.getDriverClassName());
        druidDataSource.setUsername(dataSourceSourceVo.getUsername());
        druidDataSource.setPassword(dataSourceSourceVo.getPassword());
        druidDataSource.setMaxActive(50);
        return druidDataSource;
    }

    @Bean
    DataSource targetDataSource(DataSourceTargetVo dataSourceTargetVo) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(dataSourceTargetVo.getUrl());
        druidDataSource.setDriverClassName(dataSourceTargetVo.getDriverClassName());
        druidDataSource.setUsername(dataSourceTargetVo.getUsername());
        druidDataSource.setPassword(dataSourceTargetVo.getPassword());
        druidDataSource.setMaxActive(50);
        return druidDataSource;
    }

    @Bean(name = "sourceJdbcTemplate")
    JdbcTemplate sourceJdbcTemplate(DataSourceSourceVo dataSourceSourceVo) {
        return new JdbcTemplate(sourceDataSource(dataSourceSourceVo));
    }

    @Bean(name = "targetJdbcTemplate")
    JdbcTemplate targetdbcTemplate(DataSourceTargetVo dataSourceTargetVo) {
        return new JdbcTemplate(targetDataSource(dataSourceTargetVo));
    }
}
