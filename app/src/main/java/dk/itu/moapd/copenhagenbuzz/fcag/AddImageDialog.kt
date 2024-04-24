package dk.itu.moapd.copenhagenbuzz.fcag


import android.app.AlertDialog
import android.content.Context




class AddImageDialog(context: Context) : AlertDialog(context) {

    fun showDialog() {
        AlertDialog.Builder(context)
            .setTitle("Add Image")
            .setMessage("Would you like to?")
            .setPositiveButton("Upload a photo") { dialog, which ->
                // if (auth.currentUser != null) {
                //     selectImage()
                // }
            }
            .setNegativeButton("Take a photo") { dialog, which ->
                // dispatchTakePictureIntent()
            }
            .show()
    }






}