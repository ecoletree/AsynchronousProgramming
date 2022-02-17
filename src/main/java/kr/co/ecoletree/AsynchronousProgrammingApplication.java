package kr.co.ecoletree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import kr.co.ecoletree.common.helper.MapBuilder;

@SpringBootApplication
public class AsynchronousProgrammingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsynchronousProgrammingApplication.class, args);
    }

}

@RestController
@RequestMapping("/")
class TestController {
    final ExecutorService es = Executors.newFixedThreadPool(2);

    private DeferredResult<Map<String, Object>> DEFERRED;

    @GetMapping
    public DeferredResult<Map<String, Object>> defer() {
        DEFERRED = new DeferredResult<>();
        return DEFERRED;
    }

    @GetMapping("/complete/{message}")
    public ResponseEntity<Void> complete(@PathVariable final String message) {
        Optional.of(DEFERRED).ifPresent(defer -> {
            defer.setResult(MapBuilder.of("message", message));
        });
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
