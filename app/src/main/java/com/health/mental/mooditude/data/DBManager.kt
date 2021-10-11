package com.health.mental.mooditude.data

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.health.mental.mooditude.services.freshchat.ChatService
import com.health.mental.mooditude.core.GoOfflineWhenInvisible
import com.health.mental.mooditude.data.entity.*
import com.health.mental.mooditude.data.model.*
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.community.ApiPostComment
import com.health.mental.mooditude.data.model.community.ReactionType
import com.health.mental.mooditude.data.model.community.SortingType
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.repository.*
import com.health.mental.mooditude.utils.*
import com.health.mental.mooditude.utils.MEDITATION_CATEGORY_FILENAME
import com.health.mental.mooditude.worker.SeedDatabaseWorker
import com.mindorks.example.coroutines.data.local.DatabaseBuilder
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.ArrayList


/**
 * Created by Jayshree Rathod on 02,July,2021
 */
class DBManager private constructor(private val mContext: Application) {

    private val TAG = this.javaClass.simpleName
    private val mDatabase: DatabaseReference
    private val mFirebaseDatabase: FirebaseDatabase
    private val mFireStore: FirebaseFirestore

    //Repositories
    private val mMeditationRepository: FBMeditationRepository
    private val mJournalRepository: FBJournalRepository
    private val mCommunityRepository: FBCommunityRepository
    private val mRewardRepository: FBRewardRepository
    private val m3AssessmentRepository: M3AssessmentRepository
    private val mBadgeRepository: FBBadgeRepository
    private val mCourseRepository: FBCourseRepository
    private val mRoutineRepository: FBRoutineRepository
    private val mProfileRepository: FBProfileRepository
    private val mArticlesRepository: FBArticlesRepository
    private val mAccountRepository: FBAccountRepository
    private val mTherapistRepository: FBTherapistRepository

    private val mSituationRepository: SituationRepository

    //Application Database
    private var mAppDB: AppDatabase

    init {

        mAppDB = DatabaseBuilder.getInstance(mContext)
        //initialize()
        //Enable offline data
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseDatabase.setPersistenceEnabled(true)
        mDatabase = mFirebaseDatabase.reference

        mFireStore = Firebase.firestore

        mMeditationRepository = FBMeditationRepository(mAppDB, mDatabase, mFireStore)
        mJournalRepository = FBJournalRepository(mAppDB, mDatabase, mFireStore)
        mCommunityRepository = FBCommunityRepository(mAppDB, mDatabase, mFireStore)
        mRewardRepository = FBRewardRepository(mAppDB, mDatabase, mFireStore)
        m3AssessmentRepository = M3AssessmentRepository(mAppDB, mDatabase, mFireStore)
        mBadgeRepository = FBBadgeRepository(mAppDB, mDatabase, mFireStore)
        mRoutineRepository = FBRoutineRepository(mAppDB, mDatabase, mFireStore)
        mCourseRepository = FBCourseRepository(mAppDB, mDatabase, mFireStore)
        mProfileRepository = FBProfileRepository(mAppDB, mDatabase, mFireStore)
        mArticlesRepository = FBArticlesRepository(mAppDB, mDatabase, mFireStore)
        mAccountRepository = FBAccountRepository(mAppDB, mDatabase, mFireStore)
        mTherapistRepository = FBTherapistRepository(mAppDB, mDatabase, mFireStore)

        mSituationRepository = SituationRepository(mAppDB)

    }

    companion object {
        private val CONFIGURATIONS_PATH = "configurations"

        lateinit var instance: DBManager

        fun createManager(context: Application) {
            instance = DBManager(context)
            instance.initialization(context)
        }
    }

    /**
     * This method checks wheather Firebase is connected or not
     */
    private fun checkConnectionState() {
        val connectedRef = mFirebaseDatabase.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    println("connected")
                } else {
                    println("not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                System.err.println("Listener was cancelled")
            }
        })
    }

    fun initialization(context: Application) {
        FirebaseApp.initializeApp(context)
        context.registerActivityLifecycleCallbacks(GoOfflineWhenInvisible(mFirebaseDatabase))
    }


    fun getServerConfigurations() {
        val query1 = mDatabase.child(CONFIGURATIONS_PATH)
        query1.keepSynced(true)
        query1.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    debugLog(TAG, "Configurations found " + p0.value)

                    val newConfig = p0.getValue(ApiServerConfiguration::class.java)
                    //Articles
                    if (newConfig != null) {

                        //check with existing
                        val existingConfig = ApiServerConfiguration.getExisting()
                        var success = false

                        //Meditations
                        success = processMeditationData(newConfig, existingConfig)
                        if (success) {
                            existingConfig.meditationsVersion = newConfig.meditationsVersion

                            /*if AppInfo.isOnboardingDone {
                                var updatedContent = UpdatedContent.retrieve()
                                updatedContent.newMeditation = true
                                updatedContent.save()
                            }

                            NotificationCenter.default.post(name: Constant.NotificationId.fbDataChangedEvent, object: FBDataType.meditation)*/
                        }

                        //Jokes
                        success = processJokesData(newConfig, existingConfig)
                        if (success) {
                            existingConfig.jokeVersion = newConfig.jokeVersion
                        }

                        //Quotes
                        success = processQuoteData(newConfig, existingConfig)
                        if (success) {
                            existingConfig.quoteVersion = newConfig.quoteVersion
                        }

                        //Affirmations
                        success = processAffirmationData(newConfig, existingConfig)
                        if (success) {
                            existingConfig.affirmationVersion = newConfig.affirmationVersion
                        }

                        //Tips
                        success = processTipsData(newConfig, existingConfig)
                        if (success) {
                            existingConfig.tipVersion = newConfig.tipVersion
                        }

                        //Forum Categories
                        success = processForumCategoryData(newConfig, existingConfig)
                        if (success) {
                            existingConfig.forumCategoriesVersion = newConfig.forumCategoriesVersion

                            /*if AppInfo.isOnboardingDone {
                                var updatedContent = UpdatedContent.retrieve()
                                updatedContent.newForumCategory = true
                                updatedContent.save()
                            }

                            NotificationCenter.default.post(name: Constant.NotificationId.fbDataChangedEvent, object: FBDataType.communityCategory)*/

                        }

                        //Journal Prompts
                        success = processPromptData(newConfig, existingConfig)
                        if (success) {
                            existingConfig.journalPromptsVersion = newConfig.journalPromptsVersion

                            /*if AppInfo.isOnboardingDone {
                                var updatedContent = UpdatedContent.retrieve()
                                updatedContent.newJournalPrompt = true
                                updatedContent.save()
                            }

                            NotificationCenter.default.post(name: Constant.NotificationId.fbDataChangedEvent, object: FBDataType.journalPrompt)*/
                        }

                        //Courses
                        success = processCoursesData(newConfig, existingConfig)
                        if (success) {
                            existingConfig.coursesVersion = newConfig.coursesVersion

                            /*if AppInfo.isOnboardingDone {
                                var updatedContent = UpdatedContent.retrieve()
                                updatedContent.newCourses = true
                                updatedContent.save()
                            }

                            NotificationCenter.default.post(name: Constant.NotificationId.fbDataChangedEvent, object: FBDataType.course)*/
                        }

                        //M3Assessments
                        success = processM3AssessmentData(newConfig, existingConfig)
                        if (success) {
                            existingConfig.m3AssessmentVersion = newConfig.m3AssessmentVersion
                        }

                        //Practices
                        success = processPractices(newConfig, existingConfig)
                        if (success) {
                            existingConfig.practiceVersion = newConfig.practiceVersion

                            /*if AppInfo.isOnboardingDone {
                                var updatedContent = UpdatedContent.retrieve()
                                updatedContent.newPractices = true
                                updatedContent.save()
                            }*/
                        }

                        //States
                        success = processStates(newConfig, existingConfig)
                        if (success) {
                            existingConfig.statesVersion = newConfig.statesVersion
                        }

                        //Articles
                        success = processArticles(newConfig, existingConfig)
                        if (success) {
                            existingConfig.articleVersion = newConfig.articleVersion
                        }
                        //Now save
                        existingConfig.save()
                    }

                } else {
                    errorLog(TAG, "Configurations ERROR ")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                errorLog(TAG, "Error :: " + error.code + " : " + error.message)
            }
        })
    }

    //MARK:: Meditations
    private fun processMeditationData(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.meditationsVersion >= newConfig.meditationsVersion) {
            return success
        }

        val language = newConfig.languageAvailability(newConfig.meditationLanguages)
        var processedMeditationData = true


        mMeditationRepository.fetchMeditationCategories(language)

        mMeditationRepository.fetchMeditations(language)

        success = true
        return success
    }

    //MARK:: Journal Prompts
    private fun processPromptData(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.journalPromptsVersion >= newConfig.journalPromptsVersion) {
            return success
        }

        val language = newConfig.languageAvailability(newConfig.journalPromptLanguages)
        var processedPromptData = true

        mJournalRepository.fetchJournalCategories(language)

        mJournalRepository.getJournalPrompts(language)

        success = true
        return success

    }


    //MARK:: Forum Categories
    private fun processForumCategoryData(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.forumCategoriesVersion >= newConfig.forumCategoriesVersion) {
            return success
        }

        val language = newConfig.languageAvailability(newConfig.forumCategoryLanguages)
        var processedPromptData = true

        mCommunityRepository.fetchPostCategories(language)

        success = true
        return success

    }

    //MARK:: Rewards
    private fun processJokesData(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.jokeVersion >= newConfig.jokeVersion) {
            return success
        }

        val language = newConfig.languageAvailability(newConfig.jokeLanguages)

        getRewards(RewardType.Joke, language)

        success = true
        return success

    }

    //MARK:: Quotes
    private fun processQuoteData(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.quoteVersion >= newConfig.quoteVersion) {
            return success
        }

        val language = newConfig.languageAvailability(newConfig.quoteLanguages)

        getRewards(RewardType.Quote, language)

        success = true
        return success
    }

    //MARK:: Affirmation
    private fun processAffirmationData(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.affirmationVersion >= newConfig.affirmationVersion) {
            return success
        }

        val language = newConfig.languageAvailability(newConfig.quoteLanguages)

        getRewards(RewardType.Affirmation, language)

        success = true
        return success
    }

    //MARK:: Tips
    private fun processTipsData(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.tipVersion >= newConfig.tipVersion) {
            return success
        }

        val language = newConfig.languageAvailability(newConfig.tipLanguages)

        getRewards(RewardType.Tip, language)

        success = true
        return success
    }

    private fun getRewards(type: RewardType, language: String) {
        mRewardRepository.getRewards(type, language)
    }

    //MARK:: COURSES
    private fun processCoursesData(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.coursesVersion >= newConfig.coursesVersion) {
            return success
        }

        val language = newConfig.languageAvailability(newConfig.coursesLanguages)
        var processedPromptData = true

        mCourseRepository.getCourses(language)

        success = true
        return success
    }


    //MARK:: M3AssessmentData
    private fun processM3AssessmentData(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.m3AssessmentVersion >= newConfig.m3AssessmentVersion) {
            return success
        }

        val language = newConfig.languageAvailability(newConfig.m3AssessmentLanguages)
        var processedPromptData = true

        m3AssessmentRepository.getM3AssessmentData(language)

        success = true
        return success
    }


    //MARK:: PRACTICES
    private fun processPractices(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.practiceVersion != null && newConfig.practiceVersion != null) {
            if (existingConfig.practiceVersion!! >= newConfig.practiceVersion!!) {
                return success
            }
        }

        var langs = "en"
        if (newConfig.practiceLanguages != null) {
            langs = newConfig.practiceLanguages!!
        }
        val language = newConfig.languageAvailability(langs)
        var processedPracticesData = true

        mRoutineRepository.getAllPractices(language)

        success = true
        return success
    }

    //MARK:: STATES
    private fun processStates(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.statesVersion != null && newConfig.statesVersion != null) {
            if (existingConfig.statesVersion!! >= newConfig.statesVersion!!) {
                return success
            }
        }

        var langs = "en"
        if (newConfig.stateLanguages != null) {
            langs = newConfig.stateLanguages!!
        }
        val language = newConfig.languageAvailability(langs)
        var processedStatesData = true

        mProfileRepository.getStates(language)

        success = true
        return success
    }

    //MARK:: ARTICLES
    private fun processArticles(
        newConfig: ApiServerConfiguration,
        existingConfig: ApiServerConfiguration
    ): Boolean {

        var success = false
        if (existingConfig.articleVersion != null && newConfig.articleVersion != null) {
            if (existingConfig.articleVersion!! >= newConfig.articleVersion!!) {
                return success
            }
        }

        var langs = "en"
        if (newConfig.articleLanguages != null) {
            langs = newConfig.articleLanguages!!
        }
        val language = newConfig.languageAvailability(langs)
        var processedArtitleData = true

        mArticlesRepository.getArticles(language)

        //DatabaseBuilder.getInstance(mContext!!).articleDao().getAll()

        success = true
        return success
    }


    suspend fun getArticles(): List<ApiArticle> {
        return listOf<ApiArticle>()
    }

    /*fun checkAccountExists(email: String, listener: FBQueryCompletedListener) {
        mAccountRepository.checkAccountExists(email)
    }*/

    fun writeNewUser(user: AppUser, listener: FBQueryCompletedListener) {
        mAccountRepository.writeNewUser(user, listener)
    }

    fun processInvitationCode(code: String) {
        mAccountRepository.processInvitationCode(code)
    }

    fun fetchUser(userId: String, listener: FBQueryCompletedListener) {
        mAccountRepository.fetchUser(userId, listener)
    }

    fun updateUserName(name: String) {
        mAccountRepository.updateName(name)
    }

    fun updateUserState(state: String) {
        mAccountRepository.updateState(state)
    }

    fun updateUserVeteran(veteran: Veteran) {
        mAccountRepository.updateVeteran(veteran)
    }

    fun updateUserProviderAttributes(attributes: TherapistProviderAttributes) {
        mAccountRepository.updateProviderAttributes(attributes)
    }

    fun updateUserPhone(phone: String) {
        mAccountRepository.updatePhone(phone)
    }

    fun updateUserContactTime(contact: ContactTime) {
        mAccountRepository.updateContactTime(contact)
    }

    fun updateUserPaymentMethod(method: PaymentMethod) {
        //mAccountRepository.updatePaymentMethod(method)
    }

    fun updateUserAgeGroup(selectedAge: Int) {
        mAccountRepository.updateAge(selectedAge)
    }

    fun updateGender(gender: Int) {
        mAccountRepository.updateGender(gender)
    }

    fun updateUserGoal(goal: UserTopGoal) {
        mAccountRepository.updateGoal(goal)
    }

    fun updateUserChallenges(selection: String) {
        mAccountRepository.updateUserChallenges(selection)
    }

    fun updateHealthProfessional(selection: Boolean) {
        mAccountRepository.updateHealthProfessional(selection)
    }

    fun updateCBT(selection: Boolean) {
        mAccountRepository.updateCBT(selection)
    }

    fun updateMakePromise(selection: Boolean) {
        mAccountRepository.updateMakePromise(selection)
    }

    fun updateReminder(selection: Boolean) {
        mAccountRepository.updateReminder(selection)
    }

    fun profileCompleted(selection: Boolean) {
        mAccountRepository.profileCompleted(selection)
    }

    fun setFcmToken(token: String) {
        mAccountRepository.setFcmToken(token)
        ChatService.instance.registerFCMToken(token)
    }

    fun removeFcmToken() {
        mAccountRepository.removeFcmToken()
        ChatService.instance.removeFCMToken()
    }

    fun setFreshChatRestoreId(id:String) {
        mAccountRepository.setFreshChatRestoreId(id)
    }

    fun logout(): Boolean {
        removeListeners()
        //Remove fcm token
        removeFcmToken()

        //Clears data
        mFireStore.clearPersistence()

        //mFireStore.terminate()

        val callable = object : Callable<Boolean> {
            override fun call(): Boolean {
                //Clear database
                mAppDB.clearAllTables()
                // mAppDB.close()
                //mContext.deleteDatabase(DATABASE_NAME)

                //Remove instance
                //DatabaseBuilder.clearDatabase()
                debugLog(TAG, "ALL DATA CLEARED")

                //Reset field value
                //mAppDB = DatabaseBuilder.getInstance(mContext)

                //Insert all default values
                //Insert data from assets
                insertDefaultData()
                return true
            }
        }

        val future: Future<Boolean> = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    fun saveM3AssessmentData(record: M3Assessment) {
        m3AssessmentRepository.saveM3Assessment(record)
    }

    fun fetchAllM3Assessments() {
        m3AssessmentRepository.fetchM3Assessments()
    }

    fun updateUserPhoto(url: String) {
        mAccountRepository.updatePhoto(url)
    }

    fun addListeners() {
        mAccountRepository.addListeners()
        mCommunityRepository.addListener()

        val syncWithServer = SharedPreferenceManager.getAutoBackup()
        //user has allowed syncing
        if (syncWithServer == true) {
            mJournalRepository.addListener()
            /*
            goalRepository.addListeners()
            routineRepository.addListeners()
            reminderRepository.addListener()
            courseRepository.addListener()
            copingSkillRepository.addListener()
            badgeRepository.addListener()*/
            m3AssessmentRepository.addListener()
            mTherapistRepository.addListener()
        }
    }

    fun removeListeners() {
        mAccountRepository.removeListeners()
        mTherapistRepository.removeListener()
        m3AssessmentRepository.removeListener()
        mJournalRepository.removeListener()
        mCommunityRepository.removeListener()
        /*

        goalRepository.removeListeners()
        routineRepository.removeListeners()
        reminderRepository.removeListener()
        courseRepository.removeListener()
        copingSkillRepository.removeListener()
        badgeRepository.removeListner()
        profileRepository.removeListeners()

         */
    }

    fun uploadTherapistRequest(request: ApiTherapistRequest, listener: FBQueryCompletedListener) {
        mTherapistRepository.uploadRequestTherapist(request, listener)
    }

    fun uploadTherapistFeedback(
        requestId: String,
        feedback: TherapistFeedback,
        listener: FBQueryCompletedListener
    ) {
        mTherapistRepository.uploadRequestTherapistFeedback(requestId, feedback, listener)
    }

    fun getLastestAssessment() = m3AssessmentRepository.getLastestAssessment()
    fun getLastestAssessmentBlocking() = m3AssessmentRepository.getLastestAssessmentBlocking()

    fun getTherapistRequestList() = mTherapistRepository.getTherapistRequestList()

    fun getAssessmentList() = m3AssessmentRepository.getAssessmentList()

    fun getListForMonth() = m3AssessmentRepository.getListForMonth()

    fun getListForQuarter() = m3AssessmentRepository.getListForQuarter()

    fun saveUserActivityRecord(record: UserActivity) = mSituationRepository.saveRecord(record)

    fun fetchAllUserActivityRecords() = mSituationRepository.fetchAll()
    fun fetchRecentUserActivityRecords() = mSituationRepository.fetchRecent()

    fun updateActivityUsageCount(list: ArrayList<String>) =
        mSituationRepository.updateUsageCount(list)

    fun saveJournalEntry(entry: Entry) = mJournalRepository.saveEntry(entry)

    fun addNewPost(post: ApiPost) = mCommunityRepository.savePost(post)

    fun getPost(postID: String, listener: FBQueryCompletedListener) = mCommunityRepository.getSinglePost(postID, listener)

    fun addNewPostComment(comment: ApiPostComment) = mCommunityRepository.saveComment(comment)

    fun thumbsUpDownComment(postId: String, commentId: String, thumbsUp: Boolean, remove: Boolean) =
        mCommunityRepository.thumbsUpDownComment(postId, commentId, thumbsUp, remove)

    fun fetchMultipleJournalEntries(startDate: Long, endDate: Long) =
        mJournalRepository.getAllEntries(startDate, endDate)

    fun fetchMultipleJournalEntries(startDate: Date, pageSize:Int) =
        mJournalRepository.loadMultiple(startDate, pageSize)

    fun getAllJournalPromptCategories() = mJournalRepository.getJournalCategories()

    fun getAllJournalPrompts() = mJournalRepository.getAllJournalPrompts()

    fun getPromptForOnboard() = mJournalRepository.getPromptForOnboard()

    fun getAllPostCategories() = mCommunityRepository.getPostCategories()

    fun getPostComments(postId: String, listener: FBQueryCompletedListener) =
        mCommunityRepository.getPostComments(postId, listener)

    //fun getAllFeedPosts() = DataHolder.instance.getFeedPosts()
    fun getFeedPostsAfter(
        afterToken: Any?, categoryId: String, orderBy: SortingType,
        listener: FBQueryCompletedListener
    ) = mCommunityRepository.getPostsAfter(afterToken, categoryId, orderBy, listener)


    fun getFeedPostsBefore(
        beforeToken: Any?, categoryId: String, orderBy: SortingType,
        listener: FBQueryCompletedListener
    ) = mCommunityRepository.getPostsBefore(beforeToken, categoryId, orderBy, listener)

    private fun insertArticlesFromAssets() {
        val type = object : TypeToken<List<Article>>() {}.type
        val list = readAssetFile(ARTICLE_DATA_FILENAME, type)
        mArticlesRepository.insertAll(list as List<Article>)
    }

    private fun insertMeditationCategoryFromAssets() {
        val type = object : TypeToken<List<MeditationCategory>>() {}.type
        val list = readAssetFile(MEDITATION_CATEGORY_FILENAME, type)
        mMeditationRepository.insertAllCategories(list as List<MeditationCategory>)
    }

    private fun insertMeditationFromAssets() {
        val type = object : TypeToken<List<MeditationInfo>>() {}.type
        val list = readAssetFile(MEDITATION_INFO_FILENAME, type)
        mMeditationRepository.insertAllMeditations(list as List<MeditationInfo>)
    }

    private fun insertUserActivityFromAssets() {
        val type = object : TypeToken<List<UserActivity>>() {}.type
        val list = readAssetFile(USER_ACTIVITY_FILENAME, type)
        mSituationRepository.insertAllActivities(list as List<UserActivity>)
    }

    private fun insertPromptCategoryFromAssets() {
        val type = object : TypeToken<List<PromptCategory>>() {}.type
        val list = readAssetFile(PROMPT_CATEGORY_FILENAME, type)
        mJournalRepository.insertAllCats(list as List<PromptCategory>)
    }

    private fun insertJournalPromptFromAssets() {
        val type = object : TypeToken<List<JournalPrompt>>() {}.type
        val list = readAssetFile(JOURNAL_PROMPT_FILENAME, type)
        mJournalRepository.insertAllPrompts(list as List<JournalPrompt>)
    }

    private fun readAssetFile(fileName: String, type: Type): List<Any> {
        mContext.assets.open(fileName).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->

                val gsonBuilder = GsonBuilder()
                    .registerTypeAdapter(
                        Boolean::class.javaObjectType,
                        SeedDatabaseWorker.BooleanObjectTypeAdapter()
                    )
                    .registerTypeAdapter(
                        Boolean::class.javaPrimitiveType,
                        SeedDatabaseWorker.BooleanPrimitiveTypeAdapter()
                    )
                    .registerTypeAdapter(
                        Date::class.javaObjectType,
                        SeedDatabaseWorker.UnixEpochDateTypeAdapter.unixEpochDateTypeAdapter
                    )
                    .registerTypeAdapter(
                        Double::class.javaObjectType,
                        SeedDatabaseWorker.CostTypeAdapter()
                    )
                    .create()


                val list: List<Any> =
                    gsonBuilder.fromJson(jsonReader, type)

                return list
            }
        }
    }

    fun insertDefaultData() {
        insertArticlesFromAssets()
        insertMeditationCategoryFromAssets()
        insertMeditationFromAssets()
        insertUserActivityFromAssets()
        insertPromptCategoryFromAssets()
        insertJournalPromptFromAssets()
    }

    fun addOrRemoveBookmark(postId: String, listener: FBQueryCompletedListener) =
        mCommunityRepository.addOrRemoveBookmark(postId, listener)

    fun addOrRemoveReaction(
        postId: String,
        type: ReactionType,
        listener: FBQueryCompletedListener? = null
    ) {
        mCommunityRepository.addOrRemoveReaction(postId, type, listener)
    }

    fun getAllFeedPostsByCategory(categoryId: String): ArrayList<ApiPost> {
        val list = ArrayList<ApiPost>()
        /*val listAll = getAllFeedPosts()
        for(item in listAll) {
            if(item.category.equals(categoryId)) list.add(item)
        }*/
        return list
    }

    fun fetchUserProfile(userId: String, listener: FBQueryCompletedListener) {
        mAccountRepository.fetchUserProfile(userId, listener)
    }

    fun getMyPosts(orderBy: SortingType, listener: FBQueryCompletedListener) =
        mCommunityRepository.getMyPosts(orderBy, listener)

    fun getBookmarks(orderBy: SortingType, listener: FBQueryCompletedListener) =
        mCommunityRepository.getBookmarks(orderBy, listener)

    fun getReportedPosts(orderBy: SortingType, listener: FBQueryCompletedListener) =
        mCommunityRepository.getReportedPosts(orderBy, listener)

    fun reportPost(postId: String, listener: FBQueryCompletedListener) =
        mCommunityRepository.addReport(postId, listener)

    fun deletePost(postId: String, listener: FBQueryCompletedListener)
    = mCommunityRepository.deletePost(postId, listener)

    fun deleteEntry(entry: Entry, listener: FBQueryCompletedListener)
            = mJournalRepository.deleteEntry(entry, listener)

    fun addListenerForPrivateProfile(listener: FBQueryCompletedListener) =
        mAccountRepository.registerProfileChangeListner(listener)

    fun removeProfileDataChangeListener(userid: String, listener: ValueEventListener) {
        mAccountRepository.removeProfileListener(userid, listener)
    }

}