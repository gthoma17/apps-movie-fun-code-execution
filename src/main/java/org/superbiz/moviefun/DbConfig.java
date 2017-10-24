package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
public class DbConfig {
    @Value("${moviefun.datasources.albums.url}") String albumsUrl;
    @Value("${moviefun.datasources.albums.username}") String albumsUsername;
    @Value("${moviefun.datasources.albums.password}") String albumsPassword;
    @Value("${moviefun.datasources.albums.url}") String moviesUrl;
    @Value("${moviefun.datasources.albums.username}") String moviesUsername;
    @Value("${moviefun.datasources.albums.password}") String moviesPassword;

    @Bean
    public DataSource albumsDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(albumsUrl);
        dataSource.setUser(albumsUsername);
        dataSource.setPassword(albumsPassword);
        return dataSource;
    }

    @Bean DataSource moviesDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(moviesUrl);
        dataSource.setUser(moviesUsername);
        dataSource.setPassword(moviesPassword);
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setGenerateDdl(true);
        return adapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean(){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(albumsDataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        entityManagerFactoryBean.setPersistenceUnitName("albums");
        return entityManagerFactoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean(){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(moviesDataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        entityManagerFactoryBean.setPersistenceUnitName("movies");
        return entityManagerFactoryBean;
    }
}
