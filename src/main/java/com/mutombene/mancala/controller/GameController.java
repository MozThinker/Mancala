package com.mutombene.mancala.controller;

import com.mutombene.mancala.model.dto.ConnectRequestDTO;
import com.mutombene.mancala.exception.InvalidGameException;
import com.mutombene.mancala.exception.InvalidParamException;
import com.mutombene.mancala.exception.InvalidPlayerMoveException;
import com.mutombene.mancala.exception.NotFoundException;
import com.mutombene.mancala.model.Game;
import com.mutombene.mancala.model.GamePlay;
import com.mutombene.mancala.model.Player;
import com.mutombene.mancala.service.GameServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author mutombene
 */

@RestController
@Validated
@Slf4j
@AllArgsConstructor
@RequestMapping ("/game")
public class GameController {

    private final GameServiceImpl gameServiceImpl;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody Player player){

        log.info("start game request: {}",player);
        return ResponseEntity.ok(gameServiceImpl.createGame(player));
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody ConnectRequestDTO request) {
        try {
            log.info("connect request: {}", request);
            Game game = gameServiceImpl.connectToGame(request.getPlayer(), request.getGameId());
            return ResponseEntity.ok(game);
        } catch (InvalidGameException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/connect/random")
    public ResponseEntity<?> connectRandom(@RequestBody ConnectRequestDTO request) {
        try {
            log.info("connect random : {}", request);
            Game game = gameServiceImpl.connectToRandomGame(request.getPlayer());
            return ResponseEntity.ok(game);
        } catch (InvalidParamException | InvalidGameException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/gameplay")
    public ResponseEntity<?> gamePlay(@Valid @RequestBody GamePlay request) {
        try {
            log.info("gameplay: {}", request);
            Game game = gameServiceImpl.gamePlay(request);
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
            return ResponseEntity.ok(game);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidGameException | InvalidPlayerMoveException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}