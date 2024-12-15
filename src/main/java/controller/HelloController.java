package controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import entity.Hello;
import repository.HelloRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hello")
@Tag(name = "Hello", description = "Hello API for managing messages")
public class HelloController {

    @Autowired
    private HelloRepository helloRepository;

    @GetMapping("/")
    public List<Hello> getAllMessages() {
        return helloRepository.findAll();
    }

    @PostMapping("/test")
    public void testDeserialization(@RequestBody String payload) {
        System.out.println("Received Payload: " + payload);
    }


    @PostMapping("/")
    public Hello createMessage(@RequestBody String message) {
        Hello h = new Hello();
        h.setMessage(message);
        return helloRepository.save(h);
    }


}