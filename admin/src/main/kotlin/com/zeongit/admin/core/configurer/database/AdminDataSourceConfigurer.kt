package com.zeongit.admin.core.configurer.database

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManager
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryAdmin",
        transactionManagerRef = "transactionManagerAdmin",
        basePackages = ["com.zeongit.data.database.admin.dao"]) //设置Repository所在位置
class AdminDataSourceConfigurer(private val jpaProperties: JpaProperties) {
    @Bean
    @Qualifier("adminDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.admin")
    fun adminDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }


    @Bean
    fun entityManagerAdmin(builder: EntityManagerFactoryBuilder, hibernateProperties: HibernateProperties): EntityManager {
        return entityManagerFactoryAdmin(builder, hibernateProperties).`object`!!.createEntityManager()
    }

    @Bean
    fun entityManagerFactoryAdmin(builder: EntityManagerFactoryBuilder, hibernateProperties: HibernateProperties): LocalContainerEntityManagerFactoryBean {
        val properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.properties, HibernateSettings())
        return builder
                .dataSource(adminDataSource())
                .properties(properties)
                .packages("com.zeongit.data.database.admin.entity") //设置实体类所在位置
                .persistenceUnit("adminPersistenceUnit")
                .build()
    }

    @Bean
    fun transactionManagerAdmin(builder: EntityManagerFactoryBuilder, hibernateProperties: HibernateProperties): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactoryAdmin(builder, hibernateProperties).`object`!!)
    }
}
