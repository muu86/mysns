# Spring Bean Validation
 http 요청이 서버에 들어오면 어댑터가 요청을 처리한다. 어댑터는 컨트롤러에 존재하는 핸들러를 의미한다.
 핸들러는 사용자의 입력 데이터나 요청 데이터의 유효성을 검사할 수 있다. 그리고 이는 비지니스 로직과 분리되어야 한다.
 비지니스 로직이 관여하지 않도록 특정 규칙을 기반으로 데이터의 유효성만을 검사해야 한다. 비지니스 로직과 관련된 검증은 도메인 서비스 레이어에서 처리하도록 한다.
 
## 검증 규칙 정의
spring validation api가 제공하는 어노테이션을 사용하여 검증 규칙을 정의할 수 있다. 
record 클래스에도 적용이 가능할 지는 몰랐는데 검증 어노테이션이 파라미터 레벨에서도 사용 가능한 걸 확인한 후 적용해보았더니 잘 작동한다.
```java
import jakarta.validation.constraints.NotNull;

public record CreatePostRequest(

    @NotNull
    String content

) {
}

```

## 검증 오류 처리
@RequestBody 객체에 @Valid 어노테이션을 붙여주기만 하면 된다.
스프링이 해당 객체에 json 데이터를 매핑하기 전 검증을 수행하고 오류 발생 시 MethodArgumentNotValidException을 던진다.

```java
    public ResponseEntity<CreatePostResponse> createPostDto(
        
        @Valid @RequestBody CreatePostRequest createPostRequest
    
    ) {
        PostDto newPost = new PostDto(createPostRequest.content());
        PostDto savedPost = postService.createPostDto(newPost);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new CreatePostResponse("success"));
    }
```

@RestControllerAdvice 어노테이션은 애플리케이션 내에서 발생하는 예외 처리를 간편하게 도와준다.

AOP에서 Advice란 특정한 관심사를 처리하는 행동(코드블록)을 말한다. 
여기서 관심사는 예외의 발생이고 Advice는 발생한 예외를 처리하는 코드가 될 것이다.
여러 클래스에 중복적으로 존재해야 하는 코드를 들어내어 한 곳에서 관리할 수 있도록 하는 것이 AOP의 목적이다.

어노테이션을 붙이기만 하면 스프링이 해당 예외를 처리하는 Adivce를 실행한다.

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest()
            .body(exception.getMessage());
    }
}
```
