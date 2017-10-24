package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DbConfig {
    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setGenerateDdl(true);
        return adapter;
    }

    private static DataSource buildDataSource(String url, String username, String password){
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private static LocalContainerEntityManagerFactoryBean buildEntityManagerFactoryBean
            (DataSource dataSource, HibernateJpaVendorAdapter jpaVendorAdapter, String unitName){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactoryBean.setPackagesToScan(DbConfig.class.getPackage().getName());
        entityManagerFactoryBean.setPersistenceUnitName("albums");
        return entityManagerFactoryBean;
    }
    public static class Movies {
        @Value("${moviefun.datasources.movies.url}") String url;
        @Value("${moviefun.datasources.movies.username}") String username;
        @Value("${moviefun.datasources.movies.password}") String password;

        @Bean
        public DataSource moviesDataSource(){
            return buildDataSource(url, username, password);
        }

        @Bean
        @Qualifier("movies")
        LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean
                (DataSource moviesDataSource, HibernateJpaVendorAdapter jpaVendorAdapter) {
            return buildEntityManagerFactoryBean(moviesDataSource, jpaVendorAdapter, "movies");
        }

        @Bean
        PlatformTransactionManager moviesTransactionManager
                (@Qualifier("movies") LocalContainerEntityManagerFactoryBean factoryBean){
            return new JpaTransactionManager(factoryBean.getObject());
        }
    }

    public static class Albums {
        @Value("${moviefun.datasources.albums.url}") String url;
        @Value("${moviefun.datasources.albums.username}") String username;
        @Value("${moviefun.datasources.albums.password}") String password;

        @Bean
        public DataSource albumsDataSource(){
            return buildDataSource(url, username, password);
        }

        @Bean
        @Qualifier("albums")
        LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean
                (DataSource moviesDataSource, HibernateJpaVendorAdapter jpaVendorAdapter) {
            return buildEntityManagerFactoryBean(moviesDataSource, jpaVendorAdapter, "albums");
        }

        @Bean
        PlatformTransactionManager albumsTransactionManager
                (@Qualifier("albums") LocalContainerEntityManagerFactoryBean factoryBean){
            return new JpaTransactionManager(factoryBean.getObject());
        }
    }
}
