/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.minusmc.minusbounce.utils

import net.minusmc.minusbounce.event.EventTarget
import net.minusmc.minusbounce.event.Listenable
import net.minusmc.minusbounce.event.MoveEvent
import net.minusmc.minusbounce.event.PacketEvent
import net.minusmc.minusbounce.utils.extensions.stopXZ
import net.minusmc.minusbounce.utils.extensions.toRadiansD
import net.minecraft.network.play.client.C03PacketPlayer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MovementUtils : MinecraftInstance(), Listenable {

    var speed
        get() = mc.thePlayer?.run { sqrt(motionX * motionX + motionZ * motionZ).toFloat() } ?: .0f
        set(value) { strafe(value) }

    val isMoving
        get() = mc.thePlayer?.movementInput?.run { moveForward != 0f || moveStrafe != 0f } ?: false

    val hasMotion
        get() = mc.thePlayer?.run { motionX != .0 || motionY != .0 || motionZ != .0 } ?: false

    @JvmOverloads
    fun strafe(speed: Float = this.speed, stopWhenNoInput: Boolean = false, moveEvent: MoveEvent? = null) =
        mc.thePlayer?.run {
            if (!isMoving) {
                if (stopWhenNoInput) {
                    moveEvent?.zeroXZ()
                    stopXZ()
                }

                return@run
            }

            val yaw = direction
            val x = -sin(yaw) * speed
            val z = cos(yaw) * speed

            if (moveEvent != null) {
                moveEvent.x = x
                moveEvent.z = z
            }

            motionX = x
            motionZ = z
        }

    fun forward(distance: Double) =
        mc.thePlayer?.run {
            val yaw = rotationYaw.toRadiansD()
            setPosition(posX - sin(yaw) * distance, posY, posZ + cos(yaw) * distance)
        }

    val direction
        get() = mc.thePlayer?.run {
                var yaw = rotationYaw
                var forward = 1f

                if (moveForward < 0f) {
                    yaw += 180f
                    forward = -0.5f
                } else if (moveForward > 0f)
                    forward = 0.5f

                if (moveStrafing < 0f) yaw += 90f * forward
                else if (moveStrafing > 0f) yaw -= 90f * forward

                yaw.toRadiansD()
            } ?: 0.0

    fun isOnGround(height: Double) =
        mc.theWorld != null && mc.thePlayer != null &&
        mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.entityBoundingBox.offset(0.0, -height, 0.0)).isNotEmpty()

    var serverOnGround = false

    var serverX = .0
    var serverY = .0
    var serverZ = .0

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.isCancelled)
            return

        val packet = event.packet

        if (packet is C03PacketPlayer) {
            serverOnGround = packet.onGround

            if (packet.isMoving) {
                serverX = packet.x
                serverY = packet.y
                serverZ = packet.z
            }
        }
    }

    override fun handleEvents() = true
}
