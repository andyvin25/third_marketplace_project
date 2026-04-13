```md
# Marketplace Backend MVC Code Generation – Detailed Prompt Specification

## 1. Main Goal

I want to generate a **Spring Boot MVC backend structure** for my Marketplace application using a **vertical slicing architecture**.

I already have:
- Database tables
- Table relationships
- ERD (described using Eraser.io or SQL syntax)

You must:
- Generate code structure based on my table and ERD description
- Follow all default rules unless I explicitly override them
- Not change architectural rules unless I tell you
- Ask me if something is unclear (e.g., projection, soft delete, specific fields only, etc.)

You are NOT allowed to:
- Write code outside the defined layer responsibility
- Break the folder structure rules
- Inject repositories into other services (cross-feature injection not allowed)

---

# 2. Architecture Style

## Vertical Slicing Architecture

Each feature/module must have its own folder.

Example structure:

```

Banner
├── api
│   ├── BannerController.java
│   └── StoreProjection.java
└── domain
├── Banner.java
├── BannerPost.java
├── BannerPostRepository.java
├── Etalation.java
├── EtalationRepository.java
├── Store.java
├── StoreRepository.java
└── StoreService.java

CategoryManagement
├── api
│   ├── CategoryController.java
│   ├── CategoryDto.java
│   └── CategoryRequestDto.java
└── domain
├── Category.java
├── CategoryServiceImpl.java
└── CategoryService.java

```

Each feature:
- Contains `api` folder
- Contains `domain` folder
- Must be isolated from other features

---

# 3. Model (Entity) Rules

### Location
`feature-name/domain/`

### Naming Rules
- Class name must be singular
  - Example: table `Users` → class `User`
- Entity name may use prefix based on layer name if needed
- Table name follows database naming
- Class name must not use plural suffix `s`

### Default ID Generator

```

@Id
@GeneratedId
private String id;

```

If I want:
- EmbeddedId
- Sequence generator  
I will explicitly tell you later.

### Default Annotations
- `@Getter`
- `@Setter`
- `@AllArgsConstructor`

### Auditable Rule
If entity contains:
- `created_at`
- `updated_at`

Then:
- The entity must extend `Auditable`

### ERD Source
I will describe entities using:
- Eraser.io ERD
- SQL syntax

You must interpret from that.

---

# 4. Repository Rules

### Location
`feature-name/domain/`

### General Rules
- Use JPQL
- Default: No soft delete
- If soft delete is enabled (I will tell you), then:
  - Generate:
    - `getAll()`
    - `getAllWhereIsDeletedFalse()` using JPQL

### Property Selection
- By default: return all properties
- If I want specific properties only:
  - You must ask me

### Projection
If projection is needed:
- It must be defined in repository layer
- You must ask before generating it

---

# 5. Service Layer Rules

### Location
`feature-name/domain/`

### Injection Rule
- Use `@Autowired` (field injection)
- Do NOT use constructor injection in service

### Default CRUD
Generate:
- create
- getAll
- getById
- update
- delete

### Get By ID Rule (Mandatory Pattern)

If entity not found:
- Throw `ResourceDuplicationException`
Example:
```

throw new ResourceDuplicationException("Email already exists");

```

This rule applies for:
- getById
- getByEmail
- getByName

### Auto-Generate Extra Getters
If model contains:
- email → generate `getUserByEmail`
- name → generate `getUserByName`

### Anti-Duplication Rule

If entity has:
- `categoryName`
- `subCategoryName`
- or any similar suffix field

You must:
1. Generate anti-duplication method (boolean return)
2. Use stream + count
3. Use it inside create method

This rule is default.

---

## Service Restrictions

You are NOT allowed to:
- Inject another feature’s repository
- Inject another feature’s service
- Cross-call service methods between features

If dependency needed:
- It must be passed as parameter

Example:
UserService cannot inject RoleRepository  
UserService cannot call RoleService  

---

## Create Method Rule

Create method must:
- Use RequestDto as parameter

Example:
```

public void createCategory(CategoryRequestDto categoryDto)

```

---

# 6. DTO Rules

### Location
`feature-name/api/`

### Default Files
For model `Student`:

Generate:
- `StudentDto`
- `StudentRequestDto`

### Type
- Use Java `record`

### Default JSON Naming

Both DTOs must use:

```

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

```

Imports:
```

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

```

### Validation Rule

For all `String` fields:
- Use `@NotBlank`
- From `jakarta.validation.NotBlank`

(Default behavior unless I override.)

### Projection
If projection needed:
- Only define it in repository
- Ask me before generating

### Set Conversion Rule
If entity has `Set`:
- Convert to `List` in `toDto`

---

# 7. Mapper Rules

### Location
`feature-name/api/`

### Annotation
- `@Component`

### Required Methods
Each mapper must have:
1. `toModel`
2. `toDto`

### toModel Rules
- Must include related entity parameters
- If entity has `HashSet`, initialize using:
  - `new HashSet<>()`

Example behavior:
- If `User` has `Set<Role>`
- Initialize empty HashSet

### toDto Rules
- Include related entities
- Convert `Set` → `List`

---

# 8. Controller Rules

### Location
`feature-name/api/`

### Injection Rule
- Use constructor injection
- Use `final`
- Use `@AllArgsConstructor`

Example:
```

private final StoreService storeService;

```

### Restrictions
- Cannot call repository directly
- Must call service layer
- Must call mapper when needed

---

## Default Response Rule

All controller methods must return:

```

ResponseHandler.generateResponse("Register Successfully", HttpStatus.CREATED, "");

```

Unless I override message or status.

---

## Create Method Rule

POST method must use RequestDto:

Example:
```

public ResponseEntity<Object> createCategories(@RequestBody CategoryRequestDto categoryDtos)

```

---

# 9. Swagger Documentation Rules

Each controller method must include:

- `@Operation`
- `@ApiResponse`
- `@ApiResponses`

Must define:
- Success response
- Duplicate response
- Bad request response

Example structure:

- 201 → Success
- 302 → Duplicate resource
- 400 → Invalid value

---

# 10. Soft Delete (Optional Feature)

Default:
- No soft delete

If enabled:
- Repository must generate JPQL for `is_deleted = false`
- Service must follow soft delete logic

You must ask before implementing.

---

# 11. What I Will Provide Each Time

For each feature, I will provide:
- Entity structure (ERD or SQL)
- Table name
- Relationships
- Service method names
- Controller method names
- Whether soft delete is used
- Whether projection is used
- Whether specific properties only are required

---

# 12. Important Constraints Summary

You must:
- Follow vertical slicing strictly
- Keep feature isolation
- Use field injection in service
- Use constructor injection in controller
- Use RequestDto in create method
- Apply anti-duplication rule by default
- Apply snake_case JSON naming
- Convert Set to List in DTO
- Use ResourceDuplicationException pattern

You must NOT:
- Cross-inject repositories/services
- Change ID generator unless instructed
- Skip default annotations
- Generate unnecessary layers

---

End of Detailed Prompt Specification.
```