package inspirational.designs.charpaper;

import java.io.InputStream;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class CharPaperService extends WallpaperService
{

	@Override
	public Engine onCreateEngine()
	{
		return new CharPaperEngine();
	}

	public class CharPaperEngine extends Engine
	{
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable()
		{
			public void run()
			{
				draw();
			}
		};

		private CharPaperRenderer mRenderer;
		private int width;
		private int height;
		boolean visible = true;

		public CharPaperEngine()
		{
			mRenderer = new CharPaperRenderer(this);
			handler.post(drawRunner);
		}
		
		public InputStream getRes(int id)
		{
			return getResources().openRawResource(id);
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
		  this.visible = visible;
		  if (visible)
		    handler.post(drawRunner);
		  else
		    handler.removeCallbacks(drawRunner);
		}
    
	    @Override
	    public void onSurfaceDestroyed(SurfaceHolder holder)
	    {
	      super.onSurfaceDestroyed(holder);
	      this.visible = false;
	      handler.removeCallbacks(drawRunner);
	    }
	
	    @Override
	    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
	    {
	      this.width = width;
	      this.height = height;
	      super.onSurfaceChanged(holder, format, width, height);
	    }
	    
	    public boolean isVisible()
	    {
	    	return visible;
	    }
	
	    private void draw()
	    {
	    	if (isVisible())
	    	{
				SurfaceHolder holder = getSurfaceHolder();
				Canvas canvas = null;
				try
				{
					canvas = holder.lockCanvas();
					if (canvas != null)
						mRenderer.draw(canvas, width, height);
				}
				finally
				{
					if (canvas != null)
						holder.unlockCanvasAndPost(canvas);
				}
	    	}
			//Come back and check how this is working
			//this reschedules a paint operation at the given interval.
			handler.removeCallbacks(drawRunner);
			if (isVisible())
				handler.postDelayed(drawRunner, 50);
	    }
	    
	    @Override
	    public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) 
	    {
	    	if (action.contains("android.wallpaper.tap")) 
	    		mRenderer.handleTap(x, y, z);

	    	return null;
	    }
	    
	    @Override
	    public void onTouchEvent(MotionEvent event) {
	    	if (event.getAction() == MotionEvent.ACTION_DOWN)
	    		mRenderer.handleDown(event.getX(), event.getY());
	    	if (event.getAction() == MotionEvent.ACTION_MOVE)
	    		mRenderer.handleMove(event.getX(), event.getY());
	    	if (event.getAction() == MotionEvent.ACTION_UP)
	    		mRenderer.handleUp(event.getX(), event.getY());
	    }

	  }

}
