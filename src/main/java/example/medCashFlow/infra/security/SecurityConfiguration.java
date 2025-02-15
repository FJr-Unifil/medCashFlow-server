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
                        .requestMatchers(HttpMethod.GET, "/clinics/list").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/clinics/delete/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/clinics/activate/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/employees/{id}").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/employees/list").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/employees/create").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/employees/update/{id}").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/employees/delete/{id}").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/employees/activate/{id}").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/involveds/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/involveds/list")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.POST, "/involveds/create")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/involveds/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.DELETE, "/involveds/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/involveds/activate/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/account-plannings/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/account-plannings/list")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.POST, "/account-plannings/create")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/account-plannings/update/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.DELETE, "/account-plannings/delete/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/bills/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.GET, "/bills/list")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.POST, "/bills/create")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/bills/update/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.DELETE, "/bills/delete/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/installments/update/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/installments/mark-as-paid/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/installments/mark-as-unpaid/{id}")
                        .hasAnyAuthority("ROLE_MANAGER", "ROLE_FINANCIAL_ANALYST")
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
