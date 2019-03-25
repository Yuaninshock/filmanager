package net.lzzy.filmanager.modeles;

import net.lzzy.sqllib.AsPrimaryKey;

import java.util.UUID;

/**
 *
 *
 */
public class BastEntity {
    @AsPrimaryKey
    UUID id;

    public BastEntity(){
        id=UUID.randomUUID();
    }

    public Object getIdentityValue() {
        return id;
    }

    public UUID getId() {
        return id;
    }
}
