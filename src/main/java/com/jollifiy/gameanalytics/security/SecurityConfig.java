package com.jollifiy.gameanalytics.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // API endpoint'lerinin dışarıdan (oyundan) ve testlerden çağrılabilmesi için izin veriyoruz
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(new AntPathRequestMatcher("/api/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
        );

        // Testlerin POST/PUT isteklerinde CSRF engeline takılmaması için API yollarında CSRF'yi devre dışı bırakıyoruz
        http.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")));

        super.configure(http);
        setLoginView(http, com.jollifiy.gameanalytics.views.LoginView.class);
    }

    @Bean
    public UserDetailsService users() {
        // Admin paneline giriş için örnek kullanıcı adı ve şifre
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}jollify123") // {noop} şifrenin şifrelenmeden (plain text) tutulmasını sağlar
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}