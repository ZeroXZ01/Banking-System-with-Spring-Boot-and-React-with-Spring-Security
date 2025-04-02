package com.roland.training.model;

import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String contactNo;
    private String pin;
    private Date dob;
    private int age;

}
