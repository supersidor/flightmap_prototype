package com.supersidor.flightmap.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import javax.annotation.PostConstruct;

@Configuration

public class MongoConfig extends AbstractReactiveMongoConfiguration {
    @Value("${mongodb.database}")
    private String databaseName;

//    public MongoConfig(MongoMappingContext mappingContext){
//        mappingContext.setAutoIndexCreation(true);
//
//    }

    @Bean
    public MongoClient mongoClient() {

        final MongoClient mongoClient = MongoClients.create();
        return mongoClient;
    }
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient(), getDatabaseName());
    }
    protected boolean autoIndexCreation() {
        return true;
    }
//    @Bean
//    public MappingMongoConverter mappingMongoConverter(ReactiveMongoDatabaseFactory databaseFactory,
//                                                       MongoCustomConversions customConversions, MongoMappingContext mappingContext) {
//
//        mappingContext.setAutoIndexCreation(true);
//        MappingMongoConverter converter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);
//        converter.setCustomConversions(customConversions);
//        converter.setCodecRegistryProvider(databaseFactory);
//
//        return converter;
//    }
}
