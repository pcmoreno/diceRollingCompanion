package moreno.paulo.dicerollingcompanion

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        allDiceImages = listOf<ImageView>(imageDice1, imageDice2, imageDice3, imageDice4)
        allDiceAnims = listOf<GifImageView>(spinningDice1, spinningDice2, spinningDice3, spinningDice4)
        allDiceTextResults = listOf<TextView>(diceTextResult1, diceTextResult2, diceTextResult3, diceTextResult4)

        initialize()

        text_click_to_roll.visibility = View.INVISIBLE
        diceTextResult1.visibility = View.INVISIBLE
        spinningDice1.visibility = View.INVISIBLE

        text_sides_count.text = """$numberOfSides"""
        text_dice_count.text = """$numberOfDice"""

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
                    val mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.tadaa)
                    mediaPlayer?.start()
                    status = Status.JUST_ROLLED
                }, (1000L + Random.nextInt(1, 2001)))
                for (i in 1..numberOfDice) {
                    status = Status.ROLLING
                    allDiceImages[i - 1].visibility = View.INVISIBLE
                    allDiceAnims[i - 1].visibility = View.VISIBLE
                    hideAllDiceThrowsText()
                }
            } else if (status != Status.ROLLING){
                setIntroAnimationOffAndShowDice()
                hideUnusedDices()
            }
        }
    }

    private fun updateDiceImageInView() {
        when (this.numberOfSides) {
            4, 5 -> setImagesTo(R.drawable.dice4)
            6, 7 -> setImagesTo(R.drawable.dice6)
            8, 9 -> setImagesTo(R.drawable.dice8)
            10, 11, 12, 13, 14 -> setImagesTo(R.drawable.dice10)
            else -> setImagesTo(R.drawable.dice20)
        }
        setIntroAnimationOffAndShowDice()
    }

    private fun roll():Int {
        return Random.nextInt(1, numberOfSides + 1)
    }

    private fun setIntroAnimationOffAndShowDice() {
        diceAnim.visibility = View.GONE
        status = Status.READY_TO_ROLL
        hideAllDiceThrowsText()
        text_click_to_roll.visibility = View.VISIBLE
        for (i in 1..numberOfDice) {
            allDiceImages[i - 1].visibility = View.VISIBLE
        }
        for (i in numberOfDice..3) {
            allDiceImages[i].visibility = View.INVISIBLE
        }
    }

    private fun hideUnusedDices() {
        allDiceImages.forEach { it.visibility = View.VISIBLE}
        for (i in numberOfDice..3) {
            allDiceImages[i].visibility = View.INVISIBLE
        }
    }

    private fun initialize() {
        this.status = Status.NOT_READY_TO_ROLL_YET
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

    private fun setImagesTo(diceImageIndex: Int) {
        allDiceImages.forEach { it.setImageResource(diceImageIndex)}
    }
}
