package com.vlad.dino.character

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

class CharacterManager {

    private lateinit var assets : AssetManager

    val instances = arrayListOf<ModelInstance>()
    val runningAnimationFrames = arrayListOf<ModelInstance>()

    private var initPosition = Vector3(0f, 0f, 0f)
    var position = Vector3(0f, 0f, 0f)
    var scale = 3f


    // Jump

    var jumpStartSpeed = 15f
    var jumpTime = 0f
    var isJumping = false
    var isRunning = true

    // Run animation

    var runningAnimationTime = 0.4f
    var runningAnimationTimePassed = 0f

    fun createCharacter(position: Vector3, assets: AssetManager){

        this.assets = assets
        this.initPosition = position
        this.position = initPosition.cpy()


        createRunningAnimation()

    }

    fun createRunningAnimation(){

        val runningAnimationFiles = arrayListOf<String>(
                "dino/dino0.obj",
                "dino/dino1.obj",
                "dino/dino2.obj",
                "dino/dino3.obj",
                "dino/dino4.obj",
                "dino/dino5.obj",
                "dino/dino6.obj"
        )

        runningAnimationFiles.forEach {filename->

            val dinoModel = assets.get(filename, Model::class.java)
            runningAnimationFrames.add(ModelInstance(dinoModel, position))

        }

        runningAnimationFrames.forEach {

            it.transform.scale(scale, scale, scale)

        }


    }

    fun jump(){

        if(isJumping) return

        isJumping = true
        isRunning = false
        jumpTime = 0f

    }

    fun update(deltaTime: Float){

        if(runningAnimationFrames.size == 0) return

        if(isJumping){

            jumpTime += deltaTime * 4

            position.y = initPosition.y + getYPositionCurrentTime(jumpTime)
            if(position.y < initPosition.y){

                isJumping = false
                isRunning = true
                position.y = initPosition.y

            }

            updatePosition(position)

        }

        if(isRunning){

            runningAnimationTimePassed += deltaTime

            if(runningAnimationTimePassed > runningAnimationTime){ runningAnimationTimePassed = 0f }

            animateRun(runningAnimationTimePassed)

        }


    }

    private fun animateRun(dt: Float){

        val frames = runningAnimationFrames.size
        val timePerFrame = runningAnimationTime / frames
        val currentFrame =  floor(dt / timePerFrame).roundToInt()
        instances.clear()
        instances.add(runningAnimationFrames[currentFrame])

    }

    private fun updatePosition(position: Vector3){

        instances.forEach {

            it.transform.setTranslation(position)

        }

    }

    private fun getYPositionCurrentTime(dt: Float): Float{

        return jumpStartSpeed * dt - 9.8f * dt.pow(2) / 2

    }



}