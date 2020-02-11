package fr.robar.android.recipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import fr.robar.android.R
import fr.robar.android.RobarApplication
import kotlinx.android.synthetic.main.activity_robar_custom_recipe.*

class RobarCustomRecipe : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robar_custom_recipe)

        component1.text = BOTTLES.RHUM.name
        component1.setOnTouchListener(makeEventListenerFor(BOTTLES.RHUM))

        component2.text = BOTTLES.COCA.name
        component2.setOnTouchListener(makeEventListenerFor(BOTTLES.COCA))

        component3.text = BOTTLES.ANANAS.name
        component3.setOnTouchListener(makeEventListenerFor(BOTTLES.ANANAS))

        component4.text = BOTTLES.ORANGE.name
        component4.setOnTouchListener(makeEventListenerFor(BOTTLES.ORANGE))
    }

    private fun makeEventListenerFor(ingred: BOTTLES): (View, MotionEvent) -> Boolean = { view, event ->
        if(event.action == ACTION_DOWN || event.action == ACTION_UP) {
            ingred.ordinal
            val eventMode = when (event.action) {
                ACTION_DOWN -> Mode.START
                ACTION_UP -> Mode.STOP
                else -> Mode.STOP // NEVER
            }

            // Forge a false Recipe
            Recipe(
                if(ingred.ordinal == 0) 1 else 0,
                if(ingred.ordinal == 1) 1 else 0,
                if(ingred.ordinal == 2) 1 else 0,
                if(ingred.ordinal == 3) 1 else 0,
                trigger = eventMode
            ).encodeToByteArray().also {
                if(!(application as RobarApplication).sendBytes(it)) finish()
            }
        }
        false
    }
}
