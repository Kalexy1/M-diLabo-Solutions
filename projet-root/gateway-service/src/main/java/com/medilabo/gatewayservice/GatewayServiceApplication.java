package com.medilabo.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * Application: com.medilabo.gatewayservice
 * <p>
 * Classe <strong>GatewayServiceApplication</strong>.
 * <br/>
 * Rôle : Point d'entrée du microservice Gateway basé sur <em>Spring Cloud Gateway</em>.
 * </p>
 * <p>
 * Ce microservice agit comme un proxy inverse centralisé.  
 * Il intercepte les requêtes entrantes et les redirige vers les microservices cibles
 * (auth, patient, notes, risk, UI).
 * </p>
 */
@SpringBootApplication
public class GatewayServiceApplication {

    /**
     * Méthode principale qui démarre l'application Spring Boot.
     *
     * @param args paramètres de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    /**
     * Déclare et configure les routes du Gateway.
     * <p>
     * Les routes sont créées uniquement si la propriété
     * <code>spring.cloud.gateway.enabled=true</code> (ou si la propriété est absente,
     * car <code>matchIfMissing=true</code> est activé).  
     * Cela permet de démarrer un contexte allégé pour les tests en désactivant le Gateway.
     * </p>
     *
     * <h3>Conventions définies :</h3>
     * <ul>
     *   <li><b>auth-service</b> : <code>/auth/**</code> → <code>lb://AUTH-SERVICE</code></li>
     *   <li><b>patient-service</b> : <code>/patients/**</code> → <code>/**</code> vers <code>lb://PATIENT-SERVICE</code></li>
     *   <li><b>note-service</b> : <code>/notes/**</code> → <code>/**</code> vers <code>lb://NOTE-SERVICE</code></li>
     *   <li><b>risk-service</b> : <code>/risk/**</code> (ex. <code>/risk/assess/1</code>)
     *       → réécrit en <code>/assess/**</code> pour <code>lb://RISK-ASSESSMENT-SERVICE</code></li>
     *   <li><b>patient-ui-service</b> : <code>/ui/**</code> → <code>/**</code> vers <code>lb://PATIENT-UI-SERVICE</code></li>
     * </ul>
     *
     * @param builder le constructeur de routes fourni par Spring Cloud Gateway
     * @return un {@link RouteLocator} contenant toutes les routes configurées
     */
    @Bean
    @ConditionalOnProperty(value = "spring.cloud.gateway.enabled", havingValue = "true", matchIfMissing = true)
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Auth-service
            .route("auth-service", r -> r.path("/auth/**")
                .uri("lb://AUTH-SERVICE"))

            // Patient-service
            .route("patient-service", r -> r.path("/patients/**")
                .filters(f -> f.rewritePath("/patients/(?<segment>.*)", "/${segment}"))
                .uri("lb://PATIENT-SERVICE"))

            // Note-service
            .route("note-service", r -> r.path("/notes/**")
                .filters(f -> f.rewritePath("/notes/(?<segment>.*)", "/${segment}"))
                .uri("lb://NOTE-SERVICE"))

            // Risk-assessment-service
            .route("risk-service", r -> r.path("/risk/**")
                .filters(f -> f.rewritePath("/risk/(?<segment>.*)", "/assess/${segment}"))
                .uri("lb://RISK-ASSESSMENT-SERVICE"))

            // Patient-ui-service
            .route("patient-ui-service", r -> r.path("/ui/**")
                .filters(f -> f.rewritePath("/ui/(?<segment>.*)", "/${segment}"))
                .uri("lb://PATIENT-UI-SERVICE"))

            .build();
    }
}
