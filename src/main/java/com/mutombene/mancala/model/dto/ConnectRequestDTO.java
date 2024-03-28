package com.mutombene.mancala.model.dto;

import com.mutombene.mancala.model.Player;
import lombok.Data;

/**
 * @author mutombene
 */
@Data
public class ConnectRequestDTO {
    Player player;
    String gameId;
}