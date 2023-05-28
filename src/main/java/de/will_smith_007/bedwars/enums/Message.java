package de.will_smith_007.bedwars.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {

    PREFIX("§f[§bBedWars§f] §7");

    private final String content;

    @Override
    public String toString() {
        return content;
    }
}
