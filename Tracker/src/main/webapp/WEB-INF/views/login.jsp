<%@include file="/WEB-INF/views/template/header.jsp"  %>

		<!-- Nav -->
		<nav id="nav" class="navbar nav-transparent">
			<div class="container">

				<div class="navbar-header">
					<!-- Logo -->
					<div class="navbar-brand">
						<a href="index.html">
							<img class="logo" src='<c:url value="/resources/img/logo.png" />' alt="logo">
							<img class="logo-alt" src='<c:url value="/resources/img/logo-alt.png" />' alt="logo">
						</a>
					</div>
					<!-- /Logo -->
				</div>

			</div>
		</nav>
		<!-- /Nav -->

		<!-- home wrapper -->
		<div class="home-wrapper">
			<div class="container">
				<div class="row">
					<div class="col-lg-6">
						<!-- Section-header -->
						<div class="section-header text-center">
							<h2 class="title white-text" >Login</h2>
						</div>
						<!-- /Section-header -->
						
						<!-- Error-message -->
						<c:if test="${not empty error}">
							<h2 class="text-center red-text">Invalid username or password</h2>
						</c:if>
						<!-- /Error-message -->
						
						<!-- login form -->
						<div class="col-lg-6 col-lg-offset-3">
							<form name="loginForm" action='<c:url value="/j_spring_security_check" />' class="contact-formss" method="post">
								<input type="text" id="username" name="username" class="input" placeholder="Username" style=" margin-bottom: 15px;">
								<input type="password" name="password" class="input" placeholder="Password" style=" margin-bottom: 15px;">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" >
								<button class="main-btn">login</button>
							</form>
						</div>
						<!-- /login form -->
					</div>
					<div class="col-lg-6">
						<!-- Section-header -->
						<div class="section-header text-center">
							<h2 class="title white-text" >Sing up</h2>
						</div>
						<!-- /Section-header -->
		
						<!-- sing up form -->
						<div class="col-lg-6 col-lg-offset-3">
							<form:form action="${pageContext.request.contextPath}/register" method="POST" modelAttribute="user">
								<form:input type="text" path="username" class="input" placeholder="Username" style=" margin-bottom: 15px;" />
								<form:input type="password" path="password" class="input" placeholder="Password" style=" margin-bottom: 15px;" />
								<form:input type="email" path="email" class="input" placeholder="Email" style=" margin-bottom: 15px;" />
								<button class="main-btn">sing up</button>
							</form:form>
						</div>
						<!-- /sing up form -->
					</div>
				</div>
			</div>
		</div>
		<!-- /home wrapper -->

	</header>
	<!-- /Header -->

<%@include file="/WEB-INF/views/template/footer.jsp"  %>