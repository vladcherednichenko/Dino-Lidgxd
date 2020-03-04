package com.vlad.dino

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL20.GL_COVERAGE_BUFFER_BIT_NV
import com.badlogic.gdx.graphics.GL20.GL_DEPTH_TEST
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.vlad.dino.camera.MovingCamera
import com.vlad.dino.character.CharacterManager
import com.vlad.dino.input.InputManager

class DinoGame : ApplicationAdapter() {

    private lateinit var camera: MovingCamera
    private lateinit var modelBatch: ModelBatch
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var modelBuilder: ModelBuilder
    private lateinit var environment: Environment
    private lateinit var assets: GameAssetManager
    private lateinit var inputManager: InputManager
    private lateinit var groundManager: GroundManager
    private lateinit var characterManager: CharacterManager
    private lateinit var light: DirectionalLight

    private val instances : ArrayList<ModelInstance> = arrayListOf()

    private lateinit var buttonTexture : Texture
    private lateinit var button : ImageButton

    private lateinit var stage: Stage
    private lateinit var skin: Skin

    private var width = 0
    private var height = 0

    override fun create() {

        Gdx.gl.glEnable(GL_DEPTH_TEST)

        width = Gdx.graphics.width
        height = Gdx.graphics.height

        camera = MovingCamera(60f, width.toFloat(), height.toFloat())

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

        assets = GameAssetManager()
        assets.loadAssets()


        inputManager = InputManager()
        inputManager.camera = camera

        groundManager = GroundManager()
        characterManager = CharacterManager()

        // UI
        skin = Skin(Gdx.files.internal("uiskin.json"))
        stage = Stage(ScreenViewport())

        val textButton = TextButton("Up", skin, "default")


        textButton.width = 200f
        textButton.height = 200f
        textButton.setPosition(width.toFloat() - textButton.width, 0f)

        textButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {

                characterManager.jump()

            }
        })

        stage.addActor(textButton)
        Gdx.input.inputProcessor = stage

        light = DirectionalLight()
        light.set(1f, 1f, 1f, 0f, 0f, -1f)

        environment = Environment()
        environment.add(light)
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f))

    }

    override fun render() {

        if(assets.assetsLoaded()) {

            spawnObjects()

        }

        Gdx.gl.glClearColor(227/255f, 227/255f, 227/255f, 1f)
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or (if (Gdx.graphics.bufferFormat.coverageSampling) GL_COVERAGE_BUFFER_BIT_NV else 0))


        camera.update()
        groundManager.update()
        characterManager.update(Gdx.graphics.deltaTime)

        modelBatch.begin(camera)
        modelBatch.render(characterManager.instances, environment)
        modelBatch.render(groundManager.instances, environment)
        modelBatch.end()


        stage.act(Gdx.graphics.deltaTime)
        stage.draw()

    }


    private fun spawnObjects(){

        characterManager.createCharacter(Vector3(0f, 0f, 0f), assets.assets)
        groundManager.createGround(-2f, assets.assets)

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