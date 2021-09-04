package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;




@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
	private ImplemetacaoUserDetailsService implemetacaoUserDetailsService;	
	
	@Override//Configurar as solicitações de acesso por http
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf()
		 .disable()//desabilita padrão de memoria
		 .authorizeRequests() //permitir restringir acesso
		 .antMatchers(HttpMethod.GET,"/").permitAll()//qualquer usuario acessa apagina inicail
		 .antMatchers(HttpMethod.GET,"/cadastropessoa").hasAnyRole("ADMIN")//Da acesso somente ao Admin
		 .anyRequest().authenticated()
		 .and().formLogin().permitAll()//permite qualquer usuario
		 .and().logout()//mapeia url de sair do sistema e invalida o usuario
		 .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}
	
	@Override //cria autenticação do usuario com o banco de dados ou em memoria
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(implemetacaoUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
		
		/*auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		.withUser("ben")
		.password("$2a$10$C3yofrQwX5t8Ph1usVqjUe7fOjXNIbKrcmpPTSsnxat9nRp3l0lVm")//1040
		.roles("Admin");*/
	}
	
	@Override   //ignora url especificas
	public void configure(WebSecurity web) throws Exception {
		
		web.ignoring().antMatchers("/materialize/***");
		
	}
	
}
