package com.health.mental.mooditude.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.*
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.utils.ImageUtils
import com.health.mental.mooditude.utils.isInternetAvailable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream


/**
 * Created by Jayshree.Rathod on 21-09-2017.
 */
class FirebaseStorageHelper private constructor() {

    private val TAG = this.javaClass.simpleName
    private val mStorageRef: StorageReference
    private val mFirebaseStorage: FirebaseStorage
    private val PATH_PROFILE_IMAGE    =   "Profile/Images/%s.jpg"
    private val PATH_JOURNAL_ENTRY    =   "UserEntries/%s/%s.jpg"
    private val PATH_POST_ENTRY    =   "UserEntries/%s/%s.jpg"
    private val PATH_COMMENT_ENTRY    =   "UserEntries/%s/%s.jpg"

    init {
        mFirebaseStorage = FirebaseStorage.getInstance()
        mStorageRef = mFirebaseStorage.reference
    }

    //private members
    private object Holder {
        val INSTANCE = FirebaseStorageHelper()
    }

    companion object {
        val instance: FirebaseStorageHelper by lazy { Holder.INSTANCE }
    }


    interface OnProgressStatusListener {
        fun onCompleted(any: Any?)
    }

    /**
     * get bytes array from Uri.
     *
     * @param context current context.
     * @param uri uri fo the file to read.
     * @return a bytes array.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getBytes(context: Context, uri: Uri?): ByteArray? {
        val iStream = context.contentResolver.openInputStream(uri!!)
        return try {
            getBytes(iStream!!)
        } finally {
            // close the stream
            try {
                iStream!!.close()
            } catch (ignored: IOException) { /* do nothing */
            }
        }
    }

    /**
     * get bytes from input stream.
     *
     * @param inputStream inputStream.
     * @return byte array read from the inputStream.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        var bytesResult: ByteArray? = null
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        try {
            var len: Int = 0
            len = inputStream.read(buffer)
            while (len != -1) {
                byteBuffer.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            bytesResult = byteBuffer.toByteArray()
        } finally {
            // close the stream
            try {
                byteBuffer.close()
            } catch (ignored: IOException) { /* do nothing */
            }
        }
        return bytesResult
    }

    fun uploadProfileImage(context: Context, fileUri: Uri?, listener: OnProgressStatusListener,
                           progressListener: OnProgressListener<UploadTask.TaskSnapshot>?): String? {
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            listener.onCompleted(null)
            return null
        }
        val storagePath = String.format(PATH_PROFILE_IMAGE, user.userId)
        return uploadImage(context, storagePath, fileUri,listener, progressListener )
    }

    fun uploadEntryImage(context: Context, fileUri: Uri?, listener: OnProgressStatusListener,
                           progressListener: OnProgressListener<UploadTask.TaskSnapshot>?): String? {
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            listener.onCompleted(null)
            return null
        }
        val storagePath = String.format(PATH_JOURNAL_ENTRY, user.userId, System.currentTimeMillis())
        return uploadImage(context, storagePath, fileUri,listener, progressListener )
    }

    fun uploadPostImage(context: Context, fileUri: Uri?, listener: OnProgressStatusListener,
                         progressListener: OnProgressListener<UploadTask.TaskSnapshot>?): String? {
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            listener.onCompleted(null)
            return null
        }
        val storagePath = String.format(PATH_POST_ENTRY, user.userId, System.currentTimeMillis())
        return uploadImage(context, storagePath, fileUri,listener, progressListener )
    }

    fun uploadCommentImage(context: Context, fileUri: Uri?, listener: OnProgressStatusListener,
                           progressListener: OnProgressListener<UploadTask.TaskSnapshot>?): String? {
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            listener.onCompleted(null)
            return null
        }
        val storagePath = String.format(PATH_COMMENT_ENTRY, user.userId, System.currentTimeMillis())
        return uploadImage(context, storagePath, fileUri,listener, progressListener )
    }


    /**
     * This method uploads a file on firebase storage
     */
    private fun uploadImage(context: Context, storagePath:String, fileUri: Uri?, listener: OnProgressStatusListener,
                   progressListener: OnProgressListener<UploadTask.TaskSnapshot>?): String? {

        //check for internet
        if (!isInternetAvailable(context) || fileUri == null) {
            listener.onCompleted(null)
            return null
        }

        val bmp = ImageUtils.rotateTheImage(context, fileUri)

        //Scale down the quality
        val baos = ByteArrayOutputStream()
        //Scale down the quality
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos)

        val data = baos.toByteArray()

        val riversRef = mStorageRef.child(storagePath)
        val task = riversRef.putBytes(data)
        task.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {

            // Get a URL to the uploaded content
            val downloadUrlTask = riversRef.downloadUrl
            downloadUrlTask.addOnSuccessListener {
                if(it != null) {
                    val downloadUrl = it
                    debugLog(TAG, "Download URL : " + downloadUrl)
                    listener.onCompleted(downloadUrl)
                }
            }
                .addOnFailureListener {
                    listener.onCompleted(null)
                }

        })
        .addOnFailureListener(OnFailureListener { exception ->

            errorLog(TAG, "Failure : UploadTask : " + exception.localizedMessage)
            listener.onCompleted(null)
        })

        if (progressListener != null) {
            task.addOnProgressListener(progressListener)
        }

        return riversRef.path
    }


    /**
     * This method uploads a file on firebase storage
     */
    private fun uploadFileFromUri(context: Context, storagePath: String, fileUri: Uri?, userId: String, listener: OnProgressStatusListener,
                   progressListener: OnProgressListener<UploadTask.TaskSnapshot>?) {

        //check for internet
        if (!isInternetAvailable(context) || fileUri == null) {
            listener.onCompleted(null)
            return
        }

        try {
            val data = getBytes(context, fileUri)
            if(data == null) {
                listener.onCompleted(null)
                return
            }

            val riversRef = mStorageRef.child(storagePath)
            debugLog(TAG, "path : " + riversRef.path + " : " + riversRef.downloadUrl)
            val task = riversRef.putBytes(data)
            task.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {

                // Get a URL to the uploaded content
                val downloadUrlTask = riversRef.downloadUrl
                downloadUrlTask.addOnSuccessListener {
                    if (it != null) {
                        val downloadUrl = it
                        debugLog(TAG, "Download URL : " + downloadUrl)
                        listener.onCompleted(downloadUrl)
                    }
                }
                    .addOnFailureListener {
                        listener.onCompleted(null)
                    }

            })
                .addOnFailureListener(OnFailureListener { exception ->

                    errorLog(TAG , "Failure : UploadTask : " + exception.localizedMessage)
                    listener.onCompleted(null)
                })

            if (progressListener != null) {
                task.addOnProgressListener(progressListener)
            }
        }
        catch (e:Exception) {
            errorLog(TAG, "Exception in upload file from uri : " + e.localizedMessage)
            e.printStackTrace()
        }

    }


    /**
     * Upload bitmap directly, used to save score card on storage
     */
    private fun uploadBitmap(context: Context, storagePath: String, bitmap: Bitmap, userId: String, listener: OnProgressStatusListener) {

        //check for internet
        if (!isInternetAvailable(context)) {
            listener.onCompleted(null)
            return
        }

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val data = baos.toByteArray()

        val riversRef = mStorageRef.child(storagePath)
        val task = riversRef.putBytes(data)
        task.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
            // Get a URL to the uploaded content
            val downloadUrlTask = riversRef.downloadUrl
            downloadUrlTask.addOnSuccessListener {
                if(it != null) {
                    val downloadUrl = it
                    debugLog(TAG, "Download URL : " + downloadUrl)
                    listener.onCompleted(downloadUrl)
                }
            }

        })
        .addOnFailureListener(OnFailureListener { exception ->
            errorLog(TAG, "Failure : UploadTask : " + exception.localizedMessage)
            listener.onCompleted(null)
        })
    }


    /**
     * Download the file from firebase storage and write on local path
     */
    fun downloadFile(context: Context, url: String, localFile: File, listener: OnProgressStatusListener,
                     progressListener: OnProgressListener<FileDownloadTask.TaskSnapshot>) {

        //check for internet
        if (!isInternetAvailable(context)) {
            listener.onCompleted(false)
            return
        }

        if (url.isNotEmpty()) {

            val downloadRef = mFirebaseStorage.getReferenceFromUrl(url)
            downloadRef.getFile(localFile)
                    .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot> {

                        // Get a URL to the uploaded content
                        val totalBytes = it.totalByteCount
                        debugLog(TAG, "Downloaded totalBytes : " + totalBytes)
                        listener.onCompleted(true)
                    })
                    .addOnFailureListener(OnFailureListener { exception ->
                        listener.onCompleted(false)
                    })
                    .addOnProgressListener(progressListener)
        }
    }


    /**
     * This method will delete the file from storage
     */
    fun deleteFile(url: String) {
        debugLog(TAG, "deleteFile url : " + url)
        try {
            val riversRef = mFirebaseStorage.getReferenceFromUrl(url)
            riversRef.delete()
                    .addOnSuccessListener(OnSuccessListener {
                        debugLog(TAG, "Filre removed : " + url)
                    })
                    .addOnFailureListener(OnFailureListener { exception ->
                        errorLog(TAG, "Failure : deleteFile : " + exception.localizedMessage)
                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getStorageRef(storageRefPath: String): StorageReference {
        return FirebaseStorage.getInstance().getReference(storageRefPath)
    }


    /**
     * Check and returns active uploading task
     */
    fun getActiveUploadTask( storageRef: StorageReference): UploadTask? {

        val list = storageRef.activeUploadTasks
        debugLog(TAG, "Upload task list size : " + list.size)
        if (list.size > 0) {
            return list.get(0)
        }
        return null
    }


    /**
     * Check and returns a list of active downloading task
     */
    fun getActiveDownloadTasks(storageRefPath: String): List<FileDownloadTask> {
        val storageRef = FirebaseStorage.getInstance().getReference(storageRefPath)
        return storageRef.activeDownloadTasks
    }

}
