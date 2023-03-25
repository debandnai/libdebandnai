package com.salonsolution.app.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.salonsolution.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.io.*

object FileUtils {

    suspend fun createCopyAndReturnRealPath(context: Context, uri: Uri, isImageType: Boolean): String? {

        //copy file to cache directory and then return the path of cache directory
        return withContext(Dispatchers.IO) {
            val job = supervisorScope {
                //supervisorScope for catch the exception from await()
                async(Dispatchers.IO) {
                    val contentResolver = context.contentResolver ?: null
                    val parcelFileDescriptor =
                        contentResolver?.openFileDescriptor(uri, "r", null)
                    val originalFileName = contentResolver?.getFileName(uri)
                    Log.d("tag", "fileName: $originalFileName")

                    val mExtension = getMimeType(context, uri)
                    Log.d("tag", "extension: $mExtension")

                    //This is required to avoid crash due to file name
//                    val tmpFileName = "Doc_" + System.currentTimeMillis() + "." + mExtension
                    val tmpFileName = "profile.jpg"
                    Log.d("tag", "fileName2: $tmpFileName")


                    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), tmpFileName)

                    if (parcelFileDescriptor != null) {
                        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                        val outputStream = FileOutputStream(file)
                        inputStream.copyTo(outputStream)
                        outputStream.close()
                        inputStream.close()

                        Log.d("tag", "FileSize Before: ${file.length()}")
                        val size = file.length() / 1024
                        if (isImageType && size > Constants.FILE_UPLOAD_SIZE) {
                            fileCompress(file)
                            Log.d("tag", "FileSize After: ${file.length()}")
                        }

                        parcelFileDescriptor.close()
                        Log.d("tag", "filePath: ${file.absolutePath}")
                        file.absolutePath
                    } else {
                        null
                    }

                }
            }


            try {
                job.await()
            } catch (e: Exception) {
                Log.d("tag", "Exception: ${e.message}")
                null
            }

        }


    }

    fun deleteFileFrom(filePath: String): Boolean {
        val file = File(filePath)
        return try {
            if (file.exists()) {
                file.deleteRecursively()
                Log.d("tag", "File exist:${file.exists()}")
                true
            } else {
                false
            }


        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    private fun ContentResolver.getFileName(fileUri: Uri): String {
        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

    private fun getMimeType(context: Context, uri: Uri): String? {

        //Check uri format to avoid null
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path!!)).toString())
        }
        return extension
    }

    @Throws(IOException::class)
    private suspend fun fileCompress(file: File): Boolean {
        return withContext(Dispatchers.IO) {
            async(Dispatchers.IO) {
                Log.d("tag", "FileSize Compress Started.")
                val filePath: String = file.path
                val bitmap = BitmapFactory.decodeFile(filePath)
                //for image Resizing using width and height
                //                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 50, 100, true);
                //                    Toast.makeText(getActivity(), ""+front_fileforApi.length(), Toast.LENGTH_SHORT).show();
                //
                //                    File file = new File(front_fileforApi.getPath());
                val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
                os.close()
                Log.d("tag", "FileSize Compress Completed.")
                true
            }
        }.await()
    }

    fun getCaptureImageOutputUri(context: Context): Uri? {
        var outputFileUri: Uri? = null
        val getImage: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "profile.jpg"))
        }
        return outputFileUri
    }

    fun getFileProviderUri(context: Context, uri: Uri?): Uri? {
        var fileProvider: Uri? = null

        uri?.path?.let {
            val photoFile = File(it)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    fileProvider = FileProvider.getUriForFile(
                        context, BuildConfig.APPLICATION_ID + ".provider",
                        photoFile
                    )
                } catch (e: Exception) {
                    Log.d("tag","Error: ${e.localizedMessage}")
                }
            } else {
                try {
                    fileProvider = Uri.fromFile(photoFile)
                } catch (e: Exception) {
                    Log.d("tag","Error: ${e.localizedMessage}")
                }
            }
        }
        return fileProvider

    }
}