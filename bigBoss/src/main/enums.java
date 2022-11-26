package main;

public class enums {
    public enum spriteAdrs {
        upSpriteAdrs,
        downSpriteAdrs,
        leftSpriteAdrs,
        rightSpriteAdrs
    }

    public enum abilitySpriteAddress {
        abilityIcon,
        abilitySprite
    }

    public enum characterDirection {
        facingUp,
        facingDown,
        facingLeft,
        facingRight
    }

    public enum collisionPenalty {
        none,
        health,
        speed
    }

    public enum camera {
        locked,
        unlocked
    }

    public enum cameraMovement {
        cameraUp,
        cameraDown,
        cameraLeft,
        cameraRight
    }

    public enum entityTypeEnum {
        player,
        ai
    }

    public enum responseTypeEnum {
        fromServer,
        toServer
    }
}
