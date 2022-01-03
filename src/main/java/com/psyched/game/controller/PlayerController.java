package com.psyched.game.controller;

import com.psyched.game.model.Player;
import com.psyched.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/players")
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @PostMapping("/players")
    public Player createPlayer(@Valid @RequestBody Player player) {
        return playerRepository.save(player);
    }

    @GetMapping("/players/{id}")
    public Player getPlayerById(@PathVariable(value = "id") Long id) throws Exception {
        return playerRepository.findById(id).orElseThrow(() -> new Exception("Player not found"));
    }

    @PutMapping("/players/{id}")
    public Player updatePlayer(@PathVariable(value = "id") Long id, @Valid @RequestBody Player player) throws Exception {
        Player p = playerRepository.findById(id).orElseThrow(() -> new Exception("Player not found"));

        p.setName(player.getName());
        return playerRepository.save(p);
    }

    @DeleteMapping("players/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable(value = "id") Long id) throws Exception {
        Player p = playerRepository.findById(id).orElseThrow(() -> new Exception("Player not found"));
        playerRepository.delete(p);
        return ResponseEntity.ok().build();
    }
}
