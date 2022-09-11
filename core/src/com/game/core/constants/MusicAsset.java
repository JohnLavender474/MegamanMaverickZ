package com.game.core.constants;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum MusicAsset {

    MM2_BOSS_INTRO("MM2_Boss_Intro.mp3"),
    MMZ_NEO_ARCADIA_MUSIC("MMZ_NeoArcadia.mp3"),
    STAGE_SELECT_MM3_MUSIC("StageSelectMM3.mp3"),
    MMX3_INTRO_STAGE_MUSIC("MMX3_IntroStage.ogg"),
    MM11_MAIN_MENU_MUSIC("MM11_Main_Menu.mp3"),
    MM11_WILY_STAGE_MUSIC("MM11_Wily_Stage.mp3"),
    XENOBLADE_GAUR_PLAINS_MUSIC("Xenoblade_GaurPlains.ogg"),
    MMX_LEVEL_SELECT_SCREEN_MUSIC("MMX_LevelSelectScreen.ogg");

    private static final String prefix = "music/";

    private final String src;

    public String getSrc() {
        return prefix + src;
    }

}
