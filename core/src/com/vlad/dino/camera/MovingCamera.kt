package com.vlad.dino.camera

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import kotlin.math.*


class MovingCamera(fieldOfView: Float, width: Float, height: Float) : PerspectiveCamera(fieldOfView.toFloat(), width.toFloat(), height.toFloat()), InputProcessor {

    var TARGET = Vector3(0f, 0f, 0f)
    var cameraDistance = 0f



    private var camRotationX = 0f
    private var camRotationY = 0f

    var mPreviousX = 0
    var mPreviousY = 0

    val DEG = (Math.PI / 180f).toFloat()

    override fun lookAt(x: Float, y: Float, z: Float) {
        super.lookAt(x, y, z)

        this.TARGET = Vector3(x, y, z)

        cameraDistance = sqrt(
                (position.x - TARGET.x).pow(2) +
                        (position.y - TARGET.y).pow(2) +
                        (position.z - TARGET.z).pow(2))

    }

    override fun lookAt(target: Vector3?) {
        super.lookAt(target)
        if(target != null){

            this.TARGET = target

        }

        cameraDistance = sqrt(
                (position.x - TARGET.x).pow(2) +
                        (position.y - TARGET.y).pow(2) +
                        (position.z - TARGET.z).pow(2))

        var a = 0;

    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {

        mPreviousX = x
        mPreviousY = y
        return true

    }


    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {

        val movingSensitivity = 50f

        var _dx = (x - mPreviousX) * (movingSensitivity / 100f)
        var _dy = (y - mPreviousY) * (movingSensitivity / 100f)

        camRotationX += _dx * movingSensitivity / 200
        camRotationY += _dy * movingSensitivity / 200

        mPreviousX = x
        mPreviousY = y

        val camY = sin(camRotationY * DEG.toDouble()).toFloat() * cameraDistance
        val temp = cos(camRotationY * DEG.toDouble()).toFloat() * cameraDistance
        val camX = sin(camRotationX * DEG.toDouble()).toFloat() * temp
        val camZ = cos(camRotationX * DEG.toDouble()).toFloat() * temp


        position.x = -camX
        position.y = camY
        position.z = camZ




        val camRZ = atan(camRotationY/camRotationX)
        val t = sqrt(cameraDistance.pow(2) - camRotationY.pow(2))
        val s = sqrt(cameraDistance.pow(2) - camRotationY.pow(2) - camRotationX.pow(2))
        val camRX = atan(camRotationY/s)
        val camRY = asin(camRotationX / t)

        //camera.rotate(-camRY, 0f, 1f, 0f)
//        camera.rotate(-_dy, 1f, 0f, 0f)
//        camera.rotate(-_dy, 0f, 0f, 1f)

        lookAt(TARGET)
        update()
        return true

    }


    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyTyped(character: Char): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun scrolled(amount: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyUp(keycode: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyDown(keycode: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}