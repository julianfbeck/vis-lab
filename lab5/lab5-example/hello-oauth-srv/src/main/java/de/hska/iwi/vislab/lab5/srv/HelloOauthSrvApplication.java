package de.hska.iwi.vislab.lab5.srv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableResourceServer
@RestController
public class HelloOauthSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloOauthSrvApplication.class, args);
	}
	//service Darstellen
	//einfacher statischer service
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		return "Hello Oauth!";
	}
	//HTTP Basic AUTH -> Fenster mit NAME und PW
	@Configuration
	@EnableAuthorizationServer
	protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security.checkTokenAccess("isAuthenticated()");
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			//DREI client, wir benutzen ersten.
			clients.inMemory()
				.withClient("my-trusted-client")
					.authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
					.scopes("read", "write", "trust")
					.resourceIds("oauth2-resource")
					.accessTokenValiditySeconds(600)
			.and()
				.withClient("my-client-with-registered-redirect")
					.authorizedGrantTypes("authorization_code")
					.authorities("ROLE_CLIENT")
					.scopes("read", "trust")
					.resourceIds("oauth2-resource")
					.redirectUris("http://anywhere?key=value")
			.and()
				.withClient("my-client-with-secret")
					.authorizedGrantTypes("client_credentials", "password")
					.authorities("ROLE_CLIENT")
					.scopes("read")
					.resourceIds("oauth2-resource")
					.secret("secret");
			// @formatter:on
		}

	}
}
