package com.psyched.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name= "players")
public class Player extends Auditable{

    @Getter
    @Setter
    @NotBlank
    @Column(nullable = false, unique = true)
    private String userName;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @NotBlank
    private String name;

    @Getter
    @Setter
    @NotBlank
    @NonNull
    private String psychFaceURL;

    @Getter
    @Setter
    @NotBlank
    @NonNull
    private String picURL;

    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Stats stats = new Stats();

    @ManyToMany(mappedBy = "players")
    @Getter
    @Setter
    @JsonIgnore
    private List<Game> games;

    public void updatePlayerStats(Stats statsToMerge){
        stats.increaseCorrectAnswers(statsToMerge.getCorrectAnswers());
        stats.increaseGotPsychedCount(statsToMerge.getGotPsychedCount());
        stats.increasePsychedOthersCount(statsToMerge.getPsychedOthersCount());
    }
}
