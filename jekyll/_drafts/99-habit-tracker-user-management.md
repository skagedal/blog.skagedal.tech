Use https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html#_request_matchers

			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/user/**").hasRole("USER")       
				.requestMatchers("/admin/**").hasRole("ADMIN")     
				.anyRequest().authenticated()                      
			)