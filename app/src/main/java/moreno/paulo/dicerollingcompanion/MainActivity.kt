package moreno.paulo.dicerollingcompanion

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import pl.droidsonroids.gif.GifImageView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var numberOfSides = 6
    private var numberOfDice = 2
    private lateinit var allDiceImages: List<ImageView>
    private lateinit var allDiceAnims: List<GifImageView>
    private lateinit var allDiceTextResults: List<TextView>
    private var status: Status = Status.NOT_READY_TO_ROLL_YET
    private var soundOn: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()

        button_side_minus.setOnClickListener{
            if (status != Status.ROLLING) {
                if (numberOfSides > 4)(--numberOfSides).toString()
                text_sides_count.text = """$numberOfSides"""
                updateDiceImageInView()
            }
        }
        button_side_plus.setOnClickListener{
            if (status != Status.ROLLING) {
                if (numberOfSides < 20) (++numberOfSides).toString()
                text_sides_count.text = """$numberOfSides"""
                updateDiceImageInView()
            }
        }

        button_dice_minus.setOnClickListener{
            if (status != Status.ROLLING) {
                if (numberOfDice > 1) (--numberOfDice).toString()
                text_dice_count.text = """$numberOfDice"""
                updateDiceImageInView()
            }
        }
        button_dice_plus.setOnClickListener{
            if (status != Status.ROLLING) {
                if (numberOfDice < 4) (++numberOfDice).toString()
                text_dice_count.text = """$numberOfDice"""
                updateDiceImageInView()
            }
        }
        button_invisible_roll.setOnClickListener{
            if (status == Status.READY_TO_ROLL) {
                Handler().postDelayed({
                    allDiceTextResults.forEach { it.text = roll().toString() }
                    hideAllDiceAnimations()
                    for (i in 1..numberOfDice) {
                        allDiceTextResults[i - 1].visibility = View.VISIBLE
                    }
                    playRollFinishedSound()
                    status = Status.JUST_ROLLED
                }, (1000L + Random.nextInt(1, 2001)))
                for (i in 1..numberOfDice) {
                    status = Status.ROLLING
                    allDiceImages[i - 1].visibility = View.INVISIBLE
                    allDiceAnims[i - 1].visibility = View.VISIBLE
                    hideAllDiceThrowsText()
                }
            } else if (status != Status.ROLLING){
                updateDiceImageInView()
                hideUnusedDices()
            }
            saveCurrentPreferences()
        }
        buttonSound.setOnClickListener{
            when (soundOn) {
                true -> {
                    soundOn = false
                    buttonSound.setImageResource(R.drawable.sound_off)
                }
                false -> {
                    soundOn = true
                    buttonSound.setImageResource(R.drawable.sound_on)
                }
            }
        }
    }

    private fun updateDiceImageInView() {
        when (numberOfSides) {
            in 4..5 -> setDiceImagesTo(R.drawable.dice4)
            in 6..7 -> setDiceImagesTo(R.drawable.dice6)
            in 8..9 -> setDiceImagesTo(R.drawable.dice8)
            in 10..14 -> setDiceImagesTo(R.drawable.dice10)
            else -> setDiceImagesTo(R.drawable.dice20)
        }
        resetViewAndShowDice()
    }

    private fun roll():Int {
        return Random.nextInt(1, numberOfSides + 1)
    }

    private fun resetViewAndShowDice() {
        diceAnim.visibility = View.GONE
        status = Status.READY_TO_ROLL
        hideAllDiceThrowsText()
        text_click_to_roll.visibility = View.VISIBLE
        hideUnusedDices()
    }

    private fun hideUnusedDices() {
        allDiceImages.forEach { it.visibility = View.VISIBLE}
        for (i in numberOfDice..3) {
            allDiceImages[i].visibility = View.INVISIBLE
        }
    }

    private fun initialize() {
        loadPreferences()

        allDiceImages = listOf<ImageView>(imageDice1, imageDice2, imageDice3, imageDice4)
        allDiceAnims = listOf<GifImageView>(spinningDice1, spinningDice2, spinningDice3, spinningDice4)
        allDiceTextResults = listOf<TextView>(diceTextResult1, diceTextResult2, diceTextResult3, diceTextResult4)

        this.status = Status.NOT_READY_TO_ROLL_YET

        text_click_to_roll.visibility = View.INVISIBLE
        diceTextResult1.visibility = View.INVISIBLE
        spinningDice1.visibility = View.INVISIBLE

        text_sides_count.text = """$numberOfSides"""
        text_dice_count.text = """$numberOfDice"""

        hideAllDices()
        hideAllDiceAnimations()
        hideAllDiceThrowsText()
    }

    private fun hideAllDices() {
        allDiceImages.forEach { it.visibility = View.INVISIBLE }
    }

    private fun hideAllDiceAnimations() {
        allDiceAnims.forEach { it.visibility = View.INVISIBLE }
    }

    private fun hideAllDiceThrowsText() {
        allDiceTextResults.forEach { it.visibility = View.INVISIBLE }
    }

    private fun setDiceImagesTo(diceImageIndex: Int) {
        allDiceImages.forEach { it.setImageResource(diceImageIndex)}
    }

    private fun playRollFinishedSound() {
        if (soundOn) {
            val mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.tadaa)
            mediaPlayer?.start()
        }
    }

    private fun saveCurrentPreferences() {
        val sharedPreferencesEditor = this.getPreferences(Context.MODE_PRIVATE).edit()
        sharedPreferencesEditor.putInt(getString(R.string.number_of_sides), numberOfSides)
        sharedPreferencesEditor.putInt(getString(R.string.number_of_dice), numberOfDice)
        sharedPreferencesEditor.putBoolean(getString(R.string.sound_is_on), soundOn)
        sharedPreferencesEditor.apply()
    }

    private fun loadPreferences() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        numberOfSides = sharedPref.getInt(getString(R.string.number_of_sides), 6)
        numberOfDice = sharedPref.getInt(getString(R.string.number_of_dice), 2)
        soundOn = sharedPref.getBoolean(getString(R.string.sound_is_on), true)
        when (soundOn) {
            true -> buttonSound.setImageResource(R.drawable.sound_on)
            false -> buttonSound.setImageResource(R.drawable.sound_off)
        }
    }
}
