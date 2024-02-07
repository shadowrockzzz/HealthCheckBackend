package com.servicehealthcheck.sericeMonitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.reactive.function.client.ClientResponse;
import com.servicehealthcheck.sericeMonitor.model.User;
import com.servicehealthcheck.sericeMonitor.repository.HealthRepository;
import com.servicehealthcheck.sericeMonitor.repository.UserRepository;
import java.util.Random;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import reactor.core.publisher.Mono;
import com.servicehealthcheck.sericeMonitor.model.HealthStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.mongodb.core.query.Criteria;

// import org.springframework.web.client.RestTemplate;

@CrossOrigin
@Controller
@EnableScheduling
public class HomeController {

    @Value("${jwt.secret}")
    private String secretKey;

    private final WebClient webClient;

    @Value("${basePath}")
    private String baseURI;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private HealthRepository healthStatusRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public HomeController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseURI).build();
    }

    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setId(generateRandomLong(16));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/services")
    @ResponseBody
    public Map<String, String> services() {
        Map<String, String> responseMap = new HashMap<>();
        String actuatorHealthUrl = baseURI + "actuator/health";
        String actuatorLoggerUrl = baseURI + "/actuator/logger";
        String actuatorInfoUrl = baseURI + "/actuator/info";
        String actuatorCachesUrl = baseURI + "/actuator/caches";
        String actuatorConfigpropsUrl = baseURI + "/actuator/configprops";
        String actuatorPrometheusUrl = baseURI + "/actuator/prometheus";
        String actuatorEnvUrl = baseURI + "/actuator/env";
        String actuatorMetricsUrl = baseURI + "/actuator/metrics";
        String actuatorMappingsUrl = baseURI + "/actuator/mappings";
        String actuatorHeapdumpUrl = baseURI + "/actuator/heapdump";
        String actuatorBeansUrl = baseURI + "/actuator/beans";
        System.out.print("It is working");
        responseMap.put("actuator/health", getStatus(actuatorHealthUrl).block());
        responseMap.put("actuator/logger", getStatus(actuatorLoggerUrl).block());
        responseMap.put("actuator/info", getStatus(actuatorInfoUrl).block());
        responseMap.put("actuator/caches", getStatus(actuatorCachesUrl).block());
        responseMap.put("actuator/configprops", getStatus(actuatorConfigpropsUrl).block());
        responseMap.put("actuator/prometheus", getStatus(actuatorPrometheusUrl).block());
        responseMap.put("actuator/env", getStatus(actuatorEnvUrl).block());
        responseMap.put("actuator/metrics", getStatus(actuatorMetricsUrl).block());
        responseMap.put("actuator/mappings", getStatus(actuatorMappingsUrl).block());
        responseMap.put("actuator/heapdump", getStatus(actuatorHeapdumpUrl).block());
        responseMap.put("actuator/beans", getStatus(actuatorBeansUrl).block());
        return responseMap;
    }

    private Mono<String> getStatus(String url) {
        return webClient.get()
                .uri(url)
                .exchange()
                .flatMap(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just("ACTIVE");
                    } else {
                        return Mono.just("INACTIVE");
                    }
                });
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<HealthStatus>> findInactiveServices(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to) {
        List<HealthStatus> healthStatusList = mongoTemplate.find(
                new Query(Criteria.where("status").is("INACTIVE").andOperator(
                        Criteria.where("timestamp").gte(from).andOperator(Criteria.where("timestamp").lte(to)))),
                HealthStatus.class);
        return ResponseEntity.ok(healthStatusList);
    }

    @GetMapping("/indivServiceStatus")
    public ResponseEntity<List<HealthStatus>> getHealthStatusesByEndpoint(
            @RequestParam(value = "endpoint") String endpoint) {
        List<HealthStatus> healthStatusList = mongoTemplate.find(new Query(Criteria.where("endpoint").is(endpoint)),
                HealthStatus.class);
        return ResponseEntity.ok(healthStatusList);
    }

    @Scheduled(fixedRate = 60000) // check every minute
    public void checkHealth() {

        String actuatorHealthUrl = baseURI + "/actuator/health";
        String actuatorLoggerUrl = baseURI + "/actuator/logger";
        String actuatorInfoUrl = baseURI + "/actuator/info";
        String actuatorCachesUrl = baseURI + "/actuator/caches";
        String actuatorConfigpropsUrl = baseURI + "/actuator/configprops";
        String actuatorPrometheusUrl = baseURI + "/actuator/prometheus";
        String actuatorEnvUrl = baseURI + "/actuator/env";
        String actuatorMetricsUrl = baseURI + "/actuator/metrics";
        String actuatorMappingsUrl = baseURI + "/actuator/mappings";
        String actuatorHeapdumpUrl = baseURI + "/actuator/heapdump";
        String actuatorBeansUrl = baseURI + "/actuator/beans";

        HealthStatus healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/info");
        healthStatus.setStatus(getStatus(actuatorInfoUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/logger");
        healthStatus.setStatus(getStatus(actuatorLoggerUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/env");
        healthStatus.setStatus(getStatus(actuatorEnvUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/health");
        healthStatus.setStatus(getStatus(actuatorHealthUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/caches");
        healthStatus.setStatus(getStatus(actuatorCachesUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/configprop");
        healthStatus.setStatus(getStatus(actuatorConfigpropsUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/prometheus");
        healthStatus.setStatus(getStatus(actuatorPrometheusUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/metrics");
        healthStatus.setStatus(getStatus(actuatorMetricsUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/mappings");
        healthStatus.setStatus(getStatus(actuatorMappingsUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/heapdump");
        healthStatus.setStatus(getStatus(actuatorHeapdumpUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

        healthStatus = new HealthStatus();
        healthStatus.setId(generateRandomLong(16));
        healthStatus.setEndpoint("actuator/beans");
        healthStatus.setStatus(getStatus(actuatorBeansUrl).block());
        healthStatus.setTimestamp(new Date());
        healthStatusRepository.save(healthStatus);

    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null || !password.equals(existingUser.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = Jwts.builder()
                .setSubject(existingUser.getUsername())
                .claim("tokenId", existingUser.getTokenId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        Map<String, Object> response = new HashMap<>();
        response.put("username", existingUser.getUsername());
        response.put("tokenId", existingUser.getTokenId());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    private static long generateRandomLong(int length) {
        if (length <= 0 || length > 18) {
            throw new IllegalArgumentException("Invalid length for a Long");
        }

        Random random = new Random();
        long lowerBound = (long) Math.pow(10, length - 1);
        long upperBound = (long) Math.pow(10, length) - 1;

        return lowerBound + random.nextLong() % (upperBound - lowerBound + 1);
    }
}