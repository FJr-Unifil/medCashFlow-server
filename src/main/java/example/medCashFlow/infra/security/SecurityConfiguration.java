package example.medCashFlow.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final SecurityFilter securityFilter;

    @Value("${api.security.admin.username}")
    private String adminUsername;

    @Value("${api.security.admin.password}")
    private String adminPassword;

    @Bean
    public DatabaseAuthenticationProvider databaseAuthenticationProvider() {
        return new DatabaseAuthenticationProvider(passwordEncoder());
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder.inMemoryAuthentication()
                .withUser(adminUsername)
                .password(passwordEncoder().encode(adminPassword))
                .roles("ADMIN");

        authBuilder.authenticationProvider(databaseAuthenticationProvider());

        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authManager) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configure(httpSecurity))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/clinics/list").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/clinics/delete/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/clinics/activate/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/employees/{id}").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/employees/list").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.POST, "/employees/create").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/employees/update/{id}").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/employees/delete/{id}").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/employees/activate/{id}").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/involveds/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/involveds/list")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.POST, "/involveds/create")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/involveds/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.DELETE, "/involveds/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/involveds/activate/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/account-plannings/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/account-plannings/list")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.POST, "/account-plannings/create")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/account-plannings/update/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.DELETE, "/account-plannings/delete/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/bills/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/bills/list")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.POST, "/bills/create")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/bills/update/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.DELETE, "/bills/delete/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/installments/update/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/installments/mark-as-paid/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/installments/mark-as-unpaid/{id}")
                        .hasAnyRole("MANAGER", "FINANCIAL_ANALYST")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
