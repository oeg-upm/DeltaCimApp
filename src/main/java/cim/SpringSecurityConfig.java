package cim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import cim.service.UserDetailsServiceCIM;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public UserDetailsServiceCIM userDetailsService;
	 
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());     
	}
   
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/", "/login", "/api/login", "/css/**", "/images/**", "/vendor/**", "/scss/**", "/api/sparql").permitAll() 
                //.antMatchers("/acl*").access("hasRole('ADMIN')")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/api/login")
                .defaultSuccessUrl("/dashboard")
                .failureUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
                .and()
            .logout()                                    
            	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            	.logoutSuccessUrl("/")
            	.permitAll();
    }
    
     
    
}