package gov.nasa.gsfc.irc.library.gui.vis.plot;

import gov.nasa.gsfc.commons.numerics.formats.SciDecimalFormat;
import gov.nasa.gsfc.commons.numerics.formats.TimeFormat;
import gov.nasa.gsfc.commons.numerics.formats.ValueFormat;
import gov.nasa.gsfc.commons.types.queues.FifoQueue;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisScale;
import gov.nasa.gsfc.irc.gui.vis.axis.EvenTickFactory;
import gov.nasa.gsfc.irc.gui.vis.axis.Tick;
import gov.nasa.gsfc.irc.gui.vis.axis.TickFactory;
import gov.nasa.gsfc.irc.library.gui.vis.axis.XAxisRenderer;
import gov.nasa.gsfc.irc.library.gui.vis.axis.YAxisRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;

import org.jscience.physics.units.SI;
import org.jscience.physics.units.Unit;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.GLUT;

public class OpenGlPlot implements GLEventListener, MouseListener,
        MouseMotionListener, MouseWheelListener
{

    public static final char AXIS_TOGGLE_CHAR = 'a';

	public boolean fDrawAllAxes = true;
	public float fMaxX;
    
    public boolean fUsingMillis = true;

	public class XyzPlotKeyListener implements KeyListener
	{

		private static final char LINE_WIDTH_UP_CHAR = '>';
		private static final char LINE_WIDTH_DOWN_CHAR = '<';
		private static final char LABEL_TOGGLE_CHAR = 'l';
        private static final char SNAPSHOT_MODE_TOGGLE_CHAR = 's';
		public void keyTyped(KeyEvent e)
		{
			// TODO Auto-generated method stub

		}

		public void keyPressed(KeyEvent e)
		{
	        int id = e.getID();
	        if (id == KeyEvent.KEY_PRESSED) 
	        {
	            char c = e.getKeyChar();
	            if (c == AXIS_TOGGLE_CHAR)
	            {
	            	fDrawAllAxes = !fDrawAllAxes ;
	            }
	            else if (c == LINE_WIDTH_UP_CHAR)
	            {
	            	fLineWidth = 1.1f * fLineWidth;
	            	System.out.println("Line width = " + fLineWidth);
	            }
	            else if (c == LINE_WIDTH_DOWN_CHAR)
	            {
	            	fLineWidth = .9f * fLineWidth;
	            	System.out.println("Line width = " + fLineWidth);	            	
	            }	     
	            else if (c == LABEL_TOGGLE_CHAR)
	            {
	            	fDrawDataLabels = !fDrawDataLabels;	            	
	            }	 	  
                else if (c == SNAPSHOT_MODE_TOGGLE_CHAR)
                {
                    fSnapshotMode = !fSnapshotMode;                 
                } 
	        }

		}

		public void keyReleased(KeyEvent e)
		{
			// TODO Auto-generated method stub

		}

	}

	private static final float ORIGINAL_XANGLE = 25.f;

    private static final float ORIGINAL_YANGLE = -25f;

    private static final float MAX_SCALE = 1000;

    private static final float MIN_SCALE = 1e-3f;

    private static final float ORIGINAL_SCALE = .02f;

    private static final float SCALE_FACTOR = .2f;

    private static final float SPACING_INCREMENT = 25f;

    private float fRotX = 0.0f, fRotY = 0.0f, fRotZ = 0.0f;

//    private int[] fDisplayListOlder;
//    private int[] fDisplayListNewer;    


    private int prevMouseX, prevMouseY;

    private boolean fMouseRButtonDown = false;
    
    private FifoQueue fPlotDataQueue = new FifoQueue();

    private GLUT fGlut = new GLUT();
    private float  fScale = ORIGINAL_SCALE;
    private float  fTranX = -10f;    
    private float  fTranY = -10f;
    private float  fTranZ = -40f;      
    
    private float  fSlideZ = 0f;         

    private float[] fMatrix = new float[16];
    private float[] fMatrixCopyOfOrig;    

    private boolean fMouseMButtonDown;

    private final static float ORIGINAL_SPACING = 100f;

    private static final int FONT_XAXIS = GLUT.BITMAP_HELVETICA_18;
    private static final int FONT_YAXIS = GLUT.BITMAP_HELVETICA_18;    
    
    private float fTraceSpacing = ORIGINAL_SPACING;

    private XAxisOpenGlRenderer fXAxisRenderer;

    private XyPlotData fDataNewer;
    private XyPlotData fDataOlder;    

    private double fTimeSlidingXTran;

    private float fClipPlaneZ;

    private YAxisOpenGlRenderer fYAxisRenderer;

    private boolean fMouseLButtonDown;

    private float fYAngle = ORIGINAL_YANGLE;

    private float fXAngle = ORIGINAL_XANGLE;

	private float fLineWidth = 1f;

	private boolean fDrawDataLabels;

    private boolean fSnapshotMode = true;

    private double fTimeOfLastUpdate;

    private boolean fHaveTwoDataChunks;

    private void create(String title)
    {
        Frame frame = new Frame(title);
        GLCanvas canvas = new GLCanvas();

        canvas.addGLEventListener(this);
        canvas.addKeyListener(new XyzPlotKeyListener());
        frame.add(canvas);
        frame.setSize(600, 400);
        final FPSAnimator animator = new FPSAnimator(canvas, 60);
//        final Animator animator = new Animator(canvas);        
        frame.addMouseWheelListener(this);
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        animator.stop();
                        // System.exit(0);
                    }
                }).start();
            }
        });
        frame.show();
        animator.start();
    }

    public OpenGlPlot(String title)
    {
        create(title);
    }

    public void init(GLAutoDrawable drawable)
    {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL gl = drawable.getGL();

        System.err.println("INIT GL IS: " + gl.getClass().getName());

        gl.setSwapInterval(1);

        float pos[] = { 5.0f, 5.0f, 10.0f, 0.0f };
//        float pos2[] = { -5.0f, 5.0f, 10.0f, 0.0f };        
        float pos2[] = { 0f, 5.0f, 10.0f, 0.0f };                
        float red[] = { 0.8f, 0.1f, 0.0f, 1.0f };
        float green[] = { 0.0f, 0.8f, 0.2f, 1.0f };
        float blue[] = { 0.2f, 0.2f, 1.0f, 1.0f };

//        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, pos2, 0);
//        gl.glEnable(GL.GL_CULL_FACE);
        
//        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//        gl.glHint( GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);

        // Don't need lighting for just lines ... 
//        gl.glEnable(GL.GL_LIGHTING);
//        gl.glEnable(GL.GL_LIGHT0);
        
        
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glEnable(GL.GL_NORMALIZE);

        gl.glPushMatrix();
        gl.glLoadIdentity();
        
        // Get initial transform matrix
        gl.glTranslatef(fTranX, fTranY, fTranZ);
        gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, fMatrix, 0);
        
        // Save a copy for resetting
        fMatrixCopyOfOrig = (float []) fMatrix.clone();
        gl.glPopMatrix();
        
        drawable.addMouseListener(this);
        drawable.addMouseMotionListener(this);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height)
    {
        GL gl = drawable.getGL();

        float h = (float) height / (float) width;

        gl.glMatrixMode(GL.GL_PROJECTION);

        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
        gl.glLoadIdentity();
        gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 600.0f);
//        gl.glFrustum(-1.0f, 1.0f, -h, h, -100.0f, 100.0f);        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        
    }

    public void display(GLAutoDrawable drawable)
        {
        
            float xTran = -3.5f;
            float yTran = -2f;            
            float zTran = -40f;    
            
            GL gl = drawable.getGL();
            
            checkAndProcessAnyNewData(gl);
            
//            if (newData == true)
//            {
//                fTimeSlidingXTran = 0f;
//            }
//            else {
//                fTimeSlidingXTran -= 1f;
            
//            }
            fTimeSlidingXTran -= 1f;            
            
            gl.glClearColor(1f,1f,1f,0f);
            
            if ((drawable instanceof GLJPanel)
                    && !((GLJPanel) drawable).isOpaque()
                    && ((GLJPanel) drawable)
                            .shouldPreserveColorBufferIfTranslucent())
            {
                gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
            }
            else
            {
                gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            }
        
            gl.glPushMatrix();
            gl.glLoadIdentity();
            


            
            // Create a clip plane on the
            // "left" side of the graphs (older, smaller values
            // in the basis buffer)
            //

            gl.glPushMatrix();

            gl.glTranslatef(xTran, yTran, zTran);  
            gl.glRotatef( fXAngle, 1.0f, 0.0f, 0.0f);  
            gl.glRotatef(90f + fYAngle, 0.0f, 1.0f, 0.0f); 
            gl.glClipPlane(GL.GL_CLIP_PLANE0, new double[] {0,0,1,0}, 0);

            gl.glPopMatrix();
            
            
            
            
            
            gl.glTranslatef(xTran, yTran, zTran);          
            
            gl.glRotatef(fXAngle, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(fYAngle, 0.0f, 1.0f, 0.0f);
//            gl.glRotatef(0f, 0.0f, 0.0f, 1.0f);
            
            // user can translated "back and forth" through plots
            gl.glTranslatef(0f, 0f, fSlideZ);
            
            gl.glScalef(fScale, fScale, fScale);
            
            gl.glPushMatrix();            

            if (fDataOlder != null)
            {
                
                float normalizedScale = ORIGINAL_SCALE/fScale;
                
                //
                // Render traces and X axes
                //
                gl.glEnable(GL.GL_CLIP_PLANE0);              
                
                
                gl.glPushMatrix();                    
                gl.glTranslated(fTimeSlidingXTran, 0, 0);                    
                
                for (int i = 0; i < fDataOlder.fDisplayListIds.length; i++)
                {
                    gl.glCallList(fDataOlder.fDisplayListIds[i]);
                    gl.glTranslatef(0f, 0f, -fTraceSpacing );
                }
                gl.glPopMatrix(); 

                if (fSnapshotMode == false && fHaveTwoDataChunks == true)
                {
                    gl.glPushMatrix();                    
                    gl.glTranslated(fTimeSlidingXTran + fDataOlder.getWidth(), 0, 0);                    
//                    gl.glTranslated(fTimeSlidingXTran + 500, 0, 0);                                        
                    
                    for (int i = 0; i < fDataNewer.fDisplayListIds.length; i++)
                    {
                        gl.glCallList(fDataNewer.fDisplayListIds[i]);
                        gl.glTranslatef(0f, 0f, -fTraceSpacing );
                    }
                    gl.glPopMatrix();                     
                }
                
                
                //
                // Render databuffer name and Y Axis
                //
                gl.glDisable(GL.GL_CLIP_PLANE0);  
                
                
                if (fDataOlder != null)
                {
                    gl.glPushMatrix();
                    for (int trace = 0; trace < fDataOlder.fDisplayListIds.length; trace++)
                    {
                        Color c =  fDataOlder.fTraces[trace].fColor;
                        float[] color = new float[] { c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f };                        
                        gl.glColor3fv(color, 0);                        
//                        GlutTextUtil.renderSpacedBitmapString(gl, -250f * (ORIGINAL_SCALE/fScale), 0f, .5f * (ORIGINAL_SCALE/(fScale * 8f)),
                                if (fDrawDataLabels == true)
								{
									GlutTextUtil.renderSpacedBitmapString2(gl,
											fMaxX + 50f * normalizedScale, 0f, 2f,
											normalizedScale,
											GLUT.BITMAP_HELVETICA_18,
											fDataOlder.fTraces[trace].fName);
									
								}                  
                        
                        if (fYAxisRenderer != null)
                        {
                        	if (fDrawAllAxes == false && trace == 0 || fDrawAllAxes == true)
                            {                        	
                        		fYAxisRenderer.draw(fDataOlder.fRect2D, normalizedScale, trace==0);
                            }
                        }
                        
                        gl.glTranslatef(0f, 0f, -fTraceSpacing);
                    }
                    gl.glPopMatrix();
                }
                
            }
            gl.glPopMatrix();                
            
            gl.glPopMatrix();
        }



    private boolean checkAndProcessAnyNewData(GL gl)
    {
        boolean newData = false;
        double currentTime = System.currentTimeMillis() / 1000d;
        
        if (fPlotDataQueue.size() > 0)
        {
            if (fSnapshotMode  == true)
            {
                // for now just keep up with the latest data
                fDataOlder = (XyPlotData) fPlotDataQueue.removeLast();
                fPlotDataQueue.clear();
                buildDisplayLists(gl, fDataOlder);
                fDataNewer = null;
//                fTimeSlidingXTran = 0;
            }
            else
            {
                // If data is piling up, grab the last two data chunks
                // and reset the sliding XTran
                if (fPlotDataQueue.size() > 1)
                {
                    fDataNewer = (XyPlotData) fPlotDataQueue.removeLast();
                    fDataOlder = (XyPlotData) fPlotDataQueue.removeLast();
                    buildDisplayLists(gl, fDataOlder);
                    buildDisplayLists(gl, fDataNewer);                    
                    fTimeSlidingXTran = 0;
                    fHaveTwoDataChunks = true;
                    System.out.println("GOT BEHIND");
//                    System.exit(-1);
                }
                else
                {
                    if (fDataOlder == null)
                    {
                        // First time through set fDataOlder so that something is drawn
                        fDataOlder = (XyPlotData) fPlotDataQueue.remove();
                        buildDisplayLists(gl, fDataOlder);    
                        
                        fTimeSlidingXTran = 0;
                        fHaveTwoDataChunks = false;                        
                    }
                    else
                    {
                        // There's one data chunk, so insert the new data
                        // into the mix and adjust the TimeSlidingXTran to 
                        // be relative to the new data (and adjust it for the
                        // time that has passed since the last update)
//                        fTimeSlidingXTran = fTimeSlidingXTran + fDataOlder.getWidth() - 
//                            ((currentTime - fTimeOfLastUpdate) * fDataNewer.fPixelsPerSecond);
                        
                        fTimeSlidingXTran = fTimeSlidingXTran - 
                        ((currentTime - fTimeOfLastUpdate) * fDataOlder.getPixelsPerSecond());
                        
                        if (fDataNewer != null)
                        {
                            fDataOlder = new XyPlotData(fDataNewer);
                        }
                        fDataNewer = (XyPlotData) fPlotDataQueue.remove();
                        buildDisplayLists(gl, fDataNewer);
                        
                        fHaveTwoDataChunks = true;                        
                    }
                }
            }
        }
        else
        {
            if (fSnapshotMode == false && fHaveTwoDataChunks == true)
            {
                // No new data; just slide the data according to the
                // time that has passed
                if (fDataOlder != null)
                {
                    fTimeSlidingXTran = fTimeSlidingXTran
                            - ((currentTime - fTimeOfLastUpdate) * fDataOlder
                                    .getPixelsPerSecond());
                }
            }
        }
        
        fTimeSlidingXTran = 0;
//        System.out.println("XTran = " + fTimeSlidingXTran);
        fTimeOfLastUpdate = currentTime;
        
        return newData;
       
        
    }

    private void buildDisplayLists(GL gl, XyPlotData plotData)
    {
        
        // These allocations should be moved
        if (fXAxisRenderer == null)
        {
        	TimeFormat format = getTimeFormat(plotData.fBasisUnits);
            fXAxisRenderer = new XAxisOpenGlRenderer(
                    plotData.fXAxisModel, new EvenTickFactory(), gl, FONT_XAXIS, format);
        }
        if (fYAxisRenderer == null)
        {
            fYAxisRenderer = new YAxisOpenGlRenderer(
                    plotData.fYAxisModel, gl, FONT_YAXIS);
        }
        
        // Delete old display lists
        deleteDisplayLists(gl, plotData.fDisplayListIds);
   
        
        int[] displayListIds = new int[plotData.fTraces.length];        
        float normalizedScale = ORIGINAL_SCALE/fScale;        
        float height = (float) plotData.fRect2D.getHeight();
        for (int trace = 0; trace < plotData.fTraces.length; trace++)
        {
            XyPlotData.SingleTrace traceData = plotData.fTraces[trace];
            
            displayListIds[trace] = gl.glGenLists(1);
            gl.glNewList(displayListIds[trace], GL.GL_COMPILE);
            
            Color c =  traceData.fColor;
            float[] color = new float[] { c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f };
            gl.glColor3fv(color, 0);            
//            gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, color, 0);
//            gl.glMaterialfv(GL.GL_BACK, GL.GL_AMBIENT_AND_DIFFUSE, color, 0);            
            
            gl.glLineWidth(fLineWidth );
            gl.glBegin(GL.GL_LINE_STRIP);

            int numPoints = plotData.fXCoords.length;
            
            for (int coord = 0; coord < numPoints; coord++)
            {
                if (trace == 2  && coord == 0)
                {
                    System.out.println("TRACE 3 coord y(0)=" + traceData.fYCoords[coord]);
                }
                // We add 2 to y because in the 2d plot canvasTop = 2, canvasLeft = 2 in XyPlotRenderer
                gl.glVertex2f(plotData.fXCoords[coord] - 2, height - traceData.fYCoords[coord] + 2);
                if (coord == numPoints - 1 && trace == 0)
                {
                	fMaxX = plotData.fXCoords[coord] - 2;
                }
            }
            gl.glEnd();


            if (fXAxisRenderer != null)
            {
            	if (fDrawAllAxes == false && trace == 0 || fDrawAllAxes == true)
                {
            		fXAxisRenderer.draw(plotData.fRect2D, normalizedScale, trace == 0); 
                }
            } 
            
            gl.glEndList();      
        }
        
        plotData.fDisplayListIds = displayListIds;
    }

    private void deleteDisplayLists(GL gl, int[] displayListIds)
	{
    	if (displayListIds != null)
		{
			for (int i = 0; i < displayListIds.length; i++)
			{
				gl.glDeleteLists(i, 1);
			}
		}
		
		
	}

	private TimeFormat getTimeFormat(Unit unit)
    {
        TimeFormat format = new TimeFormat("mm:ss.S");
        if (unit.equals(SI.MILLI(SI.SECOND)))
        {
            format.setUnit("msec");
        }
        else
        {
            format.setUnit("s");
        }

        return format;
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged)
    {
    }

    // Methods required for the implementation of MouseListener
    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
        prevMouseX = e.getX();
        prevMouseY = e.getY();
        if ((e.getModifiers() & e.BUTTON3_MASK) != 0)
        {
            fMouseRButtonDown = true;
        }
        else if ((e.getModifiers() & e.BUTTON2_MASK) != 0)
        {
            fMouseMButtonDown = true;
            resetView();          
        }
        else if ((e.getModifiers() & e.BUTTON1_MASK) != 0)
        {
            fMouseLButtonDown = true;
        }        
    }

    public void mouseReleased(MouseEvent e)
    {
//        if ((e.getModifiers() & e.BUTTON3_MASK) != 0)
//        {
//            fMouseRButtonDown = false;
//            fMouseMButtonDown = false;            
//        }
//        eif ((e.getModifiers() & e.BUTTON3_MASK) != 0)
//        {
            fMouseRButtonDown = false;
            fMouseMButtonDown = false;            
            fMouseLButtonDown = false;            
//        }        
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    private void resetView()
    {
        fMatrix = (float []) fMatrixCopyOfOrig.clone();  
        fScale = ORIGINAL_SCALE;
        fTraceSpacing = ORIGINAL_SPACING;
        fXAngle = ORIGINAL_XANGLE;
        fYAngle = ORIGINAL_YANGLE;
        fSlideZ = 0f;
    }
    
    // Methods required for the implementation of MouseMotionListener
    public void mouseDragged(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        Dimension size = e.getComponent().getSize();

        float xdiff = ((float) (x - prevMouseX) / (float) size.width);
        float ydiff = ((float) (prevMouseY - y) / (float) size.height);
        
        if (fMouseRButtonDown == true)
        {
            float delta = y-prevMouseY;                
            fSlideZ += (delta * .1f);

        }
        else if (fMouseLButtonDown == true)
        {
            fXAngle += (360.0f * -ydiff);
            fYAngle += (360.0f * xdiff);            
        }
        else {
            fRotX = (360.0f * -ydiff);
            fRotY = (360.0f * xdiff);
        }
        prevMouseX = x;
        prevMouseY = y;


    }

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if ((e.getModifiers() & e.BUTTON3_MASK) != 0)
        {
            fTraceSpacing = fTraceSpacing + (e.getWheelRotation() * SPACING_INCREMENT);
//            fTraceSpacing = fTraceSpacing * (1 + (e.getWheelRotation() * SPACING_FACTOR));            
        }
        else
        {
            fScale = fScale * (1 + (e.getWheelRotation() * SCALE_FACTOR));
        }
    }

    public void receiveNewPlotData(XyPlotData plotData)
    {
    	System.out.println("rcvNewData " + Thread.currentThread().getName() + " -- " + plotData.fTraces.length + ", " + plotData.fTraces[0].fYCoords.length + ", snapshot=" + fSnapshotMode);
        
        
        fPlotDataQueue.add(plotData);
    }

    public void mouseMoved(MouseEvent e)
    {
    }

    /**
     *
     * 
     */
    public class XAxisOpenGlRenderer //extends XAxisRenderer
        {
    
    
            private GL fGL;
            private int fFont;
            private final static double GL_LABEL_MARGIN = 0;
            private GLUT sGlut = new GLUT();
            private AxisModel fAxisModel;
            private TickFactory fTickFactory;
            private int fTickLength;
            protected String fLocation = XAxisRenderer.BELOW;
            private TimeFormat fFormat;
            
            /**
             * @param model
             * @param format
             * @param gl
             * @param format 
             */
            public XAxisOpenGlRenderer(AxisModel model, TickFactory factory, GL gl,
                    int font, TimeFormat format)
            {
//                super(model, factory, formatter);
                fTickFactory = factory;
                fAxisModel = model;
                fGL = gl;
                fFont = font;
                fFormat = format;
                
            }

            /**
             * Draw the axis, tick marks, and tick mark labels for this axis.
             * 
             * @param g2d the graphics context on which to draw
             * @param rect the unclipped drawing area bounds for rendering. This
             *            rectangle is used for sizing the axis.
             * @param dataSet not used by this renderer.
             * @return the same rectangle instance received.
             */
            public Rectangle2D draw(Rectangle2D rect, float normalizedScale, boolean drawLabels)
            {
            	// Axis information
            	double min = fAxisModel.getViewMinimum();
            	double max = min + fAxisModel.getViewExtent();
            	AxisScale axisScale = fAxisModel.getAxisScale();
            	
            	// Drawing canvas bounds information
            	// Shift everything by (-2, -2) to align with (0,0)                
            	double canvasLeft = rect.getMinX() - 2;		
            	double canvasRight = rect.getMaxX() - 1 - 2;		
            	double canvasTop = rect.getMinY() - 2;
            
            	//  Canvas width and height
            	double canvasWidth = rect.getWidth() - 1;
            	

                fTickLength = 5;
                
                int labelHeight = 18;//??????? Can't find way to get gl font height
//                int labelWidth = Math.abs(labelMetrics.charWidth('0') * 8);
                int labelWidth = Math.abs(sGlut.glutBitmapWidth(fFont, '0') * 8);                
            	
            	// Set the maximum number of ticks that will fit given the canvas
            	// width.
//            	fAxisModel.setNumberOfTicks((int)(canvasWidth / labelWidth) + 1);
//                Tick[] ticks = updateTicks();
                Tick[] ticks = fTickFactory.createTicks(fAxisModel);   
            
            	//  Axis information
            	Point2D.Double axisEnd = new Point2D.Double();
            	Point2D.Double axisOrigin = new Point2D.Double();
            	Line2D.Double axisLine = new Line2D.Double();
            	
            	//  Tick information
            	double tickValue = 0;
            	Line2D.Double tickLine = new Line2D.Double();
            	
            	// Determine location of horizontal axis base line
            	if (fLocation == XAxisRenderer.BELOW)
            	{
            		//  Line at top of canvas.
            		axisEnd.setLocation(canvasRight, canvasTop);
            		axisOrigin.setLocation(canvasLeft, canvasTop);
            	}
//            	else
//            	{
//            		//  Line at bottom of canvas.
//            		axisEnd.setLocation(canvasRight, 0);
//            		axisOrigin.setLocation(canvasLeft, 0);
//            	}
            
//                ValueFormat formatter = getLabelFormatter();
            	String tickLabel = null;
            	double tickRatio = 0;
            	double tickPosition = 0;
//            	labelHeight = labelMetrics.getAscent();
            	double labelYPosition = 0;
            	
            	//  Set the y position of the label based on orientation.
            	if (fLocation == XAxisRenderer.BELOW)
            	{
            		labelYPosition = axisOrigin.y - normalizedScale * ( fTickLength + labelHeight + GL_LABEL_MARGIN);
            	}
//            	else // fLocation == ABOVE
//            	{
//            		labelYPosition = axisOrigin.y - fTickLength - GL_LABEL_MARGIN;
//            	}
            	
                //  Figure out the position of each tick and label on the axis.
            	for (int i = 0; i < ticks.length; i++)
            	{
            		tickValue = ticks[i].getValue();
            		tickRatio = axisScale.getScaleRatio(tickValue, min, max-min);
//            		tickLabel = getTimeFormat().format(ticks[i].getLabelValue());
            		tickLabel = fFormat.format(ticks[i].getLabelValue());            		
            		tickPosition = canvasLeft + (tickRatio * canvasWidth);
            		
            		//  Set the position of the tick mark.
            		if (fLocation == XAxisRenderer.BELOW)
            		{
            			tickLine.setLine(tickPosition, axisOrigin.y - (normalizedScale
                                 * fTickLength), 
            				tickPosition, axisOrigin.y);
            		}
//            		else // fLocation == ABOVE
//            		{
//            			tickLine.setLine(tickPosition, axisOrigin.y + fTickLength, 
//            				tickPosition, axisOrigin.y  - fTickLength);
//            		}
            		
            		// Center the label horizontal with respect to the tick
//                    labelWidth = labelMetrics.stringWidth(tickLabel);
                    labelWidth = sGlut.glutBitmapLength(fFont, tickLabel);                  
            		double labelXPosition = tickPosition - (normalizedScale * (labelWidth / 2));
            
            		// Adjust x position if the label is clipped by a side
//            		if (labelXPosition < canvasLeft)
//            		{
//            			labelXPosition = canvasLeft;
//            		}
//            		else if ((labelXPosition + labelWidth) > canvasRight)
//            		{
//            			labelXPosition = canvasRight - labelWidth;
//            		}
            
            		//  Draw the tick mark and its label.
    //        		g2d.draw(tickLine);
                    fGL.glBegin(GL.GL_LINES);
                        fGL.glVertex3d(tickLine.x1, tickLine.y1, 0);              
                        fGL.glVertex3d(tickLine.x2, tickLine.y2, 0);                              
                    fGL.glEnd();                
                    
    //        		g2d.drawString(
    //        			tickLabel, (float) labelXPosition, (float) labelYPosition);
            		//System.out.println("1 " + tickValue);
                    if (drawLabels == true)
					{
						GlutTextUtil.renderSpacedBitmapString2(fGL,
								(float) labelXPosition, (float) labelYPosition,
								4f, normalizedScale, GLUT.BITMAP_HELVETICA_10,
								tickLabel);
					}
                            
            	}
            	
            	//  Set the location of the axis line and draw it.
            	axisLine.setLine(axisOrigin.x, axisOrigin.y, axisEnd.x, axisEnd.y);
                
                fGL.glBegin(GL.GL_LINE_STRIP);
                    fGL.glVertex3d(axisLine.x1, 0, 0);              
                    fGL.glVertex3d(axisLine.x2, 0, 0);                              
                fGL.glEnd();                  
            	//g2d.draw(axisLine);
            	
            	return rect;
            }
            
        }

    /**
     *
     * 
     * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
     * for the Instrument Remote Control (IRC) project.
     *
     * @version	$Id: OpenGlPlot.java,v 1.5 2006/08/10 16:17:59 smaher_cvs Exp $
     * @author maher
     */
    public static class YAxisOpenGlRenderer //extends YAxisRenderer
    {


        private GL fGL;
        private int fFont;
        private final static double GL_LABEL_MARGIN = 2;
        private  GLUT sGlut = new GLUT();
        private AxisModel fAxisModel;
        private TickFactory fTickFactory = new EvenTickFactory();        
        private String fLocation = YAxisRenderer.LEFT;
        private int fTickLength = 4;    
        private ValueFormat fFormatter = new SciDecimalFormat();        
        
        /**
         * @param model
         * @param format
         * @param gl
         */
        public YAxisOpenGlRenderer(AxisModel model, GL gl,
                int font)
        {
//            super(model);
            fAxisModel = model;
            fGL = gl;
            fFont = font;
//            fLocation = ABOVE;
            
        }


        /**
         * Draw the axis, tick marks, and tick mark labels for this axis.
         * 
         * @param g2d the graphics context on which to draw
         * @param rect the unclipped drawing area bounds for rendering. This
         *            rectangle is used for sizing the axis.
         * @param dataSet not used by this renderer.
         * @return the same rectangle instance received.
         */
        public Rectangle2D draw(Rectangle2D rect, float normalizedScale, boolean drawLabels)
        {
        	// Axis information
        	double min = fAxisModel.getViewMinimum();
        	double max = min + fAxisModel.getViewExtent();
        	AxisScale axisScale = fAxisModel.getAxisScale();
        	
        	// Drawing canvas bounds information
            
            // Shift everything by (-2, -2) to align with (0,0)
        	double canvasLeft = rect.getMinX() - 2;		
        	double canvasRight = rect.getMaxX() - 1 - 2;		
        	double canvasTop = rect.getMinY() - 2;
        	double canvasBottom = rect.getMaxY() - 1 -2;
        
        	//  Canvas width and height
        	double canvasHeight = rect.getHeight() - 1;
        	
        	// Update the maximum number of labels that fit in the axis.
//        	FontMetrics labelMetrics = fFontMetrics;
//        	int labelHeight = 18;
        	
        	// Set the maximum number of ticks that will fit given the canvas
        	// height.
//        	fAxisModel.setNumberOfTicks((int)(canvasHeight / (labelHeight * 1.5)) + 1);
//        	Tick[] ticks = updateTicks();
            Tick[] ticks = fTickFactory.createTicks(fAxisModel);              
        
        	//  Axis information
        	Point2D.Double axisEnd = new Point2D.Double();
        	Point2D.Double axisOrigin = new Point2D.Double();
        	Line2D.Double axisLine = new Line2D.Double();
        	
        	//  Tick information
        	double tickValue = 0;
        	Line2D.Double tickLine = new Line2D.Double();
//        	Point2D.Double tickLabelPos = new Point2D.Double();
        	
//        	initialize();
        	
        	// Determine location of vertical axis base line
        	if (fLocation == YAxisRenderer.RIGHT)
        	{
        		//  Line at right side of canvas.
        		axisEnd.setLocation(canvasRight, canvasTop);
        		axisOrigin.setLocation(canvasRight, canvasBottom);
        	}
        	else
        	{
        		//  Line at left side of canvas.
        		axisEnd.setLocation(canvasLeft, canvasTop);
        		axisOrigin.setLocation(canvasLeft, canvasBottom);
        	}
        	
//            ValueFormat formatter = getLabelFormatter();
        	String tickLabel = null;
        	double tickRatio = 0;
//        	labelHeight = labelMetrics.getAscent();
        	double labelYPosition = 0;
        	double labelXPosition = 0;
        	double tickPosition = 0;
        	
            //  Figure out the position of each tick and label on the axis.
        	for (int i = 0; i < ticks.length; i++)
        	{
        		tickValue = ticks[i].getValue();
        		tickRatio = axisScale.getScaleRatio(tickValue, min, max-min);
        		tickLabel = fFormatter.format(ticks[i].getLabelValue());
                // Reverse Y axis for GL
                tickPosition = canvasHeight - (canvasBottom - (tickRatio * canvasHeight));
        
        		//  Set the position of the tick mark.
        		if (fLocation == YAxisRenderer.LEFT)
        		{
        			tickLine.setLine(axisOrigin.x - (normalizedScale * fTickLength), tickPosition,
        				axisOrigin.x, tickPosition);
        		}
//        		else // fLocation == RIGHT
//        		{
//        			tickLine.setLine(axisOrigin.x, tickPosition, 
//        				axisOrigin.x + fTickLength, tickPosition);
//        		}
        		
        		// Center the label with respect to the tick
//                labelYPosition = tickPosition + (labelHeight / 2);

                labelYPosition = tickPosition - 5f * normalizedScale;
        
        		// Adjust y position if the label is clipped by the top or bottom
//        		if (labelYPosition > canvasBottom)
//        		{
//        			labelYPosition = canvasBottom;
//        		}
//        		else if ((labelYPosition - labelHeight) < canvasTop)
//        		{
//        			labelYPosition = canvasTop + labelHeight;
//        		}
        
        		//  Position the label to the left or right of the tick mark.
        		if (fLocation == YAxisRenderer.LEFT)
        		{
        			labelXPosition = axisOrigin.x - 
                    normalizedScale * (fTickLength + GL_LABEL_MARGIN
                    + sGlut.glutBitmapLength(fFont, tickLabel));
//                    - labelMetrics.stringWidth(tickLabel);                    
        		}
//        		else // fLocation == RIGHT
//        		{
//        			labelXPosition = axisOrigin.x + fTickLength + GL_LABEL_MARGIN;
//        		}
        
        		//  Draw the tick mark and its label.
//        		g2d.draw(tickLine);
                fGL.glBegin(GL.GL_LINES);
                fGL.glVertex2d(tickLine.x1, tickLine.y1);              
                fGL.glVertex2d(tickLine.x2, tickLine.y2);                              
                fGL.glEnd(); 
            
            
//        		g2d.drawString(
//        			tickLabel, (float) labelXPosition, (float) labelYPosition);
//                
                if (drawLabels == true)
				{
					GlutTextUtil.renderSpacedBitmapString2(fGL,
							(float) labelXPosition, (float) labelYPosition, 2f,
							normalizedScale, GLUT.BITMAP_HELVETICA_10,
							tickLabel);
				}
                                        
        	}
        
        	//  Set the location of the axis line and draw it.
        	axisLine.setLine(axisOrigin.x, axisOrigin.y, axisEnd.x, axisEnd.y);
        	//g2d.draw(axisLine);
            fGL.glBegin(GL.GL_LINE_STRIP);
            fGL.glVertex2d(axisLine.x1, axisLine.y1);              
            fGL.glVertex2d(axisLine.x2, axisLine.y2);                              
            fGL.glEnd();    
        
        	return rect;
        }
        
    }
    public final static class XyPlotData
    {
        public double[] fBasisBufferValues;

        public float[] fXCoords;

        public SingleTrace[] fTraces;

        public AxisModel fXAxisModel;
        public AxisModel fYAxisModel;
        
        public Rectangle2D fRect2D;
        public final Unit fBasisUnits;
        public int[] fDisplayListIds;
        
        public XyPlotData(int numberOfCoords, int numberOfTraces, double basisBufferRange, Unit basisUnits)
        {
            fBasisBufferValues = new double[numberOfCoords];
            fXCoords = new float[numberOfCoords];
            fTraces = new SingleTrace[numberOfTraces];
            for (int i = 0; i < fTraces.length; i++)
            {
                fTraces[i] = new SingleTrace();
                fTraces[i].fYCoords = new float[numberOfCoords];
            }
            fBasisUnits = basisUnits;
        }

        public XyPlotData(XyPlotData toCopy)
        {
            
            fBasisBufferValues = (double []) toCopy.fBasisBufferValues.clone();
            fXCoords = (float []) toCopy.fXCoords.clone();
            
            fXAxisModel = toCopy.fXAxisModel;
            fYAxisModel = toCopy.fYAxisModel;            
            fRect2D = (Rectangle2D) toCopy.fRect2D.clone();
            fBasisUnits = toCopy.fBasisUnits;
            fDisplayListIds = (int []) toCopy.fDisplayListIds.clone();
            
            fTraces = new SingleTrace[toCopy.fTraces.length];
            for (int i = 0; i < toCopy.fTraces.length; i++)
            {
                fTraces[i] = new SingleTrace();
                
                if (toCopy.fTraces[i] != null)
                {
                    fTraces[i].fYCoords = (float []) toCopy.fTraces[i].fYCoords.clone();
                    if (toCopy.fTraces[i].fName != null)
                    {
                        fTraces[i].fName = new String(toCopy.fTraces[i].fName);
                    }
                    if (toCopy.fTraces[i].fColor != null)
                    {
                        fTraces[i].fColor = new Color(toCopy.fTraces[i].fColor.getRGB());
                    }
                }
            }

            
        }

        public double getPixelsPerSecond()
        {
            double secondsInDataChunk;
            double pixelsPerSecond = 0;
            if (fBasisUnits.equals(SI.MILLI(SI.SECOND)))
            {
                secondsInDataChunk = getWidth()/1000d;
                pixelsPerSecond = fXCoords.length/secondsInDataChunk;
            }
            else if (fBasisUnits.equals(SI.SECOND))
            {
                secondsInDataChunk = getWidth();
                pixelsPerSecond = fXCoords.length/secondsInDataChunk;                    
            }
            else
            {
                pixelsPerSecond = 0;
            }
            return pixelsPerSecond;
        }

        public float getWidth()
        {
            float w = 0f;
            if (fXCoords != null)
            {
                w = fXCoords[fXCoords.length - 1] - fXCoords[0];
            }
            return w;
        }
        
        public final class SingleTrace
        {
            public float[] fYCoords;

            public Color fColor;

            public String fName;
        }
    }
    
    public static class GlutTextUtil
    {
        
        private static GLUT sGlut = new GLUT();

        public static void renderSpacedBitmapString(
                GL gl,
                    float x, 
                    float y,
                    float spacing, 
                    int font,
                    String string) {
            float x1=x;
            char c;
            for (int i = 0; i < string.length(); i++)
            {
                gl.glRasterPos2f(x1,y);
                c = string.charAt(i);
                sGlut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_12, c);                 
                x1 = x1 + sGlut.glutBitmapWidth(font,c) + spacing;       
                
            }        
        }

        public static void renderSpacedBitmapString2(
                GL gl,
                    float x, 
                    float y,
                    float spacing, 
                    float scale,
                    int font,
                    String string) {
            float x1=x;
            char c;
            for (int i = 0; i < string.length(); i++)
            {
                gl.glRasterPos2f(x1,y);
                c = string.charAt(i);
                sGlut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_12, c);                 
                x1 = x1 + scale * (sGlut.glutBitmapWidth(font, c) + spacing);       
                
            }        
        }        

//            
//        void renderVerticalBitmapString(
//                    float x, 
//                    float y, 
//                    int bitmapHeight, 
//                    void *font,
//                    char *string)
//        {
//          
//          char *c;
//          int i;
//          for (c=string,i=0; *c != '\0'; i++,c++) {
//            glRasterPos2f(x, y+bitmapHeight*i);
//            glutBitmapCharacter(font, *c);
//          }
//        }


    }
}
