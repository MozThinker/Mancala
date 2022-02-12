package com.mutombene.mancala.controller;

import com.mutombene.mancala.controller.dto.ConnectRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mutombene
 */

@RestController
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
    public ResponseEntity<Game> connect (@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
        log.info("connect request: {}", request);
        return ResponseEntity.ok(gameServiceImpl.connectToGame(request.getPlayer(), request.getGameId()));
    }

    @PostMapping("/connect/random")
    public ResponseEntity<Game> connectRandom (@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException, NotFoundException {
        log.info("connect random : {}", request);
        return ResponseEntity.ok(gameServiceImpl.connectToRandomGame(request.getPlayer()));
    }

    @PostMapping("/gameplay")
    public ResponseEntity<Game> gamePlay (@RequestBody GamePlay request) throws NotFoundException, InvalidGameException, InvalidPlayerMoveException {

         log.info("gameplay: {}", request);

         Game game = gameServiceImpl.gamePlay(request);
         simpMessagingTemplate.convertAndSend("/topic/game-progress/"+game.getGameId(),game);

        return ResponseEntity.ok(game);
    }
}