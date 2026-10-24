package com.ts.bt.hur

import com.andrerinas.openheadunit.aap.AapProjectionActivity

/**
 * The projection activity under a package name the TopWay MainUI recognises.
 *
 * MainUI only turns the rotary knob (CAN keys 515/516) into D-pad up/down when the top
 * activity's class name starts with "com.ts.bt" (or for its own launcher, radio and player).
 * For every other app it sends MEDIA_PREVIOUS/NEXT, which look exactly like the steering
 * wheel track buttons, so the knob skipped songs in Android Auto. Under this name the knob
 * arrives as D-pad keys, which the keymap can bind to the AA rotary controller, and the
 * steering wheel buttons keep skipping tracks.
 */
class HurProjectionActivity : AapProjectionActivity()
