package example.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 *  @Configuration annotation tells Spring to use this class to configure Spring and Spring Boot itself.
 *  Any Beans specified in this class will now be available to Spring's Auto Configuration engine.
 */
@Configuration
public class SecurityConfig {

    /**
     * All HTTP requests to cashcards/ endpoints are required to be authenticated using HTTP Basic Authentication security (username and password).
     * Also, do not require CSRF security.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/cashcards/**")
                        //.authenticated()
                        .hasRole("CARD-OWNER")) // enable  Role-Based Access Control (RBAC): Replace the .authenticated() call with the hasRole(...) call.)

                        /**
                         * use CSRF protection for any request that could be processed by a browser by normal users.
                         * If you are only creating a service that is used by non-browser clients, you will likely want to disable CSRF protection.
                         */
                        .csrf(csrf -> csrf.disable())
                        .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * configure a user named sarah1 with password abc123.
     * Spring's IoC container will find the UserDetailsService Bean and Spring Data will use it when needed.
     */
    @Bean
    public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails sarah = users
                .username("sarah1")
                .password(passwordEncoder.encode("abc123"))
                .roles("CARD-OWNER") // new role
                .build();
        UserDetails hankOwnsNoCards = users
                .username("hank-owns-no-cards")
                .password(passwordEncoder.encode("qrs456"))
                .roles("NON-OWNER") // new role
                .build();

        UserDetails kumar = users
                .username("kumar2")
                .password(passwordEncoder.encode("xyz789"))
                .roles("CARD-OWNER")
                .build();

        return new InMemoryUserDetailsManager(sarah, hankOwnsNoCards, kumar);
    }
}