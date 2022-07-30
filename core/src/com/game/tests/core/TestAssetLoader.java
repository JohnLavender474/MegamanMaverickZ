package com.game.tests.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.game.core.IAssetLoader;

import static com.game.ConstVals.MusicAssets.MMZ_NEO_ARCADIA_MUSIC;
import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.*;

public class TestAssetLoader implements IAssetLoader, Disposable {

    private final AssetManager assetManager = new AssetManager();

    public TestAssetLoader() {
        loadAssets(Music.class,
                MMZ_NEO_ARCADIA_MUSIC);
        loadAssets(Sound.class,
                ENEMY_BULLET_SOUND,
                ENEMY_DAMAGE_SOUND,
                MEGA_BUSTER_CHARGING_SOUND,
                MEGA_BUSTER_BULLET_SHOT_SOUND,
                MEGAMAN_LAND_SOUND,
                MEGAMAN_DEFEAT_SOUND,
                WHOOSH_SOUND,
                THUMP_SOUND,
                ACID_SOUND,
                AIR_SHOOTER_SOUND,
                ATOMIC_FIRE_SOUND,
                CRASH_BOMBER_SOUND);
        loadAssets(TextureAtlas.class,
                CUSTOM_TILES_TEXTURE_ATLAS,
                FIRE_TEXTURE_ATLAS,
                MET_TEXTURE_ATLAS,
                ENEMIES_TEXTURE_ATLAS,
                OBJECTS_TEXTURE_ATLAS,
                MEGAMAN_TEXTURE_ATLAS,
                MEGAMAN_FIRE_TEXTURE_ATLAS,
                DECORATIONS_TEXTURE_ATLAS,
                MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS);
        assetManager.finishLoading();
    }

    private <S> void loadAssets(Class<S> sClass, String... sources) {
        for (String source : sources) {
            assetManager.load(source, sClass);
        }
    }

    @Override
    public <T> T getAsset(String key, Class<T> tClass) {
        return assetManager.get(key, tClass);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

}
