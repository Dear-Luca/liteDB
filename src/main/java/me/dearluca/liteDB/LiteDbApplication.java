package me.dearluca.liteDB;

import me.dearluca.liteDB.cluster.NodeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(NodeProperties.class)
public class LiteDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiteDbApplication.class, args);
	}

}
