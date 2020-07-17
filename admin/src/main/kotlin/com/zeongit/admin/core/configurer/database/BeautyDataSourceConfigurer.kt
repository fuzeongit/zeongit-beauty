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
        entityManagerFactoryRef = "entityManagerFactoryBeauty",
        transactionManagerRef = "transactionManagerBeauty",
        basePackages = ["com.zeongit.data.database.primary.dao"]) //设置Repository所在位置
class BeautyDataSourceConfigurer(private val jpaProperties: JpaProperties) {
    @Bean
    @Qualifier("beautyDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.beauty")
    fun beautyDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }


    @Bean
    fun entityManagerBeauty(builder: EntityManagerFactoryBuilder, hibernateProperties: HibernateProperties): EntityManager {
        return entityManagerFactoryBeauty(builder, hibernateProperties).`object`!!.createEntityManager()
    }

    @Bean
    fun entityManagerFactoryBeauty(builder: EntityManagerFactoryBuilder, hibernateProperties: HibernateProperties): LocalContainerEntityManagerFactoryBean {
        val properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.properties, HibernateSettings())
        return builder
                .dataSource(beautyDataSource())
                .properties(properties)
                .packages("com.zeongit.data.database.primary.entity") //设置实体类所在位置
                .persistenceUnit("beautyPersistenceUnit")
                .build()
    }

    @Bean
    fun transactionManagerBeauty(builder: EntityManagerFactoryBuilder, hibernateProperties: HibernateProperties): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactoryBeauty(builder, hibernateProperties).`object`!!)
    }
}
