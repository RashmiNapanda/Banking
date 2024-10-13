package com.assignment.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.assignment.constants.TransactionConstants.*;

@Slf4j
@Configuration
public class HazelcastConfig {

    private static final String INSTANCE_NAME = "hazel-instance";
    private static final String LOCALHOST_8081 = "127.0.0.1:8081";
    private static final String LOCALHOST_8082 = "127.0.0.1:8082";

    @Bean
    public Config hazelConfig() {
        return new Config()
                .setInstanceName(INSTANCE_NAME)
                .addMapConfig(createMapConfig(CUSTOMERS))
                .addMapConfig(createMapConfig(ACCOUNTS))
                .addMapConfig(createMapConfig(TRANSACTIONS))
                .setNetworkConfig(createNetworkConfig());
    }

    private MapConfig createMapConfig(String mapName) {
        return new MapConfig().setName(mapName);
    }

    private NetworkConfig createNetworkConfig() {
        NetworkConfig networkConfig = new NetworkConfig();
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(true);
        joinConfig.getTcpIpConfig()
                .addMember(LOCALHOST_8081)
                .addMember(LOCALHOST_8082);
        return networkConfig;
    }
}
