package com.vbs.demo.controller;

import com.vbs.demo.dto.DisplayDto;
import com.vbs.demo.dto.LoginDto;
import com.vbs.demo.dto.UpdateDto;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.valueOf;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserRepo userRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userRepo.save(user);
        return "Signup Successfull";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto u) {
        User user = userRepo.findByUsername(u.getUsername());

        if (user == null) {
            return "User not found";
        }

        if (!u.getPassword().equals(user.getPassword())) {
            return "Password Incorrect";
        }
        if (!u.getRole().equals(user.getRole())) {
            return "Role Incorrect";
        }

        return String.valueOf(user.getId());
    }

    @GetMapping("/get-details/{id}")
    public DisplayDto display(@PathVariable int id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DisplayDto displayDto = new DisplayDto();
        displayDto.setUsername(user.getUsername());
        displayDto.setBalance(user.getBalance());
        return displayDto;
    }

    @PostMapping("/update")
    public String update(@RequestBody UpdateDto obj) {
        User user = userRepo.findById(obj.getId())
                .orElseThrow(() -> new RuntimeException("Mot Found"));
        if (obj.getKey().equalsIgnoreCase("name"))
        {
            if (user.getName().equals(obj.getValue())) return "Cannot Be Same";
            user.setName(obj.getValue());
        }
        else if (obj.getKey().equalsIgnoreCase("password"))
        {
            if (user.getPassword().equals(obj.getValue())) return "Cannot Be Same";
            user.setPassword(obj.getValue());
        }
        else if (obj.getKey().equalsIgnoreCase("email"))
        {
            if (user.getEmail().equals(obj.getValue())) return "Cannot Be Same";
            User user2 = userRepo.findByEmail(obj.getValue());
            if(user2 != null) return "Email Already Exits.";
            user.setEmail(obj.getValue());
        }
        else
        {
            return "Invalid Key";
        }
        userRepo.save(user);
        return "Update Successfully";

    }

    @PostMapping("/add")
    public String add(@RequestBody User user)
    {
        userRepo.save(user);
        return "Added Successfully";
    }

    @GetMapping("/users")
    public List<User> getAlluser(@RequestParam String sortBy,@RequestParam String order  )
    {
        Sort sort;
        if(order.equalsIgnoreCase("desc"))
        {
            sort = Sort.by(sortBy).descending();
        }
        else
        {
            sort = Sort.by(sortBy).ascending();
        }

        return userRepo.findAllByRole("customer",sort);
    }

    @GetMapping("/users/{keyword}")
    public List<User> getUsers(@PathVariable String keyword)
    {
        return userRepo.findAllByUsernameContainingIgnoreCaseAndRole(keyword,"customer");
    }







}
