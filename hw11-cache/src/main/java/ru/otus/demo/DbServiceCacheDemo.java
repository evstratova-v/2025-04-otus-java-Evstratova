package ru.otus.demo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientCacheProxy;
import ru.otus.crm.service.DbServiceClientImpl;

/** VM options: -Xmx128m -Xms128m -Xlog:gc=debug */
public class DbServiceCacheDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceCacheDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);

        var clientFirst = dbServiceClient.saveClient(new Client(
                "dbServiceFirst",
                new Address("FirstStreet"),
                List.of(new Phone("13-555-22"), new Phone("14-666-333"))));
        long id = clientFirst.getId();

        var cache = new MyCache<String, Client>();
        HwListener<String, Client> listener =
                (key, value, action) -> log.info("key:{}, value:{}, action: {}", key, value, action);
        cache.addListener(listener);
        var dbServiceClientCacheProxy = new DbServiceClientCacheProxy(dbServiceClient, cache);

        // замеряем время без кэша
        dbServiceClient.getClient(id);
        var before = LocalDateTime.now();
        dbServiceClient.getClient(id);
        var after = LocalDateTime.now();
        long millisWithoutCache = ChronoUnit.MILLIS.between(before, after);

        // замеряем время с кэшом
        dbServiceClientCacheProxy.getClient(id);
        before = LocalDateTime.now();
        dbServiceClientCacheProxy.getClient(id);
        after = LocalDateTime.now();
        long millisWithCache = ChronoUnit.MILLIS.between(before, after);

        for (int i = 0; i < 500; i++) {
            var client = new Client(
                    "clientName" + i,
                    new Address("street" + i),
                    List.of(new Phone("numberFirst" + i), new Phone("numberSecond" + i)));
            dbServiceClientCacheProxy.saveClient(client);
        }
        dbServiceClient.findAll();

        log.info("get client with id {} that was cached earlier", id);
        dbServiceClientCacheProxy.getClient(id); // на момент вызова будет удалён из кэша
        log.info(
                "millis for get client without cache: {}, millis for get client with cache: {}",
                millisWithoutCache,
                millisWithCache);
    }
}
