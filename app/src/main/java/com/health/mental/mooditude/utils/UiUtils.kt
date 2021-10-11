package com.health.mental.mooditude.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.TintableBackgroundView
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.snackbar.Snackbar
import com.health.mental.mooditude.R
import com.itextpdf.text.*
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.util.*


/**
 * Created by Jayshree Rathod on 10,July,2021
 */

object UiUtils {

    fun setBackgroundTint(editText: AppCompatEditText, colorStateList: ColorStateList) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            (editText as TintableBackgroundView).setSupportBackgroundTintList(colorStateList);
        } else {
            ViewCompat.setBackgroundTintList(editText, colorStateList);
        }
    }

    fun checkAndRequestPermissions(context: Activity?, requestId: Int): Boolean {
        val WExtstorePermission = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val cameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                .add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                context, listPermissionsNeeded
                    .toTypedArray(),
                requestId
            )
            return false
        }
        return true
    }

    fun hideKeyboard(context: Activity) {
        if(context.currentFocus != null) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                context.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun showKeyboard(context: Context) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Show
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    fun capitalizeString(str: String): String {
        var retStr = str
        try { // We can face index out of bound exception if the string is null
            retStr = str.substring(0, 1).uppercase() + str.substring(1)
        } catch (e: Exception) {
        }
        return retStr
    }

    fun sendViewViaMail(
        view: View,
        baseContext: Context,
        activityContextOnly: Context,
        textToMail: String
    ) {
        view.post {
            val heightG = view.height
            val widthG = view.width
            sendViewViaMail(view, baseContext, activityContextOnly, widthG, heightG, textToMail)
        }
    }


    private fun sendViewViaMail(
        view: View,
        baseContext: Context,
        activityContextOnly: Context,
        widthG: Int,
        heightG: Int,
        textToMail: String
    ) {
        val bitmap: Bitmap = createViewBitmap(view, widthG, heightG)
        var imageUri: Uri? = null
        var file: File? = null
        var fos1: FileOutputStream? = null
        try {
            val folder =
                File(activityContextOnly.cacheDir.toString() + File.separator + "My Temp Files")
            var success = true
            if (!folder.exists()) {
                success = folder.mkdir()
            }
            val filename = "img.jpg"
            file = File(folder.path, filename)
            fos1 = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos1)
            imageUri = FileProvider.getUriForFile(
                activityContextOnly,
                activityContextOnly.packageName + ".my.package.name.provider",
                file
            )
        } catch (ex: Exception) {
        } finally {
            try {
                fos1!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val emailIntent1 = Intent(Intent.ACTION_SEND)
        emailIntent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        emailIntent1.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>())
        emailIntent1.putExtra(Intent.EXTRA_STREAM, imageUri)
        emailIntent1.putExtra(Intent.EXTRA_SUBJECT, "[" + "COMPANY_HEADER" + "]")
        emailIntent1.putExtra(Intent.EXTRA_TEXT, textToMail)
        emailIntent1.data = Uri.parse("mailto:" + "mail@gmail.com") // or just "mailto:" for blank
        emailIntent1.type = "image/jpg"
        activityContextOnly.startActivity(Intent.createChooser(emailIntent1, "Send email using"))
    }

    fun shareAssessment(
        view: View,
        activityContextOnly: Context
    ) {

        val bitmap: Bitmap? = takeSnapShot(view)
        if (bitmap == null) {
            return
        }
        var imageUri: Uri? = null
        var file: File? = null
        var fos1: FileOutputStream? = null
        try {
            val folder =
                File(activityContextOnly.cacheDir.toString() + File.separator + "My Temp Files")
            var success = true
            if (!folder.exists()) {
                success = folder.mkdir()
            }
            val filename = "img.jpg"
            file = File(folder.path, filename)
            fos1 = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos1)
            imageUri = FileProvider.getUriForFile(
                activityContextOnly,
                activityContextOnly.packageName + ".provider",
                file
            )
        } catch (ex: Exception) {
        } finally {
            try {
                fos1!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.setType("image/*")

        activityContextOnly.startActivity(Intent.createChooser(shareIntent, "Share score via: "))
    }

    fun takeSnapShot(view: View?): Bitmap? {
        var image: Bitmap? = null
        if (view != null) {
            var width = 800
            var height = 800

            if (view.measuredWidth > 0 && view.measuredHeight > 0) {
                width = view.measuredWidth
                height = view.measuredHeight
            }
            image = Bitmap.createBitmap(
                width,
                height, Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(image)
            canvas.drawColor(ContextCompat.getColor(view.context, R.color.white))
            view.draw(canvas)

            //path = saveBitmapAsFile(context, image, fileName)
        }
        return image
    }

    fun createViewBitmap(view: View, widthG: Int, heightG: Int): Bitmap {
        val viewBitmap = Bitmap.createBitmap(widthG, heightG, Bitmap.Config.RGB_565)
        val viewCanvas = Canvas(viewBitmap)
        val backgroundDrawable = view.background
        if (backgroundDrawable != null) {
            backgroundDrawable.draw(viewCanvas)
        } else {
            viewCanvas.drawColor(Color.WHITE)
            view.draw(viewCanvas)
        }
        return viewBitmap
    }

    fun createPdfFileUri(
        view: View,
        name: String,
        email: String,
        activityContextOnly: Context
    ): Uri? {

        val bitmap: Bitmap? = takeSnapShot(view)
        if (bitmap == null) {
            return null
        }
        var pdfFile: File? = null
        var fos1: FileOutputStream? = null
        var pdfUri: Uri? = null
        try {
            val folder =
                File(activityContextOnly.cacheDir.toString() + File.separator + "My Temp Files")
            var success = true
            if (!folder.exists()) {
                success = folder.mkdir()
            }
            val filename = "img.pdf"


            //Now create the name of your PDF file that you will generate
            pdfFile = File(folder.path, filename)
            fos1 = FileOutputStream(pdfFile)

            val document = Document()
            val writer = PdfWriter.getInstance(document, fos1)
            document.open()

            //add text
            val table = PdfPTable(1)
            table.addCell("Name : " + name)
            table.addCell("Email : " + email)

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            val byteArray = stream.toByteArray();
            //addImage(document, byteArray);

            val cb: PdfContentByte = writer.getDirectContentUnder()
            table.addCell(getWatermarkedImage(cb, Image.getInstance(byteArray), "Bruno"))
            document.add(table)


            document.close();

            pdfUri = FileProvider.getUriForFile(
                activityContextOnly,
                activityContextOnly.packageName + ".provider",
                pdfFile
            )
        } catch (e: Exception) {
            e.printStackTrace();
        } finally {
            try {
                fos1!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return pdfUri
        /*val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.setType("application/pdf")


        activityContextOnly.startActivity(Intent.createChooser(shareIntent, "Share score via: "))*/

    }


    @Throws(DocumentException::class)
    fun getWatermarkedImage(cb: PdfContentByte, img: Image, watermark: String?): Image? {
        val width = img.scaledWidth
        val height = img.scaledHeight
        val template = cb.createTemplate(width, height)
        template.addImage(img, width, 0f, 0f, height, 0f, 0f)
        ColumnText.showTextAligned(
            template,
            Element.ALIGN_CENTER,
            Phrase(watermark, com.itextpdf.text.Font(Font.FontFamily.COURIER)),
            width / 2,
            height / 2,
            30f
        )
        return Image.getInstance(template)
    }

    private fun addImage(document: Document, byteArray: ByteArray) {
        var image: Image? = null
        try {
            image = Image.getInstance(byteArray)
            image.setAbsolutePosition(0f, 0f)
            //image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight())
            //image.scalePercent(50f)
            val scaleRatio = calculateScaleRatio(document, image)
            if (scaleRatio < 1f) {
                image.scalePercent(scaleRatio * 100f)
            }
        } catch (e: BadElementException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // image.scaleAbsolute(150f, 150f);
        try {
            document.addTitle("Title of PDF")
            document.addHeader("name", "jayshree")
            document.addHeader("email", "shree@mmoody.com")
            document.addSubject("pdf of assessment")
            document.add(image)
        } catch (e: DocumentException) {
            e.printStackTrace()
        }
    }

    /**
     * Calculate scale ratio required to fit supplied image in the supplied PDF document.
     * @param doc    PDF to fit image in.
     * @param image  Image to be converted into a PDF.
     * @return       Scale ratio (0.0 - 1.0), or 1.0 if no scaling is required.
     */
    private fun calculateScaleRatio(doc: Document, image: Image): Float {
        var scaleRatio: Float
        val imageWidth = image.width
        val imageHeight = image.height
        if (imageWidth > 0 && imageHeight > 0) {
            // Firstly get the scale ratio required to fit the image width
            val pageSize = doc.pageSize
            val pageWidth = pageSize.width - doc.leftMargin() - doc.rightMargin()
            scaleRatio = pageWidth / imageWidth

            // Get scale ratio required to fit image height - if smaller, use this instead
            val pageHeight = pageSize.height - doc.topMargin() - doc.bottomMargin()
            val heightScaleRatio = pageHeight / imageHeight
            if (heightScaleRatio < scaleRatio) {
                scaleRatio = heightScaleRatio
            }

            // Do not upscale - if the entire image can fit in the page, leave it unscaled.
            if (scaleRatio > 1f) {
                scaleRatio = 1f
            }
        } else {
            // No scaling if the width or height is zero.
            scaleRatio = 1f
        }
        return scaleRatio
    }


    /**
     * Shows Toast with custom font size & background color
     * @param context
     * @param message
     */
    fun showSuccessToast(context: Activity, message: String, duration: Int = Toast.LENGTH_LONG) {
        val inflater = context.layoutInflater
        val layout = inflater.inflate(R.layout.view_toast_success, null)
        val textView = layout.findViewById<TextView>(R.id.text)
        textView.text = message

        //setFontForMessageText(context, text)

        val toast = Toast(context.applicationContext)
        //toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.duration = duration
        toast.view = layout
        toast.show()
        //Toast.makeText(context, message, duration).show()
    }


    /**
     * Shows Toast with custom font size & background color
     * @param context
     * @param message
     */
    fun showErrorToast(context: Activity, message: String, duration: Int = Toast.LENGTH_LONG) {
        val inflater = context.layoutInflater
        val layout = inflater.inflate(R.layout.view_toast_error, null)
        val textView = layout.findViewById<TextView>(R.id.text)
        textView.text = message

        //setFontForMessageText(context, text)

        val toast = Toast(context.applicationContext)
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.duration = duration
        toast.view = layout
        toast.show()
        //Toast.makeText(context, message, duration).show()
    }

    fun showSnackBar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundResource(R.color.white)
        val tvSnackbar =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        if (tvSnackbar != null) {
            tvSnackbar.setTextColor(ContextCompat.getColor(view.context, R.color.primaryColor))
        }
        snackbar.show()
    }

    fun showSnackBar2(context: Activity, view1: View, message: String) {
        var view = context.findViewById<View>(android.R.id.content)
        if (view == null) {
            view = view1
        }
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundResource(R.color.brand_yellow)
        val tvSnackbar =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        if (tvSnackbar != null) {
            tvSnackbar.maxLines = 4
            tvSnackbar.minLines = 2
            tvSnackbar.textSize = context.resources.getDimension(R.dimen._12ssp)
            tvSnackbar.setTextColor(ContextCompat.getColor(view.context, R.color.primaryColor))
        }
        snackbar.show()
    }

    fun loadProfileImage(url: String, imageView: ImageView, placeHolderImg:Int = R.drawable.ic_profile_default) {
        Picasso.get()
            .load(url)
            .resize(100, 100)
            .centerCrop()
            .placeholder(placeHolderImg)
            .error(placeHolderImg)
            .into(imageView)
    }

    fun loadImage(context: Context, url: String, imageView: ImageView) {

        if(url.trim().isEmpty()) {
            imageView.setImageResource(R.drawable.ic_profile_default)
            return
        }
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.apply {
            strokeWidth = 5f
            centerRadius = 30f
            setColorSchemeColors(
                ContextCompat.getColor(context, R.color.primaryColor)
            )
            start()
        }

        Picasso.get()
            .load(url)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_no_image)
            .into(imageView)
    }
}
