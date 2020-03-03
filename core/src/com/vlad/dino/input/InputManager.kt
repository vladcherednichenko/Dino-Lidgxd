package com.vlad.dino.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.vlad.dino.Settings

class InputManager : InputProcessor{

    interface OnTouchListener{

        fun onTouch()

    }

    var onTouchListener : ()->Unit? = {}

    init{

        Gdx.input.inputProcessor = this

    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        if(Settings.cameraEnabled) camera?. touchDown(screenX, screenY, pointer, button)

        onTouchListener()

        return true

    }

    var camera: InputProcessor? = null


    fun handleCamera(camera: InputProcessor){

        this.camera = camera

    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun scrolled(amount: Int): Boolean {
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {

        if(Settings.cameraEnabled) camera?.touchDragged(screenX, screenY, pointer)



        return true

    }

    override fun keyDown(keycode: Int): Boolean {

        return true

    }


}