package com.vlad.dino

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox

class GroundManager {

    val instances = arrayListOf<ModelInstance>()

    val groundScale = 0.3f
    val groundParts = 12
    var groundPartLength = 0f
    val movementSpeed = 50f

    var groundModel : Model? = null

    val groundCenter = Vector3(130f, 0f, 0f)
    val partsPositions = arrayListOf<Vector3>()
    val groundInitRotationY = 90f

    val minVisibleBounds = -70f
    val maxVisibleBounds = 1000

    fun createGround(height: Float, assets: AssetManager){

        groundCenter.y = height
        groundModel = assets.get("ground/ground_sand.obj", Model::class.java)


        generateInitPositions()

        generateParts()

        
    }

    private fun generateParts(){

        partsPositions.forEach{center->

            instances.add(ModelInstance(groundModel, center))

        }

        instances.forEach {

            it.transform.scale(groundScale, groundScale, groundScale)
            it.transform.rotate(0f, 1f, 0f, groundInitRotationY)

        }

    }

    private fun generateInitPositions(){

        val groundInstance = ModelInstance(groundModel, 0f, 0f, 0f)

        val bb = BoundingBox()
        groundInstance.calculateBoundingBox(bb)
        groundPartLength = (bb.depth * groundScale) * 0.95f

        val halfPart = groundPartLength / 2
        val firstPartPosition = Vector3(- groundPartLength * (groundParts / 2) + halfPart - (groundParts % 2) * halfPart  , groundCenter.y, groundCenter.z)
        firstPartPosition.x += groundCenter.x

        for(i in 0 until groundParts){

            partsPositions.add(Vector3(
                    firstPartPosition.x + groundPartLength * i,
                    firstPartPosition.y,
                    firstPartPosition.z))

        }

    }


    fun update(){

        instances.forEach {

            var translation = it.transform.getTranslation(Vector3())
            translation.x -= movementSpeed / 70f
            if(translation.x <= partsPositions[0].x) {

                translation = Vector3(
                        partsPositions[partsPositions.size-1].x,
                        partsPositions[partsPositions.size-1].y,
                        partsPositions[partsPositions.size-1].z
                        )

            }

            it.transform.setTranslation(translation)

        }

    }

}