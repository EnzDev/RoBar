package fr.robar.android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import fr.robar.android.recipe.Recipe.Companion.RECIPES
import fr.robar.android.recipe.RobarCustomRecipe
import kotlinx.android.synthetic.main.activity_robar_control.*

class RobarControl : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robar_control)
        if ((application as RobarApplication).robarDevice == null) finish()

        if(!(application as RobarApplication).sendBytes(byteArrayOf(0xD, 0xE, 0xA, 0xD))) finish()

        custom_recipe.setOnClickListener {
            startActivity(Intent(this, RobarCustomRecipe::class.java))
        }

        RECIPES.entries.forEach { e->
            val button = Button(this)
            button.text = e.key
            button.setOnClickListener {
                if(!(application as RobarApplication).sendBytes(e.value.encodeToByteArray())) finish()
            }
            recipe_box.addView(button)
        }
    }
}

