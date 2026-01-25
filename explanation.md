# Deep Dive: Spring Security, Sessions, and Cookies in your Todo App

This document provides a comprehensive explanation of how the authentication system in your Spring Boot + React application works. It covers the backend mechanics, the role of cookies, and how the frontend communicates securely with the server.

---

## 1. Overview of the Authentication System

Your application uses **Session-Based Authentication**. 
- Instead of sending a token (like JWT) with every request manually, the server creates a **Session** on the server side and identifies it with a unique ID stored in a **Cookie** on the client side.
- This cookie is managed by the browser automatically once it is set.

---

## 2. Spring Security Architecture

Spring Security is built on a series of **Servlet Filters**. When a request comes to your server, it passes through these filters before reaching your `AuthController` or `TodoController`.

### A. The Security Filter Chain (`SecurityFilterChain`)
In `SecurityConfig.java`, you configured the filter chain:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
        .csrf(AbstractHttpConfigurer::disable) // CSRF disabled for simplicity (see Security Considerations)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/login", "/auth/register").permitAll() // Public endpoints
            .anyRequest().authenticated() // Everything else requires a session
        )
        // ... logout and session management
}
```
- **Filter Order:** Spring Security checks if the request is for a public URL. If not, it checks if there is a valid session (via the cookie). If no session exists, it returns `401 Unauthorized` or redirects to a login page (though in a REST API, we usually return 401).

### B. AuthenticationManager & UserDetailsService
- **`AuthenticationManager`**: The "coordinator". It takes your login credentials (email/password) and tries to verify them.
- **`DbUserDetailsServiceImpl`**: Implements `UserDetailsService`. Its job is to talk to the database (`UserRepository`) to find the user by email and return a `UserDetails` object that Spring Security understands.
- **`PasswordEncoder` (BCrypt)**: Used to securely compare the raw password from the login request with the hashed password in the database.

### C. SecurityContextHolder
This is where Spring Security "remembers" who is logged in during a single request.
- When `AuthServiceImpl.login()` is called, we manually set the authentication in the context:
  ```java
  SecurityContextHolder.getContext().setAuthentication(authentication);
  ```
- Behind the scenes, Spring Security takes this "authenticated" state and saves it into the **HTTP Session**.

---

## 3. HTTP Sessions & Cookies

### What is a Session?
HTTP is **stateless**, meaning the server forgets who you are the moment a request is finished. A **Session** allows the server to store information about a user across multiple requests.

1. **Server Side:** When a user logs in, Spring Boot creates a session object in its memory (or database).
2. **Client Side:** The server sends a response header: `Set-Cookie: JSESSIONID=ABC123XYZ; HttpOnly; Path=/`.
3. **Subsequent Requests:** The browser automatically includes `Cookie: JSESSIONID=ABC123XYZ` in every request to your domain.

### The JSESSIONID Cookie
This is the "Key" to your session. 
- The browser sees the `JSESSIONID` and knows it belongs to your backend domain.
- When the backend receives the request, it extracts the ID, looks up the session in its memory, and sees: "Ah, this is User 5! I'll put their info into the `SecurityContextHolder` for this request."

### Why HttpOnly?
In `SecurityConfig.java`, we rely on Spring Boot's default behavior where the `JSESSIONID` is **HttpOnly**.
- **HttpOnly** means JavaScript code (like `document.cookie`) **cannot** access the cookie.
- **Benefit:** If an attacker manages to inject a malicious script (XSS) into your React app, they cannot steal your session cookie. This is a major security advantage over storing JWTs in `localStorage`.

---

## 4. Detailed Authentication Flow

### 1. Registration (`POST /auth/register`)
- React sends `email` and `password`.
- `AuthService` hashes the password using `BCrypt`.
- User is saved to the database.

### 2. Login (`POST /auth/login`)
- React sends credentials.
- `AuthenticationManager` verifies them against the database.
- `SecurityContextHolder.getContext().setAuthentication(...)` is called.
- Spring Security creates a session and sends back the `JSESSIONID` cookie in the response.

### 3. Authenticated Requests (e.g., `GET /todos`)
- Browser automatically attaches the `JSESSIONID` cookie.
- Spring's `SessionManagementFilter` sees the cookie, finds the session, and populates the `SecurityContextHolder`.
- `TodoController` can now safely serve data because the user is "authenticated".

### 4. Who am I? (`GET /auth/me`)
- This endpoint is crucial for React. On page refresh, React "forgets" if the user is logged in.
- React calls `/auth/me`. If the cookie is valid, the server returns the user info. If not, it returns `401`.

### 5. Logout (`POST /auth/logout`)
- The server clears the `SecurityContextHolder`.
- The session is invalidated on the server.
- The `JSESSIONID` cookie is deleted from the browser.

---

## 5. Communication with React (Frontend)

To make this work across different "origins" (React on port 5173, Spring on 8080), two things are required:

### A. CORS Configuration (`SecurityConfig.java`)
```java
configuration.setAllowedOrigins(List.of("http://localhost:5173"));
configuration.setAllowCredentials(true); // ALLOW COOKIES
```
Browsers block cookies from being sent to a different domain/port unless the server explicitly allows "credentials".

### B. React Request Configuration
When using `axios` or `fetch`, you **must** tell the browser to include the cookies:

**Using Axios:**
```javascript
const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true // MANDATORY: This sends the JSESSIONID cookie
});
```

**Using Fetch:**
```javascript
fetch('http://localhost:8080/auth/me', {
  credentials: 'include' // MANDATORY
});
```

---

## 6. Security Considerations

### XSS (Cross-Site Scripting)
Because we use **HttpOnly** cookies, we are well-protected against XSS attacks trying to steal session identifiers.

### CSRF (Cross-Site Request Forgery)
CSRF is an attack where a malicious site tricks your browser into sending a request to your backend (since the browser attaches cookies automatically).
- In this project, `csrf` is currently **disabled** for ease of development. 
- **Production Tip:** For high-security apps, you should enable CSRF protection. Spring Security handles this by requiring a special "CSRF Token" header that JavaScript must send, which prevents malicious sites from making unauthorized requests.

### SameSite Cookie Attribute
Modern browsers use `SameSite=Lax` by default, which adds a layer of protection against CSRF by not sending cookies on "cross-site" POST requests initiated by third-party websites.

---

## 7. Summary Table

| Feature | Description |
| :--- | :--- |
| **Storage** | `JSESSIONID` in a Browser Cookie. |
| **Accessibility** | `HttpOnly` (Not accessible by JavaScript). |
| **State** | Managed by Spring Session on the server. |
| **Lifecycle** | Ends on Logout or Session Timeout. |
| **Frontend Requirement** | Must use `withCredentials: true`. |
