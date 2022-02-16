package kr.co.ecoletree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

@SpringBootApplication
public class AsynchronousProgrammingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsynchronousProgrammingApplication.class, args);
    }

}

@RestController
@RequestMapping("/")
class TestController {

    @GetMapping
    public DeferredResult<Map<String, Object>> defer() {
        final DeferredResult<Map<String, Object>> dr = new DeferredResult<>();
        return dr;
    }

    @PostMapping("/complete")
    public ResponseEntity<Void> complete(@RequestBody final Map<String, Object> param) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
