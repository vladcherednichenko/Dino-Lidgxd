package com.vlad.dino

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model

class GameAssetManager{

    val assets: AssetManager by lazy{AssetManager()}
    private var loading = false

    fun loadAssets (){

        loading = true
        assets.load("dino/dino0.obj", Model::class.java)
        assets.load("dino/dino1.obj", Model::class.java)
        assets.load("dino/dino2.obj", Model::class.java)
        assets.load("dino/dino3.obj", Model::class.java)
        assets.load("dino/dino4.obj", Model::class.java)
        assets.load("dino/dino5.obj", Model::class.java)
        assets.load("dino/dino6.obj", Model::class.java)
        assets.load("ground/ground_sand.obj", Model::class.java)

    }

    fun assetsLoaded(): Boolean{

        if(assets.update() && loading){

            loading = false
            return true

        }else{

            return false

        }

    }

    fun <T : Any?> get(fileName: String?): T {
        return assets.get(fileName)
    }

    fun <T : Any?> get(fileName: String?, type: Class<T>?): T {
        return assets.get(fileName, type)
    }
}