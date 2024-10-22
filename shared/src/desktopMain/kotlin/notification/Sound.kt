/*
 * Copyright 2023 Joel Kanyi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joelkanyi.focusbloom.utils

import com.joelkanyi.focusbloom.utils.AudioUtils.play
import javax.sound.sampled.AudioSystem

object Sound {
    fun playSound(soundFile: String = "ringing.wav") {
        try {
            val audioInputStream = AudioSystem.getAudioInputStream(
                Thread.currentThread().contextClassLoader.getResource(soundFile)
            )
            val clip = AudioSystem.getClip()
            clip.open(audioInputStream)
            clip.loop(2)
            clip.start()
        } catch (ex: Exception) {
            println("Error while loading notification sound")
        }
    }
}
