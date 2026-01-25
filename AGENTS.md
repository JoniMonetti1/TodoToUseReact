# Backend (Spring Boot) – Agent Instructions

> **Scope:** This file applies to the backend code under `TodoToUseReact/`.
> The monorepo root has global workflow rules; this file adds backend-specific guidance.

---

## 0) What this project is
Spring Boot backend for the Todo application (monorepo). This service exposes REST APIs consumed by the React UI.

## 1) Golden workflow (how to work here)
- **Plan → Implement → Run Tests → Fix → Finalize**.
- Keep patches focused: one change-set should target backend only.
- If a change requires UI updates, **stop after backend changes** and describe the required UI changes clearly.

## 2) Commands (run these; do not invent new ones)
> Use Maven/Gradle wrapper if present. Always prefer reproducible installs/tests.

### From this directory (`TodoToUseReact/`)
- **Tests:**
    - Maven: `./mvnw test` or `mvn test`
    - Gradle: `./gradlew test`
- **Run locally:**
    - Maven: `./mvnw spring-boot:run`
    - Gradle: `./gradlew bootRun`
- **Package:**
    - Maven: `./mvnw clean package`
    - Gradle: `./gradlew clean build`

### From monorepo root
- **Full stack:** `docker compose up --build`
- **Backend-only:** `docker compose up --build <backend_service_name>`

---

## 3) Project structure (follow the existing layout)
Keep changes consistent with the current package structure. Typical pattern:
- `.../controller` – REST controllers (thin)
- `.../service` – business logic
- `.../repository` – data access (Spring Data / JPA)
- `.../dto` – API I/O models (request/response)
- `.../entity` – persistence models
- `.../config` – Spring configuration
- `src/test/java/...` – tests

## 4) Boundaries (what NOT to do)
- Do **not** modify UI code in `front-todo-react-spring/` in the same patch.
- Do **not** commit secrets (tokens, credentials), `.env` files, or local machine configs.
- Do **not** change production-ish configs without calling it out explicitly.
- Avoid unnecessary new dependencies.

---

## 5) Code formatting & Java style
- **Indentation:** 4 spaces. Max line length: 120.
- **Typing:** Prefer explicit types over `var`.
- **Immutability:** Use `final` where possible; avoid mutating objects in streams.
- **Logic:** Prefer early returns; avoid deep nesting or unnecessary `else` blocks.
- **Comments:** Avoid obvious comments. Use only for complex logic or `given/when/then` in tests.

## 6) Spring conventions
- **DI:** Prefer constructor injection (Lombok `@RequiredArgsConstructor`).
- **Layering:** Controllers (mapping), Services (logic), Repositories (persistence).
- **Transactions:** Use `@Transactional` at the service layer.

## 7) Lombok (if the project uses Lombok)
- OK:
    - `@RequiredArgsConstructor` for DI
    - `@Slf4j` for logging
    - `@Builder(setterPrefix = "with")` for complex object creation
- Avoid:
    - `@Data` (prefer `@Getter` / `@Setter` for control)

## 8) DTOs, mapping, and API design
- Do not expose JPA entities directly in API responses.
- Keep request/response DTOs stable; prefer additive changes for backward compatibility.
- Use one mapping strategy consistently:
    - MapStruct (preferred for larger projects), or
    - Static mappers (small/explicit projects)
### Example: MapStruct mapper
```java
@Mapper(componentModel = "spring")
public interface TodoMapper {
  TodoDto toDto(TodoEntity entity);
  TodoEntity toEntity(CreateTodoRequest req);
}
```
### Example: Static mapper
```java
public final class TodoMapper {
    private TodoMapper() { throw new UnsupportedOperationException("No instances"); }

    public static TodoDto toDto(final TodoEntity e) {
        return e == null ? null : new TodoDto(e.getId(), e.getTitle(), e.isCompleted());
    }
}

```

## 9) Exception handling
- Prefer domain-specific unchecked exceptions (RuntimeException).
- Centralize HTTP error mapping using @ControllerAdvice.

### Example: global exception handler
```java
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(final NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("NOT_FOUND", ex.getMessage()));
    }
}
```

## 10) Logging (SLF4J)
- Use placeholders: `log.info("... {}", value)`.
- **Levels:** `INFO` (events), `DEBUG` (diagnostics), `WARN` (recoverable), `ERROR` (failures).
- **Security:** Never log secrets or sensitive payloads.

## 11) Testing (JUnit 5 + Mockito)
- Structure: **given / when / then**.
- Use `@WebMvcTest` for controllers and `@SpringBootTest` only for integration.
- Test behavior, not implementation details.

## 12) Security & validation
- Validate payloads with Bean Validation annotations + `@Validated`.
- Put method-level security at controller boundary (`@PreAuthorize`).

## 13) When you finish a task (Definition of Done)
- Code compiles and tests pass (`./mvnw test`).
- No secrets added.
- API changes documented in the PR/commit.
- If the UI must change, include a “UI follow-up” note with contract changes.

# Monorepo boundary reminder
This file governs only backend changes in TodoToUseReact/.
Do not modify UI files in front-todo-react-spring/ in the same patch.
