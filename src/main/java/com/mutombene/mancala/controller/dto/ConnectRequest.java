package com.mutombene.mancala.controller.dto;

import com.mutombene.mancala.model.Player;
import lombok.Data;

/**
 * @author mutombene
 */
@Data
public class ConnectRequest {
    Player player;
    String gameId;
}