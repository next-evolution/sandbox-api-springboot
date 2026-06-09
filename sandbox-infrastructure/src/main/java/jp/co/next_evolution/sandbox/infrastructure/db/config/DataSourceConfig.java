package jp.co.next_evolution.sandbox.infrastructure.db.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(basePackages = {"jp.co.next_evolution.sandbox.infrastructure.db.mapper"},
            sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfig {

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource.sandbox")
  public HikariConfig dataSourceProperties() {
    return new HikariConfig();
  }

  @Bean(name = {"dataSource"})
  @Primary
  public DataSource dataSource(@Qualifier("dataSourceProperties") HikariConfig properties) {
    return new HikariDataSource(properties);
  }

  @Bean(name = {"txManager"})
  @Primary
  public PlatformTransactionManager txManager(@Qualifier("dataSource") DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean(name = {"sqlSessionFactory"})
  @Primary
  public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
      throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource);
    return sqlSessionFactoryBean.getObject();
  }

}
