package com.rajnish.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Plan {

    private Long id;

    private String name;

    private String stripePriceId;

    private Integer maxProject;
    private Integer maxTokenPerDay;
    private Integer maxPreviews;
    private Boolean unlimitedAi;

    private Boolean active;

}
