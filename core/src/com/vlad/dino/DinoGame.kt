package com.vlad.dino

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL20.GL_DEPTH_TEST
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.CpuSpriteBatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.vlad.dino.camera.MovingCamera
import com.vlad.dino.input.InputManager

class DinoGame : ApplicationAdapter() {

    private lateinit var camera: MovingCamera
    private lateinit var modelBatch: ModelBatch
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var modelBuilder: ModelBuilder
    private lateinit var environment: Environment
    private lateinit var assets: DinoAssetManager
    private lateinit var inputManager: InputManager
    private lateinit var groundManager: GroundManager

    private val instances : ArrayList<ModelInstance> = arrayListOf()
    private lateinit var dinoInstance: ModelInstance

    private lateinit var buttonTexture : Texture
    private lateinit var button : ImageButton

    private lateinit var stage: Stage


    override fun create() {

        Gdx.gl.glEnable(GL_DEPTH_TEST)

        camera = MovingCamera(60f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        var cameraTarget = Vector3(10f, 7f, 0f)
        camera.position.x = 10f
        camera.position.y = 12f
        camera.position.z = 24f
        camera.near = 0.1f
        camera.far = 3000f


        camera.lookAt(cameraTarget)
        camera.rotateAround(cameraTarget, Vector3(0f, 1f, 0f), -50f)


        modelBatch = ModelBatch()
        spriteBatch = SpriteBatch()
        modelBuilder = ModelBuilder()

        assets = DinoAssetManager()
        assets.loadAssets()


        inputManager = InputManager()
        inputManager.camera = camera

        groundManager = GroundManager()


        // UI
        buttonTexture = Texture("ui/grey_button.png")
        stage = Stage()

        var skin = Skin()
        var buttonStyle = Button.ButtonStyle()
//        buttonStyle.up = skin.getDrawable("grey_button")
//        buttonStyle.down = skin.getDrawable("grey_button")
        var drawable = TextureRegionDrawable(TextureRegion(buttonTexture))
        button = ImageButton(drawable)
        stage.addActor(button)

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f))


        inputManager.onTouchListener = {

            

        }

    }

    override fun render() {

        if(assets.assetsLoaded()) {

            spawnObjects()

        }

        if(Gdx.input.isTouched){

            val x = Gdx.input.x
            val y = Gdx.input.y

//            var isHit: Boolean = false
//            if(button.hit(x.toFloat(), y.toFloat(), isHit).)
//                println("touched $x")

        }

        Gdx.gl.glClearColor(227/255f, 227/255f, 227/255f, 1f)
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        camera.update()
        groundManager.update()
        modelBatch.begin(camera)
        modelBatch.render(instances, environment)
        modelBatch.render(groundManager.instances, environment)
//        spriteBatch.begin()
//        spriteBatch.draw(buttonTexture, Gdx.graphics.width.toFloat() - buttonTexture.width, 0f)
//        spriteBatch.end()
        stage.draw()
        modelBatch.end()

    }


    fun spawnObjects(){

        val dinoScale = 3f
        val dinoModel = assets.get("dino/dino0.obj", Model::class.java)
        dinoInstance = ModelInstance(dinoModel, 0f, 0f, 0f)
        dinoInstance.transform.scale(dinoScale, dinoScale, dinoScale)
        instances.add(dinoInstance)

//        showBoundingBoxOfModel(dinoModel, dinoScale)

        groundManager.createGround(-2f, assets.assets)





        //instances.add(groundInstance)


    }

    fun showBoundingBoxOfModel(model: Model, scale: Float){

        var bb = BoundingBox()
        model.calculateBoundingBox(bb)
        val boxSize = 0.3f

        var pos1 = bb.getCorner000(Vector3())
        var pos2 = bb.getCorner001(Vector3())
        var pos3 = bb.getCorner010(Vector3())
        var pos4 = bb.getCorner011(Vector3())
        var pos5 = bb.getCorner100(Vector3())
        var pos6 = bb.getCorner101(Vector3())
        var pos7 = bb.getCorner110(Vector3())
        var pos8 = bb.getCorner111(Vector3())

        val box1 = modelBuilder.createBox(boxSize, boxSize, boxSize, Material(ColorAttribute.createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        val box2 = modelBuilder.createBox(boxSize, boxSize, boxSize, Material(ColorAttribute.createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        val box3 = modelBuilder.createBox(boxSize, boxSize, boxSize, Material(ColorAttribute.createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        val box4 = modelBuilder.createBox(boxSize, boxSize, boxSize, Material(ColorAttribute.createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        val box5 = modelBuilder.createBox(boxSize, boxSize, boxSize, Material(ColorAttribute.createDiffuse(Color.BLUE)),
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        val box6 = modelBuilder.createBox(boxSize, boxSize, boxSize,
                Material(ColorAttribute.createDiffuse(Color.BLUE)),
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        val box7 = modelBuilder.createBox(boxSize, boxSize, boxSize,
                Material(ColorAttribute.createDiffuse(Color.BLUE)),
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        val box8 = modelBuilder.createBox(boxSize, boxSize, boxSize,
                Material(ColorAttribute.createDiffuse(Color.BLUE)),
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())

        instances.add(ModelInstance(box1, pos1.x * scale, pos1.y * scale, pos1.z * scale))
        instances.add(ModelInstance(box2, pos2.x * scale, pos2.y * scale, pos2.z * scale))
        instances.add(ModelInstance(box3, pos3.x * scale, pos3.y * scale, pos3.z * scale))
        instances.add(ModelInstance(box4, pos4.x * scale, pos4.y * scale, pos4.z * scale))
        instances.add(ModelInstance(box5, pos5.x * scale, pos5.y * scale, pos5.z * scale))
        instances.add(ModelInstance(box6, pos6.x * scale, pos6.y * scale, pos6.z * scale))
        instances.add(ModelInstance(box7, pos7.x * scale, pos7.y * scale, pos7.z * scale))
        instances.add(ModelInstance(box8, pos8.x * scale, pos8.y * scale, pos8.z * scale))


    }

    override fun dispose() {

        modelBatch.dispose()
        instances.clear()
        assets.assets.dispose()

        spriteBatch.dispose()
        buttonTexture.dispose()

    }



}