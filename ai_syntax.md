first prompt:
i want you to make my prompt detail, more understandable and also help planning, you shouldn't do any code at all. and your modified form should return to txt or .md

second prompt:

this is the syntax that i want to make it detail:

You need to act as my assistant the goal, i want to make a mvc code for my marketplace backend app. I have the table that i made, and also the relation, i want the code should have a separate file like model (for entity name you can get prefix with layer name for each entity, table name, for class name it should be without suffix s (for example table Users the class name will be User), and
the id generator should look like this by default

@Id
@GeneratedId
private String id;

and i will tell if i use embed id or sequence style generator later. And then for model i will use eraser io for erd or sql syntax to describe entity. By default for this file generate @Getter @Setter @AllArgsConstructor. And for each entity that has created_at and updated_at just use extend Auditable class on that entity class. File Location for model is in domain folder.

For repository generate jpql (by default i don't use soft delete, but if i use i will tell you later, so your task in repository generate code for get all, getAll with is_deleted false (if i tell only and it should be done with jpql). By default i want to get all property and i will tell you if i want certain property only and you can ask me for each conversation for this. And the file location for repository in domain folder.

For Service I want to use @Autowired for injection, by default generate crud and i will tell if i use soft delete (and you can ask me for each conversation for this) and not and you can use method get for crud from repository interface for get only. And you can call in each with the same model name (for example the model user in UserService). I want you to generate method for get by id (if there is no with certain id just throw ResourceDuplicationException that i Wrote (for example
throw new ResourceDuplicationException("Email already exists");
) if there is with that id return that id and this schema need to be applied when get with id, get by email or get by name, if there is name or email in model you need also to make getUserByName or getUserByEmail, getAll. You don't allow to inject another model repository (for example roleRepository in UserService).If there is suffix with name (for example categoryName, subCategoryName) you should generate anti duplication method, you can use stream and count and that method return a boolean -> you need to use that method in create method (this schema is default). And You can't call another repository into another service (for example user service need role service certain method and it can't call role service inside userService). You just need to call with parameter. And For Create method you need to use to model as parameter for example
public void createCategory(CategoryRequestDto categoryDto) {}. Location for service file is in domain folder.

For DTO, by default generate a record file (for example: the model name is Student, so the dto name will have 2 file that consist of studentDto and studentRequestDto) and then i want both of the records use
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) from
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming. For String by default you need to use @NotBlank from jakarta.validation.NotBlank (this schema is default, ask me later for each convtersation). And I will tell if i want more Dto files. You can call as parameter in service and in controller (if i need projection you just need to call it in repository (you can ask me for this for each conversation). toDto if there is a set just convert that into list. Location for dto is api folder.

For Mapper use @Component anotation and it should have 2 methods to the model and to model dto, for example like this

@Component
public class StoreMapper {

    public Store toStore(StoreRequestDTO storeDto, Account account, StoreDetail storeDetail) {
        return Store.builder()
            .name(storeDto.storeName())
            .account(account)
            .storeDetail(storeDetail)
            .build();
    }

    public StoreDto toStoreDto(Store store) {
        return new StoreDto(store.getId(), store.getName());
    }

}. you need to add toModel which is you need to generate also with related entity, if there is hashset in that entity just use new HashSet in this case is Role set (for example: public User toUserAccount(UserAccountCreationDTO accountDTO) { return new User(accountDTO.email(), accountDTO.name(), accountDTO.password(), new HashSet<>()); }). For toDto i want you to display role also if there is a hashSet you need to convert that into List. Location for mapper should be in api folder.

For controller should have CRUD and use constructor injection which is use final keyword @AllArgsConstructor, for example like this
private final StoreService storeService;. You don't allowed to call repository file in this file. For create method or post method in controller i want to use model name request dto in parameter for example     public ResponseEntity<Object> createCategories(@RequestBody CategoryRequestDto categoryDtos) {}. And you should address or call all the service in the layer and mapper in here. By default all method in controller need to  
return ResponseHandler.generateResponse("Register Successfully", HttpStatus.CREATED, "");. You also need to generate description and api respone for swagger, you just need to use @Operation, @ApiResponse annotation for example like this

@Operation(summary = "User can be register in here")
@ApiResponses(value = {
@ApiResponse(responseCode = "201", description = "Successfully to create users",
content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountDto.class)) }
),
@ApiResponse(responseCode = "302", description = "the email has already been taken",
content = @Content(
mediaType = "application/json",
schema = @Schema(
example = "{\"message\": \"Email already registered\", \"cause\": \"Duplicate resource\"}"
)
)
),
@ApiResponse(responseCode = "400", description = "Invalid inserted value",
content = { @Content(mediaType = "application/json", schema = @Schema(
example = "Invalid inserted value"
)) }
)
}).

I will provide service and controller method. Location for controller file should be in api folder. 

I want use folder structure vertical slicing architecture which is like this example

Banner
 ├── api
  ├── BannerController.java
  └── StoreProjection.java
 └── domain
     ├── Banner.java
     ├── BannerPost.java
     ├── BannerPostRepository.java
     ├── Etalation.java
     ├── EtalationRepository.java
     ├── Store.java
     ├── StoreRepository.java
     └── StoreService.java
├── CategoryManagement
 ├── api
  ├── CategoryController.java
  ├── CategoryDto.java
  └── CategoryRequestDto.java
 └── domain
     ├── Category.java
     ├── CategoryServiceImpl.java
     └── CategoryService.java

each layer represents features and then inside of that, also has api and domain
