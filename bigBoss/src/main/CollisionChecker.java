package main;

import entity.Entity;

/**
 * Handles Collision
 * 
 * @author Hasan Syed
 * @version 1.0
 */
public class CollisionChecker {
    gamePanel gp;

    CollisionChecker(gamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.position.x + entity.hitbox.x;
        int entityRightWorldX = entity.position.x + entity.hitbox.x + entity.hitbox.width;
        int entityTopWorldY = entity.position.y + entity.hitbox.y;
        int entityBottomWorldY = entity.position.y + entity.hitbox.y + entity.hitbox.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tile1, tile2;

        switch (entity.myDirection) {
            case facingUp -> {
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tile1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tile2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.Tiles.get(tile1).collidable || gp.tileM.Tiles.get(tile2).collidable) {
                    gp.player.collision = true;
                }
            }
            case facingDown -> {
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tile1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tile2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.Tiles.get(tile1).collidable || gp.tileM.Tiles.get(tile2).collidable) {
                    gp.player.collision = true;
                }
            }
            case facingLeft -> {
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tile1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tile2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.Tiles.get(tile1).collidable || gp.tileM.Tiles.get(tile2).collidable) {
                    gp.player.collision = true;
                }
            }
            case facingRight -> {
                entityRightCol = (entityRightWorldX - entity.speed) / gp.tileSize;
                tile1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tile2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.Tiles.get(tile1).collidable || gp.tileM.Tiles.get(tile2).collidable) {
                    gp.player.collision = true;
                }
            }
        }
    }
}
