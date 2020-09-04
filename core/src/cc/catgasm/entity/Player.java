package cc.catgasm.entity;

import cc.catgasm.entity.util.FaceDetectionBoundingBox;
import cc.catgasm.util.SimpleVector2;
import cc.catgasm.util.SimpleVector3;
import cc.catgasm.world.World;
import cc.catgasm.world.block.Block;
import cc.catgasm.world.block.Blocks;
import cc.catgasm.world.chunk.Chunk;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import java.util.LinkedList;
import java.util.List;

import static cc.catgasm.world.chunk.Chunk.CHUNK_HEIGHT;
import static cc.catgasm.world.chunk.Chunk.CHUNK_SIZE;

@SuppressWarnings("unused")
public class Player {
    private PerspectiveCamera cam;

    //Public um 2000 Getter/Setter zu sparen
    public boolean movingForward;
    public boolean movingBackward;
    public boolean movingLeft;
    public boolean movingRight;
    public boolean movingDown;
    public boolean movingUp;
    public boolean isFlying;
    public boolean isOnGround;
    public boolean jumpPressed;
    public boolean isAttacking;
    public boolean canAttack;
    public boolean isPlacing;

    private final float WALK_SPEED = 0.15f;
    private final float JUMP_POWER = 30;
    private final float GRAVITY = -20;
    private final float EYE_HEIGHT = 1.7f;
    private final float HIT_DISTANCE = 6;
    private final float HIT_DISTANCE2 = HIT_DISTANCE * HIT_DISTANCE;
    private final int MAX_ITEMS = 5;

    public final Vector3 velocity; //Vec3 --> forward,up,sideways
    public final Vector3 velocityGoal;
    private final Vector3 position;
    private final Quaternion quaternion;
    private FaceDetectionBoundingBox faceDetection;
    private int selectedIndex;

    private enum State {
        JUMPING, STANDING, WALKING
    }

    private State state;

    public Player(PerspectiveCamera cam) {
        this.cam = cam;
        quaternion = new Quaternion();
        velocity = new Vector3();
        velocityGoal = new Vector3();
        position = new Vector3(0,70,0);
        cam.position.set(position.cpy().add(0,EYE_HEIGHT,0));
        faceDetection = new FaceDetectionBoundingBox(this);

        canAttack = true;
    }


    private float jumpHeight;
    private final World world = World.getInstance();

    public void update() {
        float dt = Gdx.graphics.getDeltaTime();

        if (dt == 0)
            return;

        if (dt > 0.15f) {
            dt = 0.15f;
        }


        //Jumping
        if (!isFlying) {
            if (position.y > jumpHeight + 1.3f) { //Sprung fertig
                state = State.STANDING;
            }

            if (state != State.JUMPING) {
                velocityGoal.y = GRAVITY;
            }

            if (jumpPressed && isOnGround) {
                velocityGoal.y = JUMP_POWER;
                jumpPressed = false;
                state = State.JUMPING;
                jumpHeight = position.y;
            }

            velocity.y = isOnGround && state != State.JUMPING ?
                    0 : approach(velocityGoal.y, velocity.y, velocity.y > 0 ? dt * 0.9f : dt);
        } else { //Flying
            if (movingDown) {
                velocity.y = -WALK_SPEED * dt * 100;
            } else if (movingUp) {
                velocity.y = WALK_SPEED * dt * 100;
            } else {
                velocity.y = 0;
            }
        }

        //Walking
        if (movingForward) {
            velocityGoal.x = WALK_SPEED;
        } else if (movingBackward) {
            velocityGoal.x = -WALK_SPEED;
        } else {
            velocityGoal.x = 0;
        }

        if (movingLeft) {
            velocityGoal.z = -WALK_SPEED;
        } else if (movingRight) {
            velocityGoal.z = WALK_SPEED;
        } else {
            velocityGoal.z = 0;
        }

        velocity.x = approach(velocityGoal.x, velocity.x, dt);
        velocity.z = approach(velocityGoal.z, velocity.z, dt);

        float dx = velocity.x * (float) Math.sin(getYawRad()) + velocity.z * (float) Math.cos(getYawRad());
        float dz = velocity.z * (float) Math.sin(getYawRad()) - velocity.x * (float) Math.cos(getYawRad());
        float dy = velocity.y;

        //Kollision

        if(!isFlying) { //Flugmdous vorübergehenden im noclip modus
            if (dy <= 0) { //TODO Block drüber checken
                Block b = world.getBlockRelativeTo(position, 0f, -0.3f, 0f);
                if (b != null) {
                    position.y = b.getPos().y + 1;
                    dy = 0;
                    velocity.y = 0;
                    isOnGround = true;
                } else {
                    isOnGround = false;
                }
            }

            if (dx != 0) {
                Block b = world.getBlockRelativeTo(position, dx > 0 ? 0.5f : -0.5f, 0, 0);
                if (b != null) {
                    dx = 0;
                    velocity.x = 0;
                }
            }

            if (dz != 0) {
                Block b = world.getBlockRelativeTo(position, 0, 0, dz > 0 ? 0.5f : -0.5f);
                if (b != null) {
                    dz = 0;
                    velocity.z = 0;
                }
            }
        }

        increasePos(dx, dy, dz);

        cam.position.set(position.x, position.y + EYE_HEIGHT, position.z);
        cam.update();

        updateQuaternion();

        if(isAttacking || isPlacing)
            runPicker();
    }

    public void updateQuaternion() {
        cam.view.getRotation(quaternion);
        quaternion.nor();
    }

    //Member variable --> kein 'new' in Schleife
    private final List<Chunk> reachableChunks = new LinkedList<>();
    private final List<Chunk> surroundingChunks = new LinkedList<>();

    private final Vector3 tmpVec = new Vector3();
    private final Vector3 chunkDimension = new Vector3(CHUNK_SIZE, CHUNK_HEIGHT, CHUNK_SIZE);
    private final Vector3 blockDimension = new Vector3(1,1,1);
    private final SimpleVector3 tmpPos = new SimpleVector3();

    private void runPicker() {
        Ray ray = cam.getPickRay(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        getNearestChunksInDirection(ray);

        float distance = -1;
        float dist2;
        Block blockAimedAt = null;

        for (Chunk chunk : reachableChunks) {
            for(Block b : chunk.getBlocksArray()){
                SimpleVector3 tmpPos = b.getPos();
                tmpVec.set(tmpPos.x + 0.5f, tmpPos.y + 0.5f, tmpPos.z + 0.5f);

                //Position vom Spieler bis Mitte des Blocks
                dist2 = ray.origin.dst2(tmpVec);

                if(distance >= 0f && dist2 > distance)
                    continue;

                if(dist2 > HIT_DISTANCE2)
                    continue;

                if (Intersector.intersectRayBoundsFast(ray, tmpVec,blockDimension)) {
                    blockAimedAt = b;
                    distance = dist2;
                }
            }
        }

        if(blockAimedAt != null) {
            if (isPlacing) {
                isPlacing = false;
                SimpleVector3 btmp = blockAimedAt.getPos();
                tmpPos.set(btmp);

                //Erkennen welche Seite des Blocks angeklickt wurde
                int face = faceDetection.getFace(ray,blockAimedAt);
                switch (face) {
                    case Blocks.Orientation.NORTH:
                        tmpPos.z--;
                        break;
                    case Blocks.Orientation.SOUTH:
                        tmpPos.z++;
                        break;
                    case Blocks.Orientation.EAST:
                        tmpPos.x++;
                        break;
                    case Blocks.Orientation.WEST:
                        tmpPos.x--;
                        break;
                    case Blocks.Orientation.UP:
                        tmpPos.y++;
                        break;
                    case Blocks.Orientation.DOWN:
                        tmpPos.y--;
                        break;
                }

                world.setBlockAt(new Block(tmpPos.x, tmpPos.y, tmpPos.z, getSelectedBlockType()), true);
            }

            if (isAttacking && canAttack) {
                canAttack = false;
                world.delBlock(blockAimedAt);
            }
        }
    }

    private final SimpleVector2 tmpChunkPos = new SimpleVector2();

    /*
     * Gibt die Chunks zurück, die der Spieler erreichen kann
     * Erster Chunk ist immer der in dem er gerade steht
     */
    private void getNearestChunksInDirection(Ray ray){
        reachableChunks.clear();

        //Spieler kann zu 100% den Chunk erreichn in dem er steht
        Chunk tmp = world.getChunkAt(getChunkPosition());
        if(tmp != null)
            reachableChunks.add(tmp);

        //Den am nächsten liegenden Chunk finden (in die Richtung des Spielers)
        surroundingChunks.clear();
        surroundingChunks.add(world.getChunkAt(getChunkPosition(tmpChunkPos).add(1, 0)));
        surroundingChunks.add(world.getChunkAt(getChunkPosition(tmpChunkPos).add(-1, 0)));
        surroundingChunks.add(world.getChunkAt(getChunkPosition(tmpChunkPos).add(0, 1)));
        surroundingChunks.add(world.getChunkAt(getChunkPosition(tmpChunkPos).add(0, -1)));
        surroundingChunks.add(world.getChunkAt(getChunkPosition(tmpChunkPos).add(1, 1)));
        surroundingChunks.add(world.getChunkAt(getChunkPosition(tmpChunkPos).add(1, -1)));
        surroundingChunks.add(world.getChunkAt(getChunkPosition(tmpChunkPos).add(-1, 1)));
        surroundingChunks.add(world.getChunkAt(getChunkPosition(tmpChunkPos).add(-1, -1)));

        //Richtigen chunk suchen
        for (Chunk surroundingChunk : surroundingChunks) {
            if(surroundingChunk == null) continue;
            SimpleVector2 chunkBlockPos = surroundingChunk.getBlockPosition();

            tmpVec.set(chunkBlockPos.x + (CHUNK_SIZE / 2f),
                    CHUNK_HEIGHT / 2f,
                    chunkBlockPos.y + (CHUNK_SIZE / 2f));

            if (Intersector.intersectRayBoundsFast(ray, tmpVec, chunkDimension)) {
                reachableChunks.add(surroundingChunk);
            }
        }
    }

    private int getSelectedBlockType() {
        switch (selectedIndex) {
            case 0:
                return Blocks.STONE;
            case 1:
                return Blocks.GRASS;
            case 2:
                return Blocks.DIRT;
            case 3:
                return Blocks.WOOD;
            case 4:
                return Blocks.SAND;
        }
        return 0;
    }

    private float approach(float goal, float current, float dt) {
        float diff = goal - current;

        if (diff > dt) {
            return current + dt;
        } else if (diff < -dt) {
            return current - dt;
        }

        return goal;
    }

    public void increaseSelectedIndex(int delta) {
        selectedIndex = Math.floorMod(selectedIndex + delta,MAX_ITEMS);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private final Vector3 tmp = new Vector3();

    public void rotateYaw(float step){
        cam.rotate(Vector3.Y,step);
    }

    public void rotatePitch(float step){
        tmp.set(cam.direction).crs(cam.up).nor();

        float currentY = cam.direction.y *90;
        if (Math.abs(currentY+step) > 89.4){
            step = Math.signum(currentY)*89.4f-currentY;
        }

        cam.direction.rotate(tmp, step).nor();
        cam.up.rotate(tmp, step).nor();

    }


    public float getYawRad() {
        return quaternion.getYawRad();
    }

    public float getYaw() {
        return quaternion.getYaw();
    }

    public float getPitch() {
        return quaternion.getPitch();
    }

    public float getRoll() {
        return quaternion.getRoll();
    }

    public void setPos(float x, float y, float z) {
        position.set(x, y, z);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }

    public void increasePos(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    public void increasePos(double dx, double dy, double dz) {
        position.add((float) dx, (float) dy, (float) dz);
    }

    public Camera getCam() {
        return cam;
    }

    public void setCam(PerspectiveCamera cam) {
        this.cam = cam;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Quaternion getQuaternion() {
        return quaternion;
    }

    public SimpleVector2 getChunkPosition() {
        int x = Math.floorDiv((int) position.x, CHUNK_SIZE);
        int z = Math.floorDiv((int) position.z, CHUNK_SIZE);

        return new SimpleVector2(x, z);
    }

    public SimpleVector2 getChunkPosition(SimpleVector2 dest) {
        dest.x = Math.floorDiv((int) position.x, CHUNK_SIZE);
        dest.y = Math.floorDiv((int) position.z, CHUNK_SIZE);

        return dest;
    }
}
