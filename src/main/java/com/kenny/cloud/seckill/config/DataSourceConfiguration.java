package com.kenny.cloud.seckill.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @ClassName DataSourceConfiguration
 * @Description:
 * @Author Kenny
 * @Date 2020/3/24
 **/
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "datasource")
public class DataSourceConfiguration {

    private String driver;
    private String url;
    private String username;
    private String passwd;

    private int initialSize;
    private int minIdle;
    private int maxActive;
    private int maxWait;
    private int timeBetweenEvictionRunsMillis;
    private int minEvictableIdleTimeMillis;
    private String connectionProperties;
    private String validationQuery;
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private String filters;

    @Bean
    public ServletRegistrationBean druidServlet(){
        return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    }

    @Bean
    @Primary
    public DataSource dataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(this.driver);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.passwd);
        dataSource.setMaxActive(this.maxActive);
        dataSource.setInitialSize(this.initialSize);
        dataSource.setMaxWait(this.maxWait);
        dataSource.setMinIdle(this.minIdle);
        dataSource.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(this.validationQuery);
        dataSource.setTestWhileIdle(this.testWhileIdle);
        dataSource.setTestOnBorrow(this.testOnBorrow);
        dataSource.setTestOnReturn(this.testOnReturn);
        if (!StringUtils.isEmpty(this.filters))
        {
            dataSource.setFilters(filters);
        }
        dataSource.setConnectionProperties(this.connectionProperties);
        return dataSource;
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        try {
            return new JdbcTemplate(dataSource());
        } catch (SQLException e) {
        }
        return new JdbcTemplate();
    }



}
