package com.health.mental.mooditude.fragment.journal

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.HowYouFeelActivity
import com.health.mental.mooditude.adapter.UserActivityIconAdapter
import com.health.mental.mooditude.custom.CustomGridView
import com.health.mental.mooditude.data.entity.UserActivity
import com.health.mental.mooditude.databinding.FragmentAddSituationBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.listener.ActivityIconSelectListener
import com.health.mental.mooditude.utils.UiUtils
import java.util.*
import kotlin.collections.LinkedHashMap


/**
 * A simple [Fragment] subclass.
 * Use the [AddSituationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddSituationFragment() : BaseFragment(), TextWatcher {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentAddSituationBinding

    private var mMapOfData = LinkedHashMap<String, Array<String>>()
    private var mCurrAdapter:UserActivityIconAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddSituationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        prepareLayout()


        return root
    }

    private enum class ActivitySection {
        emoji,moving,smileysAndPeople,animalsAndNature,foodAndDrink,sport,activity,travelAndPlaces,objects,symbols,flags

        ;
        fun getLocalizedString(context:Context) :String {
            when(this) {
                emoji -> return context.getString(R.string.activity_section_emoji)
                moving -> return context.getString(R.string.activity_section_moving)
                smileysAndPeople -> return context.getString(R.string.activity_section_smileysAndPeople)
                animalsAndNature -> return context.getString(R.string.activity_section_animalsAndNature)
                foodAndDrink -> return context.getString(R.string.activity_section_foodAndDrink)
                sport -> return context.getString(R.string.activity_section_sport)
                activity -> return context.getString(R.string.activity_section_activity)
                travelAndPlaces -> return context.getString(R.string.activity_section_travelAndPlaces)
                objects -> return context.getString(R.string.activity_section_objects)
                symbols -> return context.getString(R.string.activity_section_symbols)
                flags -> return context.getString(R.string.activity_section_flags)
            }
        }

    }

    private fun prepareLayout() {
        val context = requireContext()
        mMapOfData.clear()

        //Smileys & People
        val smileysAndPeople = arrayOf("", //user-friends
            "", //female
            "", //male
            "", //users
            "", //wheelchair
            "", //user-check
            "", //blind
            "", //walking
            "", //running
            "", //user-secret
            "", //smile-wink
            "", //smile-beam
            "", //smile
            "", //grin-squint
            "", //grin-squint
            "", //grin-hearts
            "", //grin-beam-sweat
            "", //grin-beam
            "", //grin-alt
            "", //grin-wink
            "", // blind Light Style
            "", // robot Regular Style
            "", // user-astronaut Regular Style
            "", // baby  Light Style
            "", // male  Light Style
            "", // female  Light Style
            "", // user-crown Light Style
            "", // user-secret Light Style
            "",  // angry
            "",        //dizzy
            "",        //flushed
            "",        // frown
            "",        // grimace
            "",        // grin
            "",        // grin beam
            "",        // grean beam sweat
            "",        // grin heart
            "",        // grin squint
            "",        // grin squint
            "",        // grin squint star
            "",        // grin tears
            "",        // grin tongue
            "",        // grin tongue tears
            "",        // grin tounge squint
            "",        // grin wink
            "",        // kiss
            "",        // kiss beam
            "",        // kiss wink heart
            "",        // laugh
            "",        // laugh beam
            "",        // laugh squint
            "",        // laugh wink
            "",        // meh
            "",        // meh blank
            "",        // meh rolling eyes
            "",        // sad cry
            "",        // sad rolling eyes
            "",        // surprise
            "", // baby Light Style
            "", // blind Light Style
            "", // user-astronaut Light Style
            "", // street-view Light Style
            "", // user-secret Light Style
            "", // head-vr Light Style
            "", // poo Light Style

        )

        mMapOfData.put(ActivitySection.smileysAndPeople.getLocalizedString(context), smileysAndPeople)

        var animalsAndNature = arrayOf("", //otter
            "", //hippo
            "", //dog
            "", //leaf
            "", //tree
            "", // cat Solid
            "", // dog-leashed Light Style
            "", // dragon Solid Style
            "", // frog Light Style
            "", // snake Light Style
            "", // pig Regular Style
            "", // monkey Light Style
            "", // cow Regular Style
            "", // badger-honey Regular Style
            "", // kiwi-bird Regular Style
            "", // fish Regular Style
            "", // squirrel Regular Style
            "", // unicorn Regular Style
            "", // whale Regular Style
            "", // mountain Regular Style
            "", // island-tropical Solid Style
            "", // eclipse Regular Style
            "", // meteor Regular Style
            "", // moon-stars Regular Style
            "", // igloo Solid State
            "", // snowflakes Regular Style
            "", // icicles Regular Style
            "", // cat Light Style
            "", // badger-honey Light Style
            "", // bat Light Style
            "", // cow Light Style
            "", // dog Light Style
            "", // dog-leashed Light Style
            "", // dragon Light Style
            "", // fish Light Style
            "", // frog Light Style
            "", // monkey Light Style
            "", // pig Light Style
            "", // rabbit Light Style
            "", // snake Light Style
            "", // squirrel Light Style
            "", // unicorn Light Style
            "", // whale Light Style
            "", // spider-black-widow Light Style
            "", // turtle Light Style
            "", // sheep Light Style
            "", // paw-claws Light Style
            "", // paw Light Style

        )
        mMapOfData.put(ActivitySection.animalsAndNature.getLocalizedString(context), animalsAndNature)

        var foodAndDrink = arrayOf("", //utensils
            "", //hamburger
            "", //cookie
            "", //cookie-bite
            "", //cocktail
            "", //wine-glass-alt
            "", //wine-glass
            "", //wine-bottle
            "", //glass-martini-alt
            "", //glass-martini
            "", //coffee
            "", //mug-hot
            "", //glass-whiskey
            "", //glass-cheers
            "", // birthday-cake Light Style
            "", // bread-loaf Light Style
            "", // cheeseburger Light Style
            "", // croissant Light Style
            "", // hotdog Light Style
            "", // popcorn Solid Style
            "", // pizza-slice Regular Style
            "", // pie Solid Style
            "", // pepper-hot Solid Style
            "", // ice-cream Solid Style
            "", // hotdog Solid Style
            "", // gingerbread-man Solid Style
            "", // French-fries Light Style
            "", // cheese - swiss Regular Style
            "", // mug-marshmallows Light Style
            "", // glass-martini Light Style
            "", // bacon Light Style
            "", // bread-loaf Light Style
            "", // burger-soda Light Style
            "", // burrito Light Style
            "", // cheeseburger Light Style
            "", // croissant Light Style
            "", // egg-fried Light Style
            "", // ice-cream Light Style
            "", // pizza-slice Light Style
            "", // popcorn Light Style
            "", // pie Light Style
            "", // stroopwafel Light Style
            "", // hotdog Light Style
            "", // cookie Light Style
            "", // cheese-swims Light Style
            "", // apple-alt Light Style
            "", // lemon Light Style
        )

        mMapOfData.put(ActivitySection.foodAndDrink.getLocalizedString(context), foodAndDrink)

        var activity = arrayOf("", //swimmer
            "", //snowboarding
            "", //skiing-nordic
            "", //skiing-nordic
            "", //skiing
            "", //skating
            "", //hiking
            "", // shower Light Style
            "", // person-dolly  Light Style
            "", // person-carry  Light Style
            "", // person-sign  Light Style
            "", // pray Light Style
            "", // hot-tub Light Style
            "", // door-closed Light Style
            "", // door-open Solid Style
            "", // hiking Light Style
            "", // pray Light Style
            "", // running Light Style
            "", // skating Light Style
            "", // ski-lift Light Style
            "", // skiing Light Style
            "", // snowboarding Light Style
            "", // walking Light Style
            "", // wheelchair Light Style
            "", // swimmer Light Style
            "", // snowmobile Light Style
            "", // skiing-nordic Light Style
            "", // people-carry Light Style
            "", // person-sign Light Style
            "", // sledding Light Style
        )
        mMapOfData.put(ActivitySection.activity.getLocalizedString(context), activity)

        var travelAndPlaces = arrayOf("", //route
            "", //bus
            "", //traffic-light
            "", //luggage-cart
            "", //bed
            "", //bus-alt
            "", //passport
            "", //mountain
            "", //car-side
            "", //car-alt
            "", //car
            "",  //shuttle-van
            "", //road
            "", //plane
            "", //helicopter
            "", //taxi
            "", //plane-arrival
            "", //plane-departure
            "", //fighter-jet
            "", //globe-asia
            "", //globe-americas
            "", //map-marker-alt
            "", //map-marker
            "", //school
            "", //mosque
            "", //monument
            "", //landmark
            "", //kaaba
            "", //church
            "", //hospital
            "", //home
            "", //university
            "", //city
            "",  //hotel
            "",  //place-of-worship
            "",  //cross
            "", // campground Regular Style
            "", // gas-pump Light Style
            "",// gas-pump-slash Solid Style
        )

        mMapOfData.put(ActivitySection.travelAndPlaces.getLocalizedString(context), travelAndPlaces)

        var sport = arrayOf("", // baseball
            "", // basketball
            "", // bowling
            "", // weight-lift
            "", // footbal
            "", // soccer
            "", // ice-hockey
            "", // skating
            "", // skiing
            "", // snowboarding
            "",  // table tennis
            "",
            "", // running Light Style
            "", // swimmer Light Style
            "", // snowboarding Regular Style
            "", // skiing-nordic Regular Style
            "", // skiing Regular Style
            "", // sledding Regular Style
            "", // ski-jump Regular Style
            "", //ski-lift Regular Style
            "", // skating Regular Style
            "", // baseball
            "", // basketball-ball
            "", // bowling-ball
            "", // quidditch
            "", // dumbbell
        )

        mMapOfData.put(ActivitySection.sport.getLocalizedString(context), sport)

        var moving = arrayOf("", // People carrying stuff
            "", // Archive box
            "", // dolly
            "", // couch
            "", // route
            "", // sale sign
            "", // suitecase
            "", // tape
            "", // truck
            "",  // truck loading
            "", // helicopter Solid Style
            "", // plane-alt Solid Style
            "", // plane Solid Style
            "", // fighter-jet Solid Style
            "", // space-shuttle Solid Style
            "", // tractor Solid Style
            "", // truck-monster Solid Style
            "", // truck-pickup Solid Style
            "", // rv Solid Style Solid Style
            "", // snowplow Solid Style
            "", // snowmobile Regular Style
            "", // ambulance  Light Style
            "", // car
            "", // car-side
            "", // baby-carriage
            "", // rocket
            "", // space-shuttle
            "", // tractor
            "", // truck
            "", // truck-monster
            "", // paper-plane
            "", // fighter-jet
            "", // helicopter
            "", // bicycle
            "", // truck-pickup
            "", // plane
            "", // cars
        )
        mMapOfData.put(ActivitySection.moving.getLocalizedString(context), moving)

        var objects = arrayOf(
            "", // atom Regular Style
            "", // brain Regular Style
            "", // capsules Regular Style
            "", // dna Regular Style
            "", // flask-poison Regular Style
            "", // head-side-brain Regular Style
            "", // microscope Regular Style
            "", // pills Regular Style
            "", // prescription-bottle Regular Style
            "", // mortar-pestle Regular Style
            "", // skull-crossbones Regular Style
            "", // tablets Regular Style
            "", // tally Regular Style
            "", // vials Regular Style
            "", // prescription Solid Style
            "", // joint Light Style
            "", // dharma chakra Light Style
            "", // binoculars Solid Style
            "", // hat-winter Regular Style
            "", // ear-muffs Regular Style
            "", // hat-wizard  Light Style
            "", // books Regular Style
            "", // axe Solid Style
            "", // headphones Solid Style
            "", // suitcase-rolling Light Style
            "", // backpack Solid Style
            "", // luggage-cart Light Style
            "", // luggage-cart Solid Style
            "", // fireplace Solid Style
            "", // chair-office Solid Style
            "", // chair Solid Style
            "", // couch Regular Style
            "", // tv Light Style
            "", // tv-retro Light Style
            "", // shovel-snow Solid State
            "", // camera-retro Regular Style
            "", // camera-alt Solid Style
            "", // gamepad Regular Style
            "", // shopping-cart
            "", // toilet-paper-alt Solid Style
            "", // clipboard Light Style
            "", // envelope Light Style
            "", // envelope-open Light Style
            "", // highlighter Light Style
            "", // gift Light Style
        )
        mMapOfData.put(ActivitySection.objects.getLocalizedString(context), objects)


        //Now add views
        val containerRoot = binding.activityGroupContainer
        for (key in mMapOfData.keys) {
            val iconsList = mMapOfData.get(key)

            if (iconsList != null && iconsList.size > 0) {

                val layout = layoutInflater.inflate(
                    R.layout.view_user_activity,
                    containerRoot,
                    false
                )
                val tvTitle = layout.findViewById<TextView>(R.id.tv_group_title)
                tvTitle.setText(key)

                val gridView = layout.findViewById<CustomGridView>(R.id.gridview)
                //gridView.isNestedScrollingEnabled = false
                val adapter1 = UserActivityIconAdapter(context, iconsList, object :
                    ActivityIconSelectListener {
                    override fun onIconSelected(
                        adapter: UserActivityIconAdapter,
                        imageName: String
                    ) {
                        binding.tvImageName.setText(imageName)
                        binding.tvImageName.setCompoundDrawables(null,null,null,null)
                        if(mCurrAdapter != null && mCurrAdapter != adapter) {
                            mCurrAdapter!!.removeSelection()
                        }
                        mCurrAdapter = adapter

                        //update next enabled
                        enableDisableNext()
                    }

                })
                gridView.adapter = adapter1
                binding.activityGroupContainer.addView(layout)
            }
        }

        binding.etTitle.addTextChangedListener(this)
        //editor event listener
        binding.etTitle.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener true
        }
    }

    fun enableDisableNext() {
        //First check for icon
        if(activity != null && isAdded) {
            val iconName = binding.tvImageName.text.toString()
            if (iconName.isEmpty()) {
                //disable
                (requireActivity() as HowYouFeelActivity).setNextEnabled(false)
                return
            }

            val activityName = binding.etTitle.text.toString()
            if (activityName.isEmpty()) {
                //disable
                (requireActivity() as HowYouFeelActivity).setNextEnabled(false)
                return
            }

            //enable
            (requireActivity() as HowYouFeelActivity).setNextEnabled(true)
        }
    }



    fun createRecord(): UserActivity? {

        //First check for icon
        val iconName = binding.tvImageName.text.toString()
        if(iconName.isEmpty()) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.please_select_icon))
            return null
        }

        val activityName = binding.etTitle.text.toString()
        if(activityName.isEmpty()) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.please_enter_name_situation))
            binding.etTitle.requestFocus()
            return null
        }

        //Create an entry in table
        val userActivity = UserActivity("")
        userActivity.imageName = iconName
        userActivity.title = activityName
        return userActivity
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        enableDisableNext()
    }
}