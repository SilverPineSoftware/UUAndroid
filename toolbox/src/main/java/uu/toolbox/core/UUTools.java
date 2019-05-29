package uu.toolbox.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uu.toolbox.logging.UULog;

/**
 * UUTools - a handy bag o' useful methods
 * 
 */
public final class UUTools
{
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Constants
	///////////////////////////////////////////////////////////////////////////////////////////////
	private static final String LOG_TAG = UUTools.class.getName();

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Public Static Methods
	///////////////////////////////////////////////////////////////////////////////////////////////

	public static final boolean isIntentAvailable(final Context context, final String action) 
	{
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}

	public static final Bitmap loadAndScaleBitmap(final String filePath, final int targetWidth, final int targetHeight)
	{
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		int scaleFactor = 1;
		if ((targetWidth > 0) || (targetHeight > 0)) 
		{
			scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight);	
		}

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);
		//return bitmap;
		Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, targetWidth, targetHeight);
        return croppedBitmap;
	}
	
	public static final Bitmap scaleAndCropBitmap(final Context context, final Bitmap sourceBitmap, final int targetDimensionId)
	{
		return scaleAndCropBitmap(context, sourceBitmap, targetDimensionId, targetDimensionId);
	}
	
	public static final Bitmap scaleAndCropBitmap(final Context context, final Bitmap sourceBitmap, final int targetWidthDimensionId, final int targetHeightDimensionId)
	{
		if (context != null)
		{
			int targetWidth = context.getResources().getDimensionPixelSize(targetWidthDimensionId);
			int targetHeight = context.getResources().getDimensionPixelSize(targetHeightDimensionId);
			return scaleAndCropBitmapByPixelDimensions(context, sourceBitmap, targetWidth, targetHeight);
		}
		else
		{
			return null;
		}
	}
	
	public static final Bitmap scaleAndCropBitmapByPixelDimensions(final Context context, final Bitmap sourceBitmap, final int targetWidth, final int targetHeight)
	{
		try
		{
			if (context != null && sourceBitmap != null)
			{
				int srcWidth = sourceBitmap.getWidth();
				int srcHeight = sourceBitmap.getHeight();
				
				float targetAspectRatio = (float)targetWidth / (float)targetHeight;
				float srcAspectRatio = (float)srcWidth / (float)srcHeight;
				
				int scaleWidth = targetWidth;
				int scaleHeight = targetHeight;
				int x = 0;
				int y = 0;
				
				
				if (srcAspectRatio < targetAspectRatio)
				{
					scaleWidth = targetWidth;
					scaleHeight = (int)((float)(1 / srcAspectRatio) * (float)scaleWidth);
					x = 0;
					y = (scaleHeight - targetHeight) / 2;
				}
				else
				{
					scaleHeight = targetHeight;
					scaleWidth = (int)((float)srcAspectRatio * (float)scaleHeight);
					x = (scaleWidth - targetWidth) / 2;
					y = 0;
				}
				
				//Log.d(LOG_TAG, "         srcWidth: " + srcWidth);
				//Log.d(LOG_TAG, "        srcHeight: " + srcHeight);
				//Log.d(LOG_TAG, "   srcAspectRatio: " + srcAspectRatio);
				//Log.d(LOG_TAG, "      targetWidth: " + targetWidth);
				//Log.d(LOG_TAG, "     targetHeight: " + targetHeight);
				//Log.d(LOG_TAG, "targetAspectRatio: " + targetAspectRatio);
				//Log.d(LOG_TAG, "       scaleWidth: " + scaleWidth);
				//Log.d(LOG_TAG, "      scaleHeight: " + scaleHeight);
				//Log.d(LOG_TAG, "            cropX: " + x);
				//Log.d(LOG_TAG, "            cropY: " + y);
				
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, scaleWidth, scaleHeight, true);
				Bitmap croppedBitmap = Bitmap.createBitmap(scaledBitmap, x, y, targetWidth, targetHeight);
		        return croppedBitmap;
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error scaling and cropping bitmap", ex);
		}
		
		return null;
	}
	
	public static final Bitmap loadScaleAndCropBitmap(final Context context, final String filePath, final int targetWidthDimensionId, final int targetHeightDimensionId)
	{
		if (context != null)
		{
			int targetWidth = context.getResources().getDimensionPixelSize(targetWidthDimensionId);
			int targetHeight = context.getResources().getDimensionPixelSize(targetHeightDimensionId);
			return loadScaleAndCropBitmapByPixelDimensions(context, filePath, targetWidth, targetHeight);
		}
		else
		{
			return null;
		}
	}
	
	public static final Bitmap loadScaleAndCropBitmapByPixelDimensions(final Context context, final String filePath, final int targetWidth, final int targetHeight)
	{
		try
		{
			if (context != null && filePath != null)
			{
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filePath, bmOptions);
				
				int srcWidth = bmOptions.outWidth;
				int srcHeight = bmOptions.outHeight;
				//Log.d("Foo", "srcWidth: " + srcWidth + ", srcHeight: " + srcHeight);
				int scaleFactor = (int)Math.min((float)srcWidth / (float)targetWidth, (float)srcHeight / (float)targetHeight);
				
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scaleFactor;
				bmOptions.inPurgeable = true;

				Bitmap sourceBitmap = BitmapFactory.decodeFile(filePath, bmOptions);
				return scaleAndCropBitmapByPixelDimensions(context, sourceBitmap, targetWidth, targetHeight);
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error loading, scaling and cropping bitmap", ex);
		}
		
		return null;
	}
	
	public static final String getAppVersion(final Context context)
    {
        try 
        { 
            PackageManager manager = context.getPackageManager(); 
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0); 
            return info.versionName; 
        } 
        catch(Exception ex) 
        { 
        	Log.e(LOG_TAG, "Error getting app version", ex);
        }
        
        return "0.0";
    }
	
	public static final File createPrivateSubfolder(final Context context, final String subfolder)
	{
		String path = context.getFilesDir() + "//" + subfolder;
		File folder = new File(path);
		
		if (!folder.exists())
		{
			boolean ok = folder.mkdirs();
			if (!ok)
			{
				Log.w(LOG_TAG, "mkdirs() returned false for path: " + path);
			}
		}
		
		return folder;
	}
	
	public static final File saveBitmap(final Context context, final Bitmap outputImage, final String subfolder, final String fileName, final Bitmap.CompressFormat format)
	{
        File storagePath = createPrivateSubfolder(context, subfolder);
        
        File imageFile = null;
        FileOutputStream outputStream = null;
        
        try 
        { 
        	imageFile = new File(storagePath, fileName);
        	outputStream = new FileOutputStream(imageFile); 
            outputImage.compress(format, 100, outputStream); 
            outputStream.flush();
        } 
        catch (Exception ex) 
        { 
        	Log.e(LOG_TAG, "saveBitmap", ex); 
        	imageFile = null;
        }       
        finally
        {
        	closeStream(outputStream);
        }
        
        return imageFile;
    }
	
	public static final void closeStream(final OutputStream stream)
	{
		try
		{
			if (stream != null)
			{
				stream.close();
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "closeStream", ex);
		}
	}
	
	public static final void closeStream(final InputStream stream)
	{
		try
		{
			if (stream != null)
			{
				stream.close();
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "closeStream", ex);
		}
	}
	
	public static final void closeStatement(final SQLiteStatement statement)
	{
		try
		{
			if (statement != null)
			{
				statement.close();
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "closeStatement", ex);
		}
	}
	
	public static int tryParseInt(final String integerString, final int defaultValue)
	{
		try
		{
			return Integer.valueOf(integerString);
		}
		catch (Exception ex)
		{
			return defaultValue;
		}
	}
	
	public static final boolean isExternalStorageWritable()
	{
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	/*
	public static final void addToDevicePhotoGallery(final Context context, final Uri fileUri) 
	{
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    mediaScanIntent.setData(fileUri);
	    context.sendBroadcast(mediaScanIntent);
	}*/
	
	public static final boolean isUuidInList(final String uuid, final UUID[] list)
	{
		if (uuid != null && list != null)
		{
			for (UUID o : list)
			{
				if (uuid.equals(o.toString()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	public static final int convertDipToPixels(final Context context, final int dpValue)
	{
		int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
		return pixels;
	}
	
	public static final Bitmap scaleBitmapToHeight(final Context context, final Bitmap sourceBitmap, final int targetDimensionId)
	{
		try
		{
			if (context != null && sourceBitmap != null)
			{
				int thumbSize = context.getResources().getDimensionPixelSize(targetDimensionId);
				int srcWidth = sourceBitmap.getWidth();
				int srcHeight = sourceBitmap.getHeight();
				float aspectRatio = (float)srcWidth / (float) srcHeight;
				int destHeight = thumbSize;
				int destWidth = (int)((float)thumbSize * aspectRatio);
				
		        Bitmap scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, destWidth, destHeight, true);
		        return scaledBitmap;
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error scaling bitmap", ex);
		}
		
		return null;
	}
	
	public static final void rotateJpegIfNeeded(final String filePath)
	{
		FileOutputStream fos = null;
		
		try
		{
			Bitmap b = BitmapFactory.decodeFile(filePath);

			int width = b.getWidth();
			int height = b.getHeight();
			Matrix matrix = new Matrix();

			ExifInterface exif = new ExifInterface(filePath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			switch (orientation)
			{
				case ExifInterface.ORIENTATION_ROTATE_90:
					matrix.postRotate(90);
					break;
					
				case ExifInterface.ORIENTATION_ROTATE_180:
					matrix.postRotate(180);
					break;
					
				case ExifInterface.ORIENTATION_ROTATE_270:
					matrix.postRotate(270);
					break;
			}
 
			fos = new FileOutputStream(new File(filePath));
			Bitmap resizedBitmap = Bitmap.createBitmap(b, 0, 0, width, height, matrix, true);
			resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL));
			exif.saveAttributes();
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error rotating jpeg file", ex);
		}
	}
	
	public static final void writeExifLocation(final String fileName, final Location location)
	{
		try
		{
			if (fileName != null && location != null)
			{
				ExifInterface ef = new ExifInterface(fileName);
				
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				
				ef.setAttribute(ExifInterface.TAG_GPS_LATITUDE, decimalToDegreesHoursMinutes(lat));
				ef.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, decimalToDegreesHoursMinutes(lng));
				ef.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(lat));
				ef.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(lng));
				ef.saveAttributes();
				
				// DEBUG
				Location check = readExifLocation(fileName);
				if (check != null)
				{
					if (check.getLatitude() == lat && check.getLongitude() == lng)
					{
						Log.d(LOG_TAG, "Exif Location written succesfully");
					}
					else
					{
						Log.d(LOG_TAG, "Exif Location written but does not match");
					}
				}
				else
				{
					Log.d(LOG_TAG, "Exif Location was not written");
				}
				// END DEBUG
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error writing EXIF location info", ex);
		}
	}
	
	public static final void writeExifTags(final String fileName, final Map<String, String> tags)
	{
		try
		{
			if (fileName != null && tags != null)
			{
				ExifInterface ef = new ExifInterface(fileName);
				
				Set<String> keys = tags.keySet();
				for (String key : keys)
				{
					String val = tags.get(key);
					ef.setAttribute(key, val);
				}
				
				ef.saveAttributes();
				
				// DEBUG
				ef = new ExifInterface(fileName);
				for (String key : keys)
				{
					String val = tags.get(key);
					String check = ef.getAttribute(key);
					if (!val.equals(check))
					{
						Log.d(LOG_TAG, "Failed to write Exif Tag: " + key + " with value: " + val);
					}
				}
				// END DEBUG
				
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error writing EXIF location info", ex);
		}
	}
	
	public static final Location readExifLocation(final String fileName)
	{
		Location location = null;
		
		try
		{
			if (fileName != null)
			{
				ExifInterface ef = new ExifInterface(fileName);
				
				float[] loc = new float[2];
				if (ef.getLatLong(loc))
				{
					location = new android.location.Location("ExifInterface");
					location.setLatitude(loc[0]);
					location.setLongitude(loc[1]);
				}
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error writing EXIF location info", ex);
		}
		
		return location;
	}
	
	public static final String[] EXIF_TAGS = new String[]
	{
		ExifInterface.TAG_APERTURE, //	Type is String.
		ExifInterface.TAG_DATETIME, //	Type is String.
		ExifInterface.TAG_EXPOSURE_TIME, //	Type is String.
		ExifInterface.TAG_FLASH, //	Type is int.
		ExifInterface.TAG_FOCAL_LENGTH, //	Type is rational.
		ExifInterface.TAG_GPS_ALTITUDE, //	The altitude (in meters) based on the reference in TAG_GPS_ALTITUDE_REF.
		ExifInterface.TAG_GPS_ALTITUDE_REF, //	0 if the altitude is above sea level.
		ExifInterface.TAG_GPS_DATESTAMP, //	Type is String.
		ExifInterface.TAG_GPS_LATITUDE, //	String.
		ExifInterface.TAG_GPS_LATITUDE_REF, //	Type is String.
		ExifInterface.TAG_GPS_LONGITUDE, //	String.
		ExifInterface.TAG_GPS_LONGITUDE_REF, //	Type is String.
		ExifInterface.TAG_GPS_PROCESSING_METHOD, //	Type is String.
		ExifInterface.TAG_GPS_TIMESTAMP, //	Type is String.
		ExifInterface.TAG_IMAGE_LENGTH, //	Type is int.
		ExifInterface.TAG_IMAGE_WIDTH, //	Type is int.
		ExifInterface.TAG_ISO, //	Type is String.
		ExifInterface.TAG_MAKE, //	Type is String.
		ExifInterface.TAG_MODEL, //	Type is String.
		ExifInterface.TAG_ORIENTATION, //	Type is int.
		ExifInterface.TAG_WHITE_BALANCE, //	Type is int.

	};
	
	public static final void logExifInfo(final String fileName)
	{
		try
		{
			if (fileName != null)
			{
				ExifInterface ef = new ExifInterface(fileName);
				
				Log.d(LOG_TAG, "Exif info for file: " + fileName);
				for (String key : EXIF_TAGS)
				{
					String val = ef.getAttribute(key);
					Log.d(LOG_TAG, "Exif tag " + key + ": " + val);
				}
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error logging EXIF", ex);
		}
	}
	
	/**
	 * Clamp's a floating point number within a min/max range
	 * @param value the number to clamp
	 * @param min the min range value
	 * @param max the max range value
	 * @return a value gauraunteed to be within min-max
	 */
	public static final float clamp(final float value, final float min, final float max)
	{
		if (value < min)
		{
			return min;
		}
		
		if (value > max)
		{
			return max;
		}
		
		return value;
	}
	
	/**
	 * Clamp's a Double point number within a min/max range
	 * @param value the number to clamp
	 * @param min the min range value
	 * @param max the max range value
	 * @return a value gauraunteed to be within min-max
	 */
	public static final double clamp(final double value, final double min, final double max)
	{
		if (value < min)
		{
			return min;
		}
		
		if (value > max)
		{
			return max;
		}
		
		return value;
	}
	
	/**
	 * Clamp's a long integer number within a min/max range
	 * @param value the number to clamp
	 * @param min the min range value
	 * @param max the max range value
	 * @return a value gauraunteed to be within min-max
	 */
	public static final long clamp(final long value, final long min, final long max)
	{
		if (value < min)
		{
			return min;
		}
		
		if (value > max)
		{
			return max;
		}
		
		return value;
	}
	
	/**
	 * Clamp's a integer number within a min/max range
	 * @param value the number to clamp
	 * @param min the min range value
	 * @param max the max range value
	 * @return a value gauraunteed to be within min-max
	 */
	public static final int clamp(final int value, final int min, final int max)
	{
		if (value < min)
		{
			return min;
		}
		
		if (value > max)
		{
			return max;
		}
		
		return value;
	}
	
	public static final Bitmap maskBitmapWithRoundedCorners(final Bitmap bitmap, final float cornerRadius)
	{    
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		// We have to make sure our rounded corners have an alpha channel in most cases
		Bitmap rounder = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(rounder);    

		// We're going to apply this paint eventually using a porter-duff xfer mode.
		// This will allow us to only overwrite certain pixels. RED is arbitrary. This
		// could be any color that was fully opaque (alpha = 255)
		Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		xferPaint.setColor(Color.RED);

		// We're just reusing xferPaint to paint a normal looking rounded box, the 20.f
		// is the amount we're rounding by.
		canvas.drawRoundRect(new RectF(0, 0, w, h), cornerRadius, cornerRadius, xferPaint);     

		// Now we apply the 'magic sauce' to the paint  
		xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		
		
		Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas resultCanvas = new Canvas(result);
		resultCanvas.drawBitmap(bitmap, 0, 0, null);
		resultCanvas.drawBitmap(rounder, 0, 0, xferPaint);
		
		return result;
	}
	
	public static final boolean doesFileExist(final String filePath)
	{
		File f = new File(filePath);
		return f.exists();
	}
	
	/**
	 * Safely deletes a file
	 * 
	 * @param filePath the file to delete
	 */
	public static void deleteFile(final String filePath)
	{
		try
		{
			if (filePath != null)
			{
				File f = new File(filePath);
				if (f.exists())
				{
					f.delete();
				}
			}
		}
		catch (Exception ex)
		{
			Log.d(LOG_TAG, "deleteFile", ex);
		}
	}

	public static void copyFile(final String source, final String dest)
    {
        UULog.debug(UUTools.class, "copyFile", "source: " + source + ", dest: " + dest);

		InputStream in = null;
		OutputStream out = null;

		try
        {
            File srcFile = new File(source);

            String fileName = srcFile.getName();

            File destFile = new File(dest);
			File destFolder = new File (destFile.getParent());

			if (!destFolder.exists())
			{
				destFolder.mkdirs();
			}

			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);

			byte[] buffer = new byte[1024];
			int read = 0;

			while (read != -1)
            {
                read = in.read(buffer);
				out.write(buffer, 0, read);
			}
		}
		catch (Exception ex)
        {
            UULog.debug(UUTools.class, "copyFile", ex);
		}
        finally
        {
            closeStream(in);
            closeStream(out);
        }
	}
	
	public static final byte[] convertToJpeg(final File file)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] result = null;
		
		try
		{
			if (file != null)
			{
				Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
				if (bmp.compress(CompressFormat.JPEG, 100, bos))
				{
					result = bos.toByteArray();
				}
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error converting to jpeg", ex);
			result = null;
		}
		finally
		{
			UUTools.closeStream(bos);
		}
		
		return result;
	}
	
	public static final byte[] convertToPNG(final Context context, final File file)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] result = null;
		
		try
		{
			if (file != null)
			{
				Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
				bmp = UUTools.scaleAndCropBitmapByPixelDimensions(context, bmp, 128, 128);
				if (bmp.compress(CompressFormat.PNG, 100, bos))
				{
					result = bos.toByteArray();
				}
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error converting to png", ex);
			result = null;
		}
		finally
		{
			UUTools.closeStream(bos);
		}
		
		return result;
	}
	
	public static final byte[] readEntireFile(final File file)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FileInputStream fis = null;
		byte[] result = null;
		
		try
		{
			if (file != null)
			{
				fis = new FileInputStream(file);
				
				byte[] buffer = new byte[10240];
				
				int bytesRead = 0;
				
				while (true)
				{
					bytesRead = fis.read(buffer, 0, buffer.length);
					if (bytesRead == -1)
						break;
					
					bos.write(buffer, 0, bytesRead);
				}
				
				result = bos.toByteArray();
			}
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Error reading whole file", ex);
			result = null;
		}
		finally
		{
			UUTools.closeStream(fis);
			UUTools.closeStream(bos);
		}
		
		return result;
	}
	
	public static final int[] toIntArray(final ArrayList<Integer> objectArray)
	{
		if (objectArray == null)
		{
			return null;
		}
		
		int[] nativeArray = new int[objectArray.size()];
		int i = 0;
		for (Integer val : objectArray)
		{
			nativeArray[i++] = val.intValue();
		}
		
		return nativeArray;
	}

    public static final boolean isValidEmailAddress(final String emailAddress)
    {
        if (emailAddress == null || emailAddress.length() <= 0)
        {
            return false;
        }

        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Private Static Methods
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final String decimalToDegreesHoursMinutes(final double input)
	{
		int degrees = (int)Math.floor(input);
	    int hours = (int)Math.floor((input - degrees) * 60);
	    double minutes = (input - ((double)degrees+((double)hours/60))) * 3600000;
		return String.format(Locale.US, "%d/1,%d/1,%f/1000", degrees, hours, minutes);
	}
	
	private static final String latitudeRef(final double latitude)
	{
		if (latitude > 0)
		{
			return "N";
		}
		else
		{
			return "S";
		}
	}
	
	private static final String longitudeRef(final double longitude)
	{
		if (longitude > 0)
		{
			return "E";
		}
		else
		{
			return "W";
		}
	}
}
