package com.vlad.dino.camera

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import kotlin.math.*


class ControllableCamera(fieldOfView: Float, width: Float, height: Float) : PerspectiveCamera(fieldOfView.toFloat(), width.toFloat(), height.toFloat()), InputProcessor {
    private var aspectRatio: Float
    private var x = -1
    private var y = -1
    private var dx = 0.0f
    private var dy = 0.0f
    private val tmp = Vector3()
    // fields related to pinch-to-zoom
    private var numberOfFingers = 0
    private var fingerOnePointer = 0
    private var fingerTwoPointer = 0
    private var lastDistance = 0f
    private val fingerOne = Vector3()
    private val fingerTwo = Vector3()

    private var camRotationX = 0f
    private var camRotationY = 0f

    fun pause() {
        numberOfFingers = 0
    }

    fun resize(width: Int, height: Int) {
        viewportWidth = width.toFloat()
        viewportHeight = height.toFloat()
        aspectRatio = viewportHeight /viewportWidth.toFloat()
        update()
    }

    fun render() {
        if (isSingleTouched) { // This gets the change in touch position and
// compensates for the aspect ratio.
            if (x == -1 || y == -1 || justSingleTouched) {
                x = Gdx.input.x
                y = Gdx.input.y
            } else {
                dx = (x - Gdx.input.x).toFloat()
                dy = (y - Gdx.input.y) / aspectRatio
            }
            // This zooms when control is pressed.
            if (controlIsPressed && dy > 0) {
                scrollIn()
            } else if (controlIsPressed && dy < 0) {
                scrollOut()
            } else if (shiftIsPressed) {
                translateTangentially(0f, 0f)
            } else {
                travelAround()
            }
            x = Gdx.input.x
            y = Gdx.input.y
            justSingleTouched = false
        }
        // this zooms when the mouse wheel is rotated
        if (isScrollingUp) {
            scrollIn()
            isScrollingUp = false
        } else if (isScrollingDown) {
            scrollOut()
            isScrollingDown = false
        }
        // Some key controls
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            translateTangentially(1f, 0f)
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.D)) {
            translateTangentially(-1f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            translateTangentially(0f, 1f)
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)
                || Gdx.input.isKeyPressed(Input.Keys.S)) {
            translateTangentially(0f, -1f)
        }
        update()
    }

    // These methods create the pinch zoom
// and set some flags for logic in render method.
    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean { // for pinch-to-zoom
        numberOfFingers++
        if (numberOfFingers == 1) {
            isSingleTouched = true
            justSingleTouched = true
            fingerOnePointer = pointer
            fingerOne[x.toFloat(), y.toFloat()] = 0f
        } else if (numberOfFingers == 2) {
            isSingleTouched = false
            fingerTwoPointer = pointer
            fingerTwo[x.toFloat(), y.toFloat()] = 0f
            val distance = fingerOne.dst(fingerTwo)
            lastDistance = distance
        }
        return true
    }

    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        if (numberOfFingers > 1) {
            if (pointer == fingerOnePointer) {
                fingerOne[x.toFloat(), y.toFloat()] = 0f
            }
            if (pointer == fingerTwoPointer) {
                fingerTwo[x.toFloat(), y.toFloat()] = 0f
            }
            val distance = fingerOne.dst(fingerTwo)
            if (lastDistance > distance) {
                scrollOut()
            } else if (lastDistance < distance) {
                scrollIn()
            }
            lastDistance = distance
            update()
        }
        return true
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        isSingleTouched = false
        if (numberOfFingers == 1) {
            val touchPoint = Vector3(x.toFloat(), y.toFloat(), 0f)
            unproject(touchPoint)
        }
        numberOfFingers--
        // just some error prevention... clamping number of fingers (ouch! :-)
        if (numberOfFingers < 0) {
            numberOfFingers = 0
        }
        lastDistance = 0f
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyTyped(character: Char): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // These methods set flags for logic in render method.
    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> shiftIsPressed = true
            Input.Keys.CONTROL_LEFT, Input.Keys.CONTROL_RIGHT -> controlIsPressed = true
            Input.Keys.O -> {
                this.up.set(0.0f, 1.0f, 0.0f)
                this.position.set(0.0f, 0.0f, 30.0f)
                this.lookAt(0f, 0f, 0f)
                this.update()
            }
        }
        return true
    }

    override fun keyUp(arg0: Int): Boolean {
        controlIsPressed = false
        shiftIsPressed = controlIsPressed
        return true
    }

    override fun scrolled(direction: Int): Boolean {
        if (direction == -1) {
            isScrollingUp = true
        } else if (direction == 1) {
            isScrollingDown = true
        }
        return true
    }

    // The rest of the methods translate the camera.
    fun scrollIn() {
        val magnitude = 1.0f
        scrollIn(magnitude)
    }

    fun scrollIn(magnitude: Float) {
        if (position.dst2(ORIGIN) > 2.0f) {
            tmp.set(position)
            tmp.nor()
            this.translate(-tmp.x * magnitude, -tmp.y * magnitude, -tmp.z
                    * magnitude)
            update()
        }
    }

    fun scrollOut() {
        val magnitude = 1.0f
        scrollOut(magnitude)
    }

    fun scrollOut(magnitude: Float) {
        tmp.set(position)
        tmp.nor()
        this.translate(tmp.x * magnitude, tmp.y * magnitude, tmp.z * magnitude)
        update()
    }

    private fun travelAround() {

//        println("dx $dx")
//        println("dy $dy")
//
        val DEG = (Math.PI / 180f).toFloat()

        var cameraDistance = 20f
//
        camRotationX += dx / 10
        camRotationY += dy / 10
//
//        val camY = sin(camRotationY * DEG.toDouble()).toFloat() * cameraDistance
//        val temp = cos(camRotationY * DEG.toDouble()).toFloat() * cameraDistance
//        val camX = sin(camRotationX * DEG.toDouble()).toFloat() * temp
//        val camZ = cos(camRotationX * DEG.toDouble()).toFloat() * temp
//
//        position.x = camX
//        position.y = camY
//        position.z = camZ

//        tmp.set(up)
//        rotateAround(ORIGIN, tmp, dx)

        var yAxis = Vector3(0f, 1f, 0f)
        var xAxis = Vector3(1f, 0f, 0f)
        var zAxis = Vector3(0f, 0f, 1f)


        val camZ = atan(dy/dx)
        val t = sqrt(cameraDistance.pow(2) - dy.pow(2))
        val s = sqrt(cameraDistance.pow(2) - dy.pow(2) - dx.pow(2))
        val camX = atan(dy/s)
        val camY = asin(dx / t)

//        rotateAround(ORIGIN, xAxis, camX)
//        rotateAround(ORIGIN, yAxis, camY)
//        rotateAround(ORIGIN, zAxis, camZ)


        rotateAround(ORIGIN, yAxis, dx)
        rotateAround(ORIGIN, xAxis, dy)

//        view.rotate(1f, 0f, 0f, dy)
//        view.rotate(0f, 1f, 0f, dx)

//        translate(camX, camY, camZ)

//        rotateAround(ORIGIN, yAxis, camX)
//        rotateAround(ORIGIN, xAxis, camY)
//        rotateAround(ORIGIN, xAxis, camZ)


        //lookAt(0f, 0f, 0f)


    }

    private fun translateTangentially(dx: Float , dy: Float) {
        tmp.set(up)
        tmp.crs(position)
        if (dx > 0) {
            translate(tmp.x / 15.0f, tmp.y / 15.0f, tmp.z / 15.0f)
        } else if (dx < 0) {
            translate(-tmp.x / 15.0f, -tmp.y / 15.0f, -tmp.z / 15.0f)
        }
        if (dy > 0) {
            translate(-up.x, -up.y, -up.z)
        } else if (dy < 0) {
            translate(up)
        }
    }

    companion object {
        val ORIGIN = Vector3(0f, 0f, 0f)
        private var shiftIsPressed = false
        private var controlIsPressed = false
        private var isScrollingUp = false
        private var isScrollingDown = false
        private var isSingleTouched = false
        private var justSingleTouched = false
    }

    init {
        aspectRatio = viewportHeight / viewportWidth.toFloat()
        Gdx.input.setInputProcessor(this)
        up.set(0.0f, 1.0f, 0.0f)
        position.set(0.0f, 0.0f, 30.0f)
        far = 300.0f
        lookAt(0f, 0f, 0f)
        translate(0.0f, 0.0f, 2.1f)
        lookAt(0f, 0f, 0f)
        update()
    }
}