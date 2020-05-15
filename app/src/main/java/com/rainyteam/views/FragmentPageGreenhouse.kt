package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.UserPlants
import kotlinx.android.synthetic.main.fragment_plants_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext


class FragmentPageGreenhouse(
    position: Int
) : androidx.fragment.app.Fragment(), CoroutineScope {

    var mainConnection: Connection? = null

    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var user: String? = ""

    var mBDD: FirebaseFirestore? = null

    private var mutableList: MutableList<Plants>? = null
    val pos: Int = position;

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewActual = inflater.inflate(R.layout.fragment_plants_page, container, false)

        this.mainConnection = Connection()
        mBDD = mainConnection!!.mBDD()
        prefs = context?.getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")

        launch {
            /*mutableList = user?.let {
                mainConnection!!.getUserPlantsAlive(it)
            }*/
            mutableList = mutableListOf()
            val auxList = mutableListOf<UserPlants>()
            mBDD!!.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereGreaterThanOrEqualTo("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        auxList.add(document.toObject(UserPlants::class.java))
                    }
                }.await()
            mBDD!!.collection("Plants")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {

                        val actualPlant = document.toObject(Plants::class.java)
                        val userPlant = auxList.firstOrNull { it.plantId == document.id }
                        if (userPlant != null) {
                            actualPlant.setName(document.id)
                            actualPlant.setImageName(
                                "plant_" + actualPlant.getScientificName().toLowerCase().replace(
                                    " ",
                                    "_"
                                )
                            )
                            actualPlant.setStatus(userPlant.status)
                            mutableList!!.add(actualPlant)
                        }
                    }
                }.await()
            if (mutableList!!.size > pos * 9) {
                val auxPlant1: Plants? = mutableList!![pos * 9]
                val drawableName1: String? = auxPlant1!!.getPotImagePlant()
                val resID1: Int =
                    resources.getIdentifier(drawableName1, "drawable", context?.packageName)
                plantR1C1!!.setImageResource(resID1)
                plantR1C1.setOnClickListener { view ->
                    val intent = Intent(plantR1C1.context, EncyclopediaDetailActivity::class.java)
                    intent.putExtra("idPlant", auxPlant1.getName())
                    plantR1C1.context.startActivity(intent)
                }
            }
            if (mutableList!!.size > pos * 9 + 1) {
                val auxPlant2: Plants? = mutableList!![pos * 9 + 1]
                val drawableName2: String? = auxPlant2!!.getPotImagePlant()
                val resID2: Int =
                    resources.getIdentifier(drawableName2, "drawable", context?.packageName)
                plantR1C2!!.setImageResource(resID2)
                plantR1C2.setOnClickListener { view ->
                    val intent = Intent(plantR1C2.context, EncyclopediaDetailActivity::class.java)
                    intent.putExtra("idPlant", auxPlant2.getName())
                    plantR1C2.context.startActivity(intent)
                }
            }
            if (mutableList!!.size > pos * 9 + 2) {
                val auxPlant3: Plants? =mutableList!![pos * 9 + 2]
                val drawableName3: String? = auxPlant3!!.getPotImagePlant()
                val resID3: Int =
                    resources.getIdentifier(drawableName3, "drawable", context?.packageName)
                plantR1C3!!.setImageResource(resID3)
                plantR1C1.setOnClickListener { view ->
                    val intent = Intent(plantR1C3.context, EncyclopediaDetailActivity::class.java)
                    intent.putExtra("idPlant", auxPlant3.getName())
                    plantR1C3.context.startActivity(intent)
                }
            }
            if (mutableList!!.size > pos * 9 + 3) {
                val auxPlant4: Plants? =mutableList!![pos * 9 + 3]
                val drawableName4: String? = auxPlant4!!.getPotImagePlant()
                val resID4: Int =
                    resources.getIdentifier(drawableName4, "drawable", context?.packageName)
                plantR2C1!!.setImageResource(resID4)
                plantR2C1.setOnClickListener { view ->
                    val intent = Intent(plantR2C1.context, EncyclopediaDetailActivity::class.java)
                    intent.putExtra("idPlant", auxPlant4.getName())
                    plantR2C1.context.startActivity(intent)
                }
            }
            if (mutableList!!.size > pos * 9 + 4) {
                val auxPlant5: Plants? =mutableList!![pos * 9 + 4]
                val drawableName5: String? = auxPlant5!!.getPotImagePlant()
                val resID5: Int =
                    resources.getIdentifier(drawableName5, "drawable", context?.packageName)
                plantR2C2!!.setImageResource(resID5)
                plantR2C2.setOnClickListener { view ->
                    val intent = Intent(plantR2C2.context, EncyclopediaDetailActivity::class.java)
                    intent.putExtra("idPlant", auxPlant5.getName())
                    plantR2C2.context.startActivity(intent)
                }
            }
            if (mutableList!!.size > pos * 9 + 5) {
                val auxPlant6: Plants? =mutableList!![pos * 9 + 5]
                val drawableName6: String? = auxPlant6!!.getPotImagePlant()
                val resID6: Int =
                    resources.getIdentifier(drawableName6, "drawable", context?.packageName)
                plantR2C3!!.setImageResource(resID6)
                plantR2C3.setOnClickListener { view ->
                    val intent = Intent(plantR2C3.context, EncyclopediaDetailActivity::class.java)
                    intent.putExtra("idPlant", auxPlant6.getName())
                    plantR2C3.context.startActivity(intent)
                }
            }
            if (mutableList!!.size > pos * 9 + 6) {
                val auxPlant7: Plants? =mutableList!![pos * 9 + 6]
                val drawableName7: String? = auxPlant7!!.getPotImagePlant()
                val resID7: Int =
                    resources.getIdentifier(drawableName7, "drawable", context?.packageName)
                plantR3C1!!.setImageResource(resID7)
                plantR3C1.setOnClickListener { view ->
                    val intent = Intent(plantR3C1.context, EncyclopediaDetailActivity::class.java)
                    intent.putExtra("idPlant", auxPlant7.getName())
                    plantR3C1.context.startActivity(intent)
                }
            }

            if (mutableList!!.size > pos * 9 + 7) {
                val auxPlant8: Plants? =mutableList!![pos * 9 + 7]
                val drawableName8: String? = auxPlant8!!.getPotImagePlant()
                val resID8: Int =
                    resources.getIdentifier(drawableName8, "drawable", context?.packageName)
                plantR3C2!!.setImageResource(resID8)
                plantR3C2.setOnClickListener { view ->
                    val intent = Intent(plantR3C2.context, EncyclopediaDetailActivity::class.java)
                    intent.putExtra("idPlant", auxPlant8.getName())
                    plantR3C2.context.startActivity(intent)
                }
            }
            if (mutableList!!.size > pos * 9 + 8) {
                val auxPlant9: Plants? =mutableList!![pos * 9 + +8]
                val drawableName9: String? = auxPlant9!!.getPotImagePlant()
                val resID9: Int =
                    resources.getIdentifier(drawableName9, "drawable", context?.packageName)
                plantR3C3!!.setImageResource(resID9)
                plantR3C3.setOnClickListener { view ->
                    val intent = Intent(plantR3C3.context, EncyclopediaDetailActivity::class.java)
                    intent.putExtra("idPlant", auxPlant9.getName())
                    plantR3C3.context.startActivity(intent)
                }
            }
        }
        return viewActual
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}