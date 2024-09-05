package moreno.paulo.dicerollingcompanion

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import moreno.paulo.dicerollingcompanion.databinding.ActivityMainBinding
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
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        initialize()
        binding.buttonSideMinus.setOnClickListener{
            if (status != Status.ROLLING) {
                if (numberOfSides > 4)(--numberOfSides).toString()
                binding.textSidesCount.text = """$numberOfSides"""
                updateDiceImageInView()
            }
        }
        binding.buttonSidePlus.setOnClickListener{
            if (status != Status.ROLLING) {
                if (numberOfSides < 20) (++numberOfSides).toString()
                binding.textSidesCount.text = """$numberOfSides"""
                updateDiceImageInView()
            }
        }

        binding.buttonDiceMinus.setOnClickListener{
            if (status != Status.ROLLING) {
                if (numberOfDice > 1) (--numberOfDice).toString()
                binding.textDiceCount.text = """$numberOfDice"""
                updateDiceImageInView()
            }
        }
        binding.buttonDicePlus.setOnClickListener{
            if (status != Status.ROLLING) {
                if (numberOfDice < 4) (++numberOfDice).toString()
                binding.textDiceCount.text = """$numberOfDice"""
                updateDiceImageInView()
            }
        }
        binding.buttonInvisibleRoll.setOnClickListener{
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
        binding.buttonSound.setOnClickListener{
            when (soundOn) {
                true -> {
                    soundOn = false
                    binding.buttonSound.setImageResource(R.drawable.sound_off)
                }
                false -> {
                    soundOn = true
                    binding.buttonSound.setImageResource(R.drawable.sound_on)
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
        binding.diceAnim.visibility = View.GONE
        status = Status.READY_TO_ROLL
        hideAllDiceThrowsText()
        binding.textClickToRoll.visibility = View.VISIBLE
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

        allDiceImages = listOf<ImageView>(binding.imageDice1, binding.imageDice2, binding.imageDice3, binding.imageDice4)
        allDiceAnims = listOf<GifImageView>(binding.spinningDice1, binding.spinningDice2, binding.spinningDice3, binding.spinningDice4)
        allDiceTextResults = listOf<TextView>(binding.diceTextResult1, binding.diceTextResult2, binding.diceTextResult3, binding.diceTextResult4)

        this.status = Status.NOT_READY_TO_ROLL_YET

        binding.textClickToRoll.visibility = View.INVISIBLE
        binding.diceTextResult1.visibility = View.INVISIBLE
        binding.spinningDice1.visibility = View.INVISIBLE

        binding.textSidesCount.text = """$numberOfSides"""
        binding.textDiceCount.text = """$numberOfDice"""

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
            true -> binding.buttonSound.setImageResource(R.drawable.sound_on)
            false -> binding.buttonSound.setImageResource(R.drawable.sound_off)
        }
    }
}
