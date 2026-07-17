package com.rajnish.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @ManyToOne
    @JoinColumn(nullable = false,name = "owner_id")
    User owner;
    Boolean isPublic=false;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
    Instant deletedAt;
}
