//package com.intentionman.vkselectiontask.config;
//
//import com.intentionman.vkselectiontask.services.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
//
//
//@Configuration
//@RequiredArgsConstructor
//@EnableWebSecurity(debug = true)
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    private final AuthService authService;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .mvcMatchers("/users/**").hasAnyRole("ROLE_USERS", "ROLE_ADMIN")
//                .mvcMatchers("/posts/**").hasAnyRole("ROLE_POSTS", "ROLE_ADMIN")
//                .mvcMatchers("/albums/**").hasAnyRole("ROLE_ALBUMS", "ROLE_ADMIN")
//                .mvcMatchers("/auth/**").permitAll()
//                .and().cors().and().csrf().disable()
//                .logout(config ->
//                        config.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
//                );
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(authService)
//                .passwordEncoder(MapperConfig.encoder());
//    }
//
//
////    @Bean
////    public RoleHierarchy roleHierarchy() {
////        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
////        String hierarchy = "ROLE_ADMIN > ROLE_USERS \n ROLE_ADMIN > ROLE_POSTS \n ROLE_ADMIN > ROLE_ALBUMS";
////        roleHierarchy.setHierarchy(hierarchy);
////        return roleHierarchy;
////    }
////
////    @Bean
////    PasswordEncoder passwordEncoder() {
////        return NoOpPasswordEncoder.getInstance();
////    }
////
////    @Override
////    @Bean
////    protected AuthenticationManager authenticationManager() throws Exception {
////        return super.authenticationManager();
////    }
//}
