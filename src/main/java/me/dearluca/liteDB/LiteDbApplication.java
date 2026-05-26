package me.dearluca.liteDB;

import me.dearluca.liteDB.cluster.NodeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(NodeProperties.class)
public class LiteDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiteDbApplication.class, args);
	}

}
