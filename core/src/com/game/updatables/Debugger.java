package com.game.updatables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Debugger {

    private boolean debug = true;

    public void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

}
