//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.gui.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;

import gov.nasa.gsfc.irc.gui.swing.AxisScrollBar;
import gov.nasa.gsfc.irc.gui.swing.plaf.AxisScrollBarUI;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;

/**
 * Implementation of AxisScrollBarUI for the Basic Look and Feel. This class 
 * was adapted from the <code>javax.swing.plaf.basic.BasicScrollBarUI</code> 
 * class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 02:54:48 $
 * @author 	Troy Ames
 */
public class BasicAxisScrollBarUI extends AxisScrollBarUI implements
		LayoutManager, SwingConstants
{

	private static final int POSITIVE_SCROLL = 1;
	private static final int NEGATIVE_SCROLL = -1;

	private static final int MIN_SCROLL = 2;
	private static final int MAX_SCROLL = 3;

	protected Dimension fMinimumThumbSize;
	protected Dimension fMaximumThumbSize;

	protected Color fThumbHighlightColor;
	protected Color fThumbLightShadowColor;
	protected Color fThumbDarkShadowColor;
	protected Color fThumbColor;
	protected Color fTrackColor;
	protected Color fTrackHighlightColor;

	protected AxisScrollBar fScrollbar;
	protected JButton fIncrButton;
	protected JButton fDecrButton;
	protected boolean fIsDragging;
	protected boolean fIsScalingLeft;
	protected boolean fIsScalingRight;
	protected TrackListener fTrackListener;
	protected ArrowButtonListener fButtonListener;
	protected ModelListener fModelListener;

	protected Rectangle fThumbRect;
	protected Rectangle fTrackRect;

	protected int fTrackHighlight;

	protected static final int NO_HIGHLIGHT = 0;
	protected static final int DECREASE_HIGHLIGHT = 1;
	protected static final int INCREASE_HIGHLIGHT = 2;

	protected ScrollListener fScrollListener;
	protected PropertyChangeListener fPropertyChangeListener;
	protected Timer fScrollTimer;

	private final static int fScrollSpeedThrottle = 60; // delay in milli seconds

	/** True indicates a middle click will absolutely position the
	 * scrollbar. */
	private boolean fSupportsAbsolutePositioning;

	/** 
	 * Hint as to what width (when vertical) or height (when horizontal)
	 * should be.
	 */
	private int fScrollBarWidth;

	/** 
	 * Hint as to what height (when vertical) or width (when horizontal)
	 * the scalar region of the thumb should be.
	 */
    protected int fScalarSize = 5;

	public static ComponentUI createUI(JComponent c)
	{
		return new BasicAxisScrollBarUI();
	}

	protected void configureScrollBarColors()
	{
		LookAndFeel.installColors(fScrollbar, "ScrollBar.background",
			"ScrollBar.foreground");
		fThumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
		fThumbLightShadowColor = UIManager.getColor("ScrollBar.thumbShadow");
		fThumbDarkShadowColor = UIManager.getColor("ScrollBar.thumbDarkShadow");
		fThumbColor = UIManager.getColor("ScrollBar.thumb");
		fTrackColor = UIManager.getColor("ScrollBar.track");
		fTrackHighlightColor = UIManager.getColor("ScrollBar.trackHighlight");
	}

	public void installUI(JComponent component)
	{
		fScrollbar = (AxisScrollBar) component;
		fThumbRect = new Rectangle(0, 0, 0, 0);
		fTrackRect = new Rectangle(0, 0, 0, 0);
		installDefaults();
		installComponents();
		installListeners();
		installKeyboardActions();
	}

	public void uninstallUI(JComponent component)
	{
		fScrollbar = (AxisScrollBar) component;
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
		uninstallKeyboardActions();
		component.remove(fIncrButton);
		component.remove(fDecrButton);
		component.setLayout(null);
		fThumbRect = null;
		fScrollbar = null;
		fIncrButton = null;
		fDecrButton = null;
	}

	protected void installDefaults()
	{
		fScrollBarWidth = UIManager.getInt("ScrollBar.width");
		
		if (fScrollBarWidth <= 0)
		{
			fScrollBarWidth = 16;
		}
		
		fMinimumThumbSize = (Dimension) UIManager
				.get("ScrollBar.minimumThumbSize");

		fMaximumThumbSize = (Dimension) UIManager
				.get("ScrollBar.maximumThumbSize");
		
		Boolean absB = 
			(Boolean) UIManager.get("ScrollBar.allowsAbsolutePositioning");
		fSupportsAbsolutePositioning = 
			(absB != null) ? absB.booleanValue(): false;

		fTrackHighlight = NO_HIGHLIGHT;
		switch (fScrollbar.getOrientation())
		{
			case JScrollBar.VERTICAL:
				fIncrButton = createIncreaseButton(SOUTH);
				fDecrButton = createDecreaseButton(NORTH);
				break;

			case JScrollBar.HORIZONTAL:
				if (fScrollbar.getComponentOrientation().isLeftToRight())
				{
					fIncrButton = createIncreaseButton(EAST);
					fDecrButton = createDecreaseButton(WEST);
				}
				else
				{
					fIncrButton = createIncreaseButton(WEST);
					fDecrButton = createDecreaseButton(EAST);
				}
				break;
		}

		fScrollbar.setLayout(this);
		fScrollbar.add(fIncrButton);
		fScrollbar.add(fDecrButton);
		fScrollbar.setEnabled(fScrollbar.isEnabled());
		fScrollbar.setOpaque(true);
		configureScrollBarColors();
		LookAndFeel.installBorder(fScrollbar, "ScrollBar.border");
	}

	protected void installComponents()
	{
	}

	protected void uninstallComponents()
	{
	}

	protected void installListeners()
	{
		fTrackListener = createTrackListener();
		fButtonListener = createArrowButtonListener();
		fModelListener = createModelListener();
		fPropertyChangeListener = createPropertyChangeListener();

		fScrollbar.addMouseListener(fTrackListener);
		fScrollbar.addMouseMotionListener(fTrackListener);
		fScrollbar.getModel().addChangeListener(fModelListener);
		fScrollbar.addPropertyChangeListener(fPropertyChangeListener);

		if (fIncrButton != null)
		{
			fIncrButton.addMouseListener(fButtonListener);
		}
		if (fDecrButton != null)
		{
			fDecrButton.addMouseListener(fButtonListener);
		}

		fScrollListener = createScrollListener();
		fScrollTimer = new Timer(fScrollSpeedThrottle, fScrollListener);
		fScrollTimer.setInitialDelay(300); // default InitialDelay?
	}

	protected void installKeyboardActions()
	{
		ActionMap map = getActionMap();

		SwingUtilities.replaceUIActionMap(fScrollbar, map);
		InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
		SwingUtilities.replaceUIInputMap(fScrollbar, JComponent.WHEN_FOCUSED,
			inputMap);
	}

	protected void uninstallKeyboardActions()
	{
		SwingUtilities.replaceUIInputMap(
			fScrollbar, JComponent.WHEN_FOCUSED, null);
		SwingUtilities.replaceUIActionMap(fScrollbar, null);
	}

	private InputMap getInputMap(int condition)
	{
		if (condition == JComponent.WHEN_FOCUSED)
		{
			InputMap keyMap = 
				(InputMap) UIManager.get("ScrollBar.focusInputMap");
			InputMap rtlKeyMap;

			if (fScrollbar.getComponentOrientation().isLeftToRight()
					|| ((rtlKeyMap = (InputMap) UIManager
							.get("ScrollBar.focusInputMap.RightToLeft")) == null))
			{
				return keyMap;
			}
			else
			{
				rtlKeyMap.setParent(keyMap);
				return rtlKeyMap;
			}
		}
		return null;
	}

	private ActionMap getActionMap()
	{
		ActionMap map = (ActionMap) UIManager.get("AxisScrollBar.actionMap");

		if (map == null)
		{
			map = createActionMap();
			if (map != null)
			{
				UIManager.getLookAndFeelDefaults().put("AxisScrollBar.actionMap",
					map);
			}
		}
		return map;
	}

	private ActionMap createActionMap()
	{
		ActionMap map = new ActionMapUIResource();
		map.put("positiveUnitIncrement", new SharedActionScroller(
				POSITIVE_SCROLL, false));
		map.put("positiveBlockIncrement", new SharedActionScroller(
				POSITIVE_SCROLL, true));
		map.put("negativeUnitIncrement", new SharedActionScroller(
				NEGATIVE_SCROLL, false));
		map.put("negativeBlockIncrement", new SharedActionScroller(
				NEGATIVE_SCROLL, true));
		map.put("minScroll", new SharedActionScroller(MIN_SCROLL, true));
		map.put("maxScroll", new SharedActionScroller(MAX_SCROLL, true));
		return map;
	}

	protected void uninstallListeners()
	{
		fScrollTimer.stop();
		fScrollTimer = null;

		if (fDecrButton != null)
		{
			fDecrButton.removeMouseListener(fButtonListener);
		}
		if (fIncrButton != null)
		{
			fIncrButton.removeMouseListener(fButtonListener);
		}

		fScrollbar.getModel().removeChangeListener(fModelListener);
		fScrollbar.removeMouseListener(fTrackListener);
		fScrollbar.removeMouseMotionListener(fTrackListener);
		fScrollbar.removePropertyChangeListener(fPropertyChangeListener);
	}

	protected void uninstallDefaults()
	{
		LookAndFeel.uninstallBorder(fScrollbar);
	}

	protected TrackListener createTrackListener()
	{
		return new TrackListener();
	}

	protected ArrowButtonListener createArrowButtonListener()
	{
		return new ArrowButtonListener();
	}

	protected ModelListener createModelListener()
	{
		return new ModelListener();
	}

	protected ScrollListener createScrollListener()
	{
		return new ScrollListener();
	}

	protected PropertyChangeListener createPropertyChangeListener()
	{
		return new PropertyChangeHandler();
	}

	public void paint(Graphics g, JComponent c)
	{
		paintTrack(g, c, getTrackBounds());
		paintThumb(g, c, getThumbBounds());
	}

	/**
	 * A vertical scrollbar's preferred width is the maximum of 
	 * preferred widths of the (non <code>null</code>)
	 * increment/decrement buttons,
	 * and the minimum width of the thumb. The preferred height is the 
	 * sum of the preferred heights of the same parts.  The basis for 
	 * the preferred size of a horizontal scrollbar is similar. 
	 * <p>
	 * The <code>preferredSize</code> is only computed once, subsequent
	 * calls to this method just return a cached size.
	 * 
	 * @param c the <code>AxisScrollBar</code> that's delegating this method to us
	 * @return the preferred size of a Basic AxisScrollBar
	 * @see #getMaximumSize
	 * @see #getMinimumSize
	 */
	public Dimension getPreferredSize(JComponent c)
	{
		Dimension result = null;

		if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
		{
			result = new Dimension(fScrollBarWidth, 48);
		}
		else
		{
			result = new Dimension(48, fScrollBarWidth);
		}
		
		return result;
	}

	/**
	 * A vertical scrollbar's minimum width is the largest 
	 * minimum width of the (non <code>null</code>) increment/decrement buttons,
	 * and the minimum width of the thumb. The minimum height is the 
	 * sum of the minimum heights of the same parts.  The basis for 
	 * the preferred size of a horizontal scrollbar is similar. 
	 * <p>
	 * The <code>minimumSize</code> is only computed once, subsequent
	 * calls to this method just return a cached size. 
	 * 
	 * @param c the <code>JScrollBar</code> that's delegating this method to us
	 * @return the minimum size of a basic <code>JScrollBar</code>
	 * @see #getMaximumSize
	 * @see #getPreferredSize
	 */
	public Dimension getMinimumSize(JComponent c)
	{
		return getPreferredSize(c);
	}

	/**
	 * @param c The JScrollBar that's delegating this method to us.
	 * @return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	 * @see #getMinimumSize
	 * @see #getPreferredSize
	 */
	public Dimension getMaximumSize(JComponent c)
	{
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	protected JButton createDecreaseButton(int orientation)
	{
		return new BasicArrowButton(orientation, UIManager
				.getColor("ScrollBar.thumb"), UIManager
				.getColor("ScrollBar.thumbShadow"), UIManager
				.getColor("ScrollBar.thumbDarkShadow"), UIManager
				.getColor("ScrollBar.thumbHighlight"));
	}

	protected JButton createIncreaseButton(int orientation)
	{
		return new BasicArrowButton(orientation, UIManager
				.getColor("ScrollBar.thumb"), UIManager
				.getColor("ScrollBar.thumbShadow"), UIManager
				.getColor("ScrollBar.thumbDarkShadow"), UIManager
				.getColor("ScrollBar.thumbHighlight"));
	}

	protected void paintDecreaseHighlight(Graphics g)
	{
		Insets insets = fScrollbar.getInsets();
		Rectangle thumbR = getThumbBounds();
		g.setColor(fTrackHighlightColor);

		if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
		{
			int x = insets.left;
			int y = fDecrButton.getY() + fDecrButton.getHeight();
			int w = fScrollbar.getWidth() - (insets.left + insets.right);
			int h = thumbR.y - y;
			g.fillRect(x, y, w, h);
		}
		else
		{
			int x, w;
			if (fScrollbar.getComponentOrientation().isLeftToRight())
			{
				x = fDecrButton.getX() + fDecrButton.getWidth();
				w = thumbR.x - x;
			}
			else
			{
				x = thumbR.x + thumbR.width;
				w = fDecrButton.getX() - x;
			}
			int y = insets.top;
			int h = fScrollbar.getHeight() - (insets.top + insets.bottom);
			g.fillRect(x, y, w, h);
		}
	}

	protected void paintIncreaseHighlight(Graphics g)
	{
		Insets insets = fScrollbar.getInsets();
		Rectangle thumbR = getThumbBounds();
		g.setColor(fTrackHighlightColor);

		if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
		{
			int x = insets.left;
			int y = thumbR.y + thumbR.height;
			int w = fScrollbar.getWidth() - (insets.left + insets.right);
			int h = fIncrButton.getY() - y;
			g.fillRect(x, y, w, h);
		}
		else
		{
			int x, w;
			if (fScrollbar.getComponentOrientation().isLeftToRight())
			{
				x = thumbR.x + thumbR.width;
				w = fIncrButton.getX() - x;
			}
			else
			{
				x = fIncrButton.getX() + fIncrButton.getWidth();
				w = thumbR.x - x;
			}
			int y = insets.top;
			int h = fScrollbar.getHeight() - (insets.top + insets.bottom);
			g.fillRect(x, y, w, h);
		}
	}

	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
	{
		g.setColor(fTrackColor);
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width,
			trackBounds.height);

		if (fTrackHighlight == DECREASE_HIGHLIGHT)
		{
			paintDecreaseHighlight(g);
		}
		else if (fTrackHighlight == INCREASE_HIGHLIGHT)
		{
			paintIncreaseHighlight(g);
		}
	}

	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
	{
		if (thumbBounds.isEmpty() || !fScrollbar.isEnabled())
		{
			return;
		}

		int w = thumbBounds.width;
		int h = thumbBounds.height;

		g.translate(thumbBounds.x, thumbBounds.y);

		g.setColor(fThumbDarkShadowColor);
		g.drawRect(0, 0, w - 1, h - 1);
		g.setColor(fThumbColor);
		g.fillRect(0, 0, w - 1, h - 1);

		g.setColor(fThumbHighlightColor);
		g.drawLine(1, 1, 1, h - 2);
		g.drawLine(2, 1, w - 3, 1);

		g.drawLine(fScalarSize, 1, fScalarSize, h - 2);
		g.drawLine(2, 1, w - 3, 1);

		g.setColor(fThumbLightShadowColor);
		g.drawLine(2, h - 2, w - 2, h - 2);
		g.drawLine(w - 2, 1, w - 2, h - 3);

		g.translate(-thumbBounds.x, -thumbBounds.y);
	}

	/** 
	 * Return the smallest acceptable size for the thumb.  If the scrollbar
	 * becomes so small that this size isn't available, the thumb will be
	 * hidden.  
	 * <p>
	 * <b>Warning </b>: the value returned by this method should not be
	 * be modified, it's a shared static constant.
	 *
	 * @return The smallest acceptable size for the thumb.
	 * @see #getMaximumThumbSize
	 */
	protected Dimension getMinimumThumbSize()
	{
		return fMinimumThumbSize;
	}

	/** 
	 * Return the largest acceptable size for the thumb.  To create a fixed 
	 * size thumb one make this method and <code>getMinimumThumbSize</code> 
	 * return the same value.
	 * <p>
	 * <b>Warning </b>: the value returned by this method should not be
	 * be modified, it's a shared static constant.
	 *
	 * @return The largest acceptable size for the thumb.
	 * @see #getMinimumThumbSize
	 */
	protected Dimension getMaximumThumbSize()
	{
		return fMaximumThumbSize;
	}

	/*
	 * LayoutManager Implementation
	 */

	public void addLayoutComponent(String name, Component child)
	{
	}
	public void removeLayoutComponent(Component child)
	{
	}

	public Dimension preferredLayoutSize(Container scrollbarContainer)
	{
		return getPreferredSize((JComponent) scrollbarContainer);
	}

	public Dimension minimumLayoutSize(Container scrollbarContainer)
	{
		return getMinimumSize((JComponent) scrollbarContainer);
	}

	protected void layoutVScrollbar(AxisScrollBar sb)
	{
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/*
		 * Width and left edge of the buttons and thumb.
		 */
		int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
		int itemX = sbInsets.left;

		/* Nominal locations of the buttons, assuming their preferred
		 * size will fit.
		 */
		int decrButtonH = fDecrButton.getPreferredSize().height;
		int decrButtonY = sbInsets.top;

		int incrButtonH = fIncrButton.getPreferredSize().height;
		int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);

		/* The thumb must fit within the height left over after we
		 * subtract the preferredSize of the buttons and the insets.
		 */
		int sbInsetsH = sbInsets.top + sbInsets.bottom;
		int sbButtonsH = decrButtonH + incrButtonH;
		float trackH = sbSize.height - (sbInsetsH + sbButtonsH);

		/* Compute the height and origin of the thumb.   The case
		 * where the thumb is at the bottom edge is handled specially 
		 * to avoid numerical problems in computing thumbY.  Enforce
		 * the thumbs min/max dimensions.  If the thumb doesn't
		 * fit in the track (trackH) we'll hide it later.
		 */
		double min = sb.getMinimum();
		double extent = sb.getVisibleAmount();
		double range = sb.getMaximum() - min;
		double value = sb.getValue();

		int thumbH = (range <= 0) ? getMaximumThumbSize().height
				: (int) (trackH * (extent / range));
		thumbH = Math.max(thumbH, getMinimumThumbSize().height);
		thumbH = Math.min(thumbH, getMaximumThumbSize().height);

		// Calculate the position of the thumb
		int thumbY = decrButtonY;
		
		if (sb.getValue() < (sb.getMaximum() - sb.getVisibleAmount()))
		{
			float thumbRange = trackH - thumbH;
			//thumbY = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
			thumbY = (int) (0.5f + thumbRange - (thumbRange * ((value - min) / (range - extent))));
			thumbY += decrButtonY + decrButtonH;
		}

		/* If the buttons don't fit, allocate half of the available 
		 * space to each and move the lower one (incrButton) down.
		 */
		int sbAvailButtonH = (sbSize.height - sbInsetsH);
		if (sbAvailButtonH < sbButtonsH)
		{
			incrButtonH = decrButtonH = sbAvailButtonH / 2;
			incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
		}
		fDecrButton.setBounds(itemX, decrButtonY, itemW, decrButtonH);
		fIncrButton.setBounds(itemX, incrButtonY, itemW, incrButtonH);

		/* Update the trackRect field.
		 */
		int itrackY = decrButtonY + decrButtonH;
		int itrackH = incrButtonY - itrackY;
		fTrackRect.setBounds(itemX, itrackY, itemW, itrackH);

		/* If the thumb isn't going to fit, zero it's bounds.  Otherwise
		 * make sure it fits between the buttons.  Note that setting the
		 * thumbs bounds will cause a repaint.
		 */
		if (thumbH > (int) trackH)
		{
			thumbH = (int) trackH;
		}
		if ((thumbY + thumbH) > incrButtonY)
		{
			thumbY = incrButtonY - thumbH;
		}
		if (thumbY < (decrButtonY + decrButtonH))
		{
			thumbY = decrButtonY + decrButtonH + 1;
		}
		
		setThumbBounds(itemX, thumbY, itemW, thumbH);
	}

	protected void layoutHScrollbar(AxisScrollBar sb)
	{
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/* Height and top edge of the buttons and thumb.
		 */
		int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);
		int itemY = sbInsets.top;

		boolean ltr = sb.getComponentOrientation().isLeftToRight();

		/* Nominal locations of the buttons, assuming their preferred
		 * size will fit.
		 */
		int leftButtonW = (ltr ? fDecrButton : fIncrButton).getPreferredSize().width;
		int rightButtonW = (ltr ? fIncrButton : fDecrButton).getPreferredSize().width;
		int leftButtonX = sbInsets.left;
		int rightButtonX = sbSize.width - (sbInsets.right + rightButtonW);

		/* The thumb must fit within the width left over after we
		 * subtract the preferredSize of the buttons and the insets.
		 */
		int sbInsetsW = sbInsets.left + sbInsets.right;
		int sbButtonsW = leftButtonW + rightButtonW;
		float trackW = sbSize.width - (sbInsetsW + sbButtonsW);

		/* Compute the width and origin of the thumb.  Enforce
		 * the thumbs min/max dimensions.  The case where the thumb 
		 * is at the right edge is handled specially to avoid numerical 
		 * problems in computing thumbX.  If the thumb doesn't
		 * fit in the track (trackH) we'll hide it later.
		 */
		double min = sb.getMinimum();
		double max = sb.getMaximum();
		double extent = sb.getVisibleAmount();
		double range = max - min;
		double value = sb.getValue();

		int thumbW = (range <= 0) ? getMaximumThumbSize().width
				: (int) (trackW * (extent / range));
		thumbW = Math.max(thumbW, getMinimumThumbSize().width);
		thumbW = Math.min(thumbW, getMaximumThumbSize().width);

		int thumbX = ltr ? rightButtonX - thumbW : leftButtonX + leftButtonW;
		if (sb.getValue() < (max - sb.getVisibleAmount()))
		{
			float thumbRange = trackW - thumbW;
			if (ltr)
			{
				thumbX = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
			}
			else
			{
				thumbX = (int) (0.5f + (thumbRange * ((max - extent - value) / (range - extent))));
			}
			thumbX += leftButtonX + leftButtonW;
		}

		/* If the buttons don't fit, allocate half of the available 
		 * space to each and move the right one over.
		 */
		int sbAvailButtonW = (sbSize.width - sbInsetsW);
		if (sbAvailButtonW < sbButtonsW)
		{
			rightButtonW = leftButtonW = sbAvailButtonW / 2;
			rightButtonX = sbSize.width - (sbInsets.right + rightButtonW);
		}

		(ltr ? fDecrButton : fIncrButton).setBounds(leftButtonX, itemY,
			leftButtonW, itemH);
		(ltr ? fIncrButton : fDecrButton).setBounds(rightButtonX, itemY,
			rightButtonW, itemH);

		/* Update the trackRect field.
		 */
		int itrackX = leftButtonX + leftButtonW;
		int itrackW = rightButtonX - itrackX;
		fTrackRect.setBounds(itrackX, itemY, itrackW, itemH);

		/* Make sure the thumb fits between the buttons.  Note 
		 * that setting the thumbs bounds causes a repaint.
		 */
		if (thumbW > (int) trackW)
		{
			thumbW = (int) trackW;
		}

		if (thumbX + thumbW > rightButtonX)
		{
			thumbX = rightButtonX - thumbW;
		}
		if (thumbX < leftButtonX + leftButtonW)
		{
			thumbX = leftButtonX + leftButtonW + 1;
		}
		
		setThumbBounds(thumbX, itemY, thumbW, itemH);
	}

	public void layoutContainer(Container scrollbarContainer)
	{
		/* If the user is dragging the value, we'll assume that the 
		 * scrollbars layout is OK modulo the thumb which is being 
		 * handled by the dragging code.
		 */
		if (fIsDragging || fIsScalingLeft || fIsScalingRight)
		{
			return;
		}

		AxisScrollBar scrollbar = (AxisScrollBar) scrollbarContainer;
		switch (scrollbar.getOrientation())
		{
			case JScrollBar.VERTICAL:
				layoutVScrollbar(scrollbar);
				break;

			case JScrollBar.HORIZONTAL:
				layoutHScrollbar(scrollbar);
				break;
		}
	}

	/**
	 * Set the bounds of the thumb and force a repaint that includes
	 * the old thumbBounds and the new one.
	 *
	 * @see #getThumbBounds
	 */
	protected void setThumbBounds(int x, int y, int width, int height)
	{
		/* If the thumbs bounds haven't changed, we're done.
		 */
		if ((fThumbRect.x == x) && (fThumbRect.y == y)
				&& (fThumbRect.width == width) && (fThumbRect.height == height))
		{
			return;
		}

		/* Update thumbRect, and repaint the union of x,y,w,h and 
		 * the old thumbRect.
		 */
		int minX = Math.min(x, fThumbRect.x);
		int minY = Math.min(y, fThumbRect.y);
		int maxX = Math.max(x + width, fThumbRect.x + fThumbRect.width);
		int maxY = Math.max(y + height, fThumbRect.y + fThumbRect.height);

		fThumbRect.setBounds(x, y, width, height);
		fScrollbar.repaint(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * Return the current size/location of the thumb.
	 * <p>
	 * <b>Warning </b>: the value returned by this method should not be
	 * be modified, it's a reference to the actual rectangle, not a copy.
	 *
	 * @return The current size/location of the thumb.
	 * @see #setThumbBounds
	 */
	protected Rectangle getThumbBounds()
	{
		return fThumbRect;
	}

	protected boolean inScalarRegion(int x, int y)
	{
		return inScalarRegionLeft(x, y) || inScalarRegionRight(x, y);
	}
	
	/**
	 * Checks if the give point is in the scalar region on the top or left
	 * side of the thumb.
	 * 
	 * @param x mouse x position
	 * @param y mouse y position
	 * @return true if point is in scalar region, false otherwise.
	 */
	protected boolean inScalarRegionLeft(int x, int y)
	{
		boolean result = false;
		Rectangle thumbRect = getThumbBounds();
		
		if (thumbRect.contains(x, y))
		{
			if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
			{
				if(Math.abs(thumbRect.getMinY() - y) < fScalarSize)
				{
					// The mouse is over the left end of the rectangle
					result = true;
				}
			}
			else
			{
				if(Math.abs(thumbRect.getMinX() - x) < fScalarSize)
				{
					// The mouse is over the top end of the rectangle
					result = true;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Checks if the give point is in the scalar region on the bottom or right
	 * side of the thumb.
	 * 
	 * @param x mouse x position
	 * @param y mouse y position
	 * @return true if point is in scalar region, false otherwise.
	 */
	protected boolean inScalarRegionRight(int x, int y)
	{
		boolean result = false;
		Rectangle thumbRect = getThumbBounds();
		
		if (thumbRect.contains(x, y))
		{
			if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
			{
				if(Math.abs(thumbRect.getMaxY() - y) < fScalarSize)
				{
					// The mouse is over the left end of the rectangle
					result = true;
				}
			}
			else
			{
				if(Math.abs(thumbRect.getMaxX() - x) < fScalarSize)
				{
					// The mouse is over the right end of the rectangle
					result = true;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the current bounds of the track, i.e. the space in between
	 * the increment and decrement buttons, less the insets.  The value
	 * returned by this method is updated each time the scrollbar is
	 * laid out (validated).
	 * <p>
	 * <b>Warning </b>: the value returned by this method should not be
	 * be modified, it's a reference to the actual rectangle, not a copy.
	 *
	 * @return the current bounds of the scrollbar track
	 * @see #layoutContainer
	 */
	protected Rectangle getTrackBounds()
	{
		return fTrackRect;
	}

	/*
	 * Method for scrolling by a block increment.
	 * Added for mouse wheel scrolling support, RFE 4202656.
	 */
	static void scrollByBlock(AxisScrollBar scrollbar, int direction)
	{
		// This method is called from BasicScrollPaneUI to implement wheel
		// scrolling, and also from scrollByBlock().
		double oldValue = scrollbar.getValue();
		double blockIncrement = scrollbar.getBlockIncrement(direction);
		double delta = blockIncrement * ((direction > 0) ? +1.0 : -1.0);

		double newValue = oldValue + delta;

		// Check that new value is in range
		if (scrollbar.getOrientation() == JScrollBar.VERTICAL
				&& newValue > (scrollbar.getMaximum() - scrollbar.getVisibleAmount()))
		{
			newValue = scrollbar.getMaximum() - scrollbar.getVisibleAmount();
		}
		else if (newValue > scrollbar.getMaximum())
		{
			newValue = scrollbar.getMaximum();
		}
		else if (newValue < scrollbar.getMinimum())
		{
			newValue = scrollbar.getMinimum();
		}
		
		scrollbar.setValue(newValue);
	}

	protected void scrollByBlock(int direction)
	{
		scrollByBlock(fScrollbar, direction);
		fTrackHighlight = direction > 0 ? INCREASE_HIGHLIGHT
				: DECREASE_HIGHLIGHT;
		Rectangle dirtyRect = getTrackBounds();
		fScrollbar.repaint(dirtyRect.x, dirtyRect.y, dirtyRect.width,
			dirtyRect.height);
	}

	/*
	 * Method for scrolling by a unit increment.
	 * Added for mouse wheel scrolling support, RFE 4202656.
	 */
	static void scrollByUnits(AxisScrollBar scrollbar, int direction, int units)
	{
		// This method is called from BasicScrollPaneUI to implement wheel
		// scrolling, as well as from scrollByUnit().
		double delta = units;

		if (direction > 0)
		{
			delta *= scrollbar.getUnitIncrement(direction);
		}
		else
		{
			delta *= -scrollbar.getUnitIncrement(direction);
		}

		double oldValue = scrollbar.getValue();
		double newValue = oldValue + delta;

		// Check for overflow.
		if (scrollbar.getOrientation() == JScrollBar.VERTICAL
				&& newValue > (scrollbar.getMaximum() - scrollbar.getVisibleAmount()))
		{
			newValue = scrollbar.getMaximum() - scrollbar.getVisibleAmount();
		}
		else if (newValue > scrollbar.getMaximum())
		{
			newValue = scrollbar.getMaximum();
		}
		else if (newValue < scrollbar.getMinimum())
		{
			newValue = scrollbar.getMinimum();
		}

		scrollbar.setValue(newValue);
	}

	protected void scrollByUnit(int direction)
	{
		scrollByUnits(fScrollbar, direction, 1);
	}

	/**
	 * Indicates whether the user can absolutely position the offset with
	 * a mouse click (usually the middle mouse button).
	 * <p>The return value is determined from the UIManager property
	 * ScrollBar.allowsAbsolutePositioning.
	 */
	private boolean getSupportsAbsolutePositioning()
	{
		return fSupportsAbsolutePositioning;
	}

	/**
	 * A listener to listen for model changes.
	 *
	 */
	protected class ModelListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			layoutContainer(fScrollbar);
		}
	}

	/**
	 * Track mouse drags.
	 */
	protected class TrackListener extends MouseAdapter implements
			MouseMotionListener
	{
		protected transient int fOffset;
		protected transient int fCurrentMouseX, fCurrentMouseY;
		private transient int fDirection = +1;

		public void mouseReleased(MouseEvent e)
		{
			if (SwingUtilities.isRightMouseButton(e)
					|| (!getSupportsAbsolutePositioning() 
							&& SwingUtilities.isMiddleMouseButton(e)))
				return;
			if (!fScrollbar.isEnabled())
				return;

			Rectangle r = getTrackBounds();
			fScrollbar.repaint(r.x, r.y, r.width, r.height);

			fTrackHighlight = NO_HIGHLIGHT;
			fIsDragging = false;
			fIsScalingLeft = false;
			fIsScalingRight = false;
			fOffset = 0;
			fScrollTimer.stop();
			fScrollbar.setValueIsAdjusting(false);
		}

		/**
		 * If the mouse is pressed above the "thumb" component
		 * then reduce the scrollbars value by one page ("page up"), 
		 * otherwise increase it by one page.  If there is no 
		 * thumb then page up if the mouse is in the upper half
		 * of the track.
		 */
		public void mousePressed(MouseEvent e)
		{
			if (SwingUtilities.isRightMouseButton(e)
					|| (!getSupportsAbsolutePositioning() 
							&& SwingUtilities.isMiddleMouseButton(e)))
				return;
			if (!fScrollbar.isEnabled())
				return;

			if (!fScrollbar.hasFocus() && fScrollbar.isRequestFocusEnabled())
			{
				fScrollbar.requestFocus();
			}

			fScrollbar.setValueIsAdjusting(true);

			fCurrentMouseX = e.getX();
			fCurrentMouseY = e.getY();

			// Clicked in the Thumb area?
			if (getThumbBounds().contains(fCurrentMouseX, fCurrentMouseY))
			{
				switch (fScrollbar.getOrientation())
				{
					case JScrollBar.VERTICAL:
						fOffset = fCurrentMouseY - getThumbBounds().y;
						break;
					case JScrollBar.HORIZONTAL:
						fOffset = fCurrentMouseX - getThumbBounds().x;
						break;
				}

				if (inScalarRegionLeft(fCurrentMouseX, fCurrentMouseY))
				{
					// Mouse is over the scalar region of the thumb
					fIsScalingLeft = true;
				}
				else if (inScalarRegionRight(fCurrentMouseX, fCurrentMouseY))
				{
					// Mouse is over the scalar region of the thumb
					fIsScalingRight = true;
				}
				else

				{
					fIsDragging = true;
				}
				
				return;
			}
			else if (getSupportsAbsolutePositioning()
					&& SwingUtilities.isMiddleMouseButton(e))
			{
				switch (fScrollbar.getOrientation())
				{
					case JScrollBar.VERTICAL:
						fOffset = getThumbBounds().height / 2;
						break;
					case JScrollBar.HORIZONTAL:
						fOffset = getThumbBounds().width / 2;
						break;
				}
				fIsDragging = true;
				setValueFrom(e);
				return;
			}
			
			fIsDragging = false;
			fIsScalingLeft = false;
			fIsScalingRight = false;

			Dimension sbSize = fScrollbar.getSize();
			fDirection = +1;

			switch (fScrollbar.getOrientation())
			{
				case JScrollBar.VERTICAL:
					if (getThumbBounds().isEmpty())
					{
						int scrollbarCenter = sbSize.height / 2;
						fDirection = (fCurrentMouseY < scrollbarCenter) ? -1 : 1;
					}
					else
					{
						int thumbY = getThumbBounds().y;
						fDirection = (fCurrentMouseY < thumbY) ? -1 : 1;
					}
					break;
				case JScrollBar.HORIZONTAL:
					if (getThumbBounds().isEmpty())
					{
						int scrollbarCenter = sbSize.width / 2;
						fDirection = (fCurrentMouseX < scrollbarCenter) ? -1 : +1;
					}
					else
					{
						int thumbX = getThumbBounds().x;
						fDirection = (fCurrentMouseX < thumbX) ? -1 : +1;
					}
					if (!fScrollbar.getComponentOrientation().isLeftToRight())
					{
						fDirection = -fDirection;
					}
					break;
			}
			
			scrollByBlock(fDirection);

			fScrollTimer.stop();
			fScrollListener.setDirection(fDirection);
			fScrollListener.setScrollByBlock(true);
			startScrollTimerIfNecessary();
		}

	    /**
	     * Invoked when the mouse exits a component.
	     */
	    public void mouseExited(MouseEvent e) 
	    {
			// Here the mouse is leaving the component
			fScrollbar.setCursor(sDefaultCursor);
	    }

	    /** 
		 * Set the models value to the position of the thumb's top of Vertical
		 * scrollbar, or the left/right of Horizontal scrollbar in
		 * left-to-right/right-to-left scrollbar relative to the origin of the
		 * track.
		 */
		public void mouseDragged(MouseEvent e)
		{
			if (SwingUtilities.isRightMouseButton(e)
					|| (!getSupportsAbsolutePositioning() 
							&& SwingUtilities.isMiddleMouseButton(e)))
				return;
			if (!fScrollbar.isEnabled() || getThumbBounds().isEmpty())
			{
				return;
			}
			if (fIsDragging)
			{
				setValueFrom(e);
			}
			else if (fIsScalingLeft)
			{
				setExtentLeftFrom(e);
			}
			else if (fIsScalingRight)
			{
				setExtentRightFrom(e);
			}
			else

			{
				fCurrentMouseX = e.getX();
				fCurrentMouseY = e.getY();
				startScrollTimerIfNecessary();
			}
		}

		/**
		 * Sets the thumb extent on the left or top side of the thumb when
		 * the resize region is dragged.
		 * 
		 * @param e event to use for Mouse position
		 */
		private void setExtentLeftFrom(MouseEvent e)
		{
			AxisModel model = fScrollbar.getModel();
			Rectangle thumbRect = getThumbBounds();
			int trackLength;
			int trackOffset;
			Dimension minThumbDimension = getMinimumThumbSize();

			if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
			{
				trackOffset = fDecrButton.getY() + fDecrButton.getHeight();
				trackLength = getTrackBounds().height;
				
				// Get thumb screen position
				int thumbMax = fIncrButton.getY();
				int thumbPos = Math.max(trackOffset, 
					Math.min(e.getY(), thumbRect.y + thumbRect.height - 1));
				int thumbHeight = thumbRect.y + thumbRect.height - thumbPos;
				
				// Verify thumbHeight is height > 0 and 
				// thumb position + height <= maximum
				int screenThumbPos = thumbPos;
				int screenThumbHeight = thumbHeight;
				
				if (thumbHeight < minThumbDimension.height)
				{
					screenThumbPos = screenThumbPos - minThumbDimension.height + thumbHeight;
					screenThumbHeight = minThumbDimension.height;
				}

				setThumbBounds(
					thumbRect.x, screenThumbPos, thumbRect.width, screenThumbHeight);

				// Set the scrollbars value and extent.
				double valueMax = model.getMaximum();
				double valueMin = model.getMinimum();
				double valueRange = valueMax - valueMin;
				double newExtent = valueRange * ((double) thumbHeight / trackLength);
				double newValue = valueMin
						+ (valueRange * (1.0d - ((double) (thumbHeight
								+ thumbPos - trackOffset) / trackLength)));

				fScrollbar.setValues(newValue, newExtent, valueMin, valueMax);
			}
			else
			{
				trackOffset = fDecrButton.getX() + fDecrButton.getWidth();
				trackLength = getTrackBounds().width;
				
				// Get thumb screen position
				int thumbMax = fIncrButton.getX();
				int thumbPos = Math.max(trackOffset, 
					Math.min(e.getX(), thumbRect.x + thumbRect.width - 1));
				int thumbWidth = thumbRect.x + thumbRect.width - thumbPos;
				
				// Verify thumbWidth is width > 0 and 
				// thumb position + width <= maximum
				int screenThumbPos = thumbPos;
				int screenThumbWidth = thumbWidth;
				
				if (thumbWidth < minThumbDimension.width)
				{
					screenThumbPos = screenThumbPos - minThumbDimension.width + thumbWidth;
					screenThumbWidth = minThumbDimension.width;
				}

				setThumbBounds(
					screenThumbPos, thumbRect.y, screenThumbWidth, thumbRect.height);

				// Set the scrollbars value and extent.
				double valueMax = model.getMaximum();
				double valueMin = model.getMinimum();
				double valueRange = valueMax - valueMin;
				double newExtent = valueRange * ((double) thumbWidth / trackLength);
				double newValue = valueMin
						+ (valueRange * ((double) (thumbPos - trackOffset) / trackLength));

				fScrollbar.setValues(newValue, newExtent, valueMin, valueMax);
			}
		}

		/**
		 * Sets the thumb extent on the right or bottom side of the thumb when
		 * the resize region is dragged.
		 * 
		 * @param e event to use for Mouse position
		 */
		private void setExtentRightFrom(MouseEvent e)
		{
			AxisModel model = fScrollbar.getModel();
			Rectangle thumbRect = getThumbBounds();
			int trackLength;
			int trackOffset;
			Dimension minThumbDimension = getMinimumThumbSize();

			if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
			{
				trackOffset = fDecrButton.getY() + fDecrButton.getHeight();
				trackLength = getTrackBounds().height;
				
				// Get thumb screen position
				int thumbMax = fIncrButton.getY();
				int thumbPos = thumbRect.y;
				int thumbHeight = e.getY() - thumbPos;
				
				// Verify thumbHeight is height > 0 and 
				// thumb position + height <= maximum
				if (thumbHeight <= 0)
				{
					thumbHeight = 1;
				}
				else if ((thumbPos + thumbHeight) > thumbMax)
				{
					thumbHeight = thumbMax - thumbPos;
				}

				int minThumbHeight = Math.max(thumbHeight, minThumbDimension.height);
				setThumbBounds(
					thumbRect.x, thumbRect.y, thumbRect.width, minThumbHeight);

				// Set the scrollbars value and extent.
				double valueMax = model.getMaximum();
				double valueMin = model.getMinimum();
				double valueRange = valueMax - valueMin;
				double newExtent = valueRange * ((double) thumbHeight / trackLength);
				double newValue = valueMin
						+ (valueRange * (1.0d - ((double) (thumbHeight
								+ thumbPos - trackOffset) / trackLength)));

				fScrollbar.setValues(newValue, newExtent, valueMin, valueMax);
			}
			else
			{
				int thumbMax;
				// Get thumb screen position
				int thumbPos = thumbRect.x;
				int thumbWidth = e.getX() - thumbPos;
				
				if (fScrollbar.getComponentOrientation().isLeftToRight())
				{
					thumbMax = fIncrButton.getX();
				}
				else
				{
					thumbMax = fDecrButton.getX();
				}
				
				// Verify thumb width > 0 and 
				// thumb position + width <= maximum
				if (thumbWidth <= 0)
				{
					thumbWidth = 1;
				}
				else if ((thumbPos + thumbWidth) > thumbMax)
				{
					thumbWidth = thumbMax - thumbPos;
				}
				
				int minThumbWidth = Math.max(thumbWidth, minThumbDimension.width);
				setThumbBounds(
					thumbRect.x, thumbRect.y, minThumbWidth, thumbRect.height);

				trackLength = getTrackBounds().width;
				
				// Set the scrollbars extent.
				double valueMax = model.getMaximum();
				double valueMin = model.getMinimum();
				double valueRange = valueMax - valueMin;
				double newExtent = valueRange * ((double) thumbWidth / trackLength);

				fScrollbar.setVisibleAmount(newExtent);
			}
		}

		private void setValueFrom(MouseEvent e)
		{
			AxisModel model = fScrollbar.getModel();
			Rectangle thumbR = getThumbBounds();
			float trackLength;
			int thumbMin, thumbMax, thumbPos;

			if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
			{
				thumbMin = fDecrButton.getY() + fDecrButton.getHeight();
				thumbMax = fIncrButton.getY() - thumbR.height;
				thumbPos = Math.min(thumbMax, Math.max(thumbMin,
					(e.getY() - fOffset)));
				setThumbBounds(thumbR.x, thumbPos, thumbR.width, thumbR.height);
				trackLength = getTrackBounds().height;

				/*
				 * Set the scrollbars value. If the thumb has reached the end of
				 * the scrollbar, then just set the value to its minimum.
				 * Otherwise compute the value as accurately as possible. Note
				 * that in the vertical orientation the axis model is inverted
				 * relative to the scrollbar with the minimum value at the
				 * bottom and the max at the top.
				 */
				if (thumbPos == thumbMax)
				{
					fScrollbar.setValue(model.getMinimum());
				}
				else
				{
					double valueMax = model.getMaximum() - model.getViewExtent();
					double valueRange = valueMax - model.getMinimum();
					double thumbValue = thumbPos - thumbMin;
					double thumbRange = thumbMax - thumbMin;
					double value = ((thumbMax - thumbPos) / thumbRange) * valueRange;
					fScrollbar.setValue(value + model.getMinimum());
				}
			}
			else
			{
				if (fScrollbar.getComponentOrientation().isLeftToRight())
				{
					thumbMin = fDecrButton.getX() + fDecrButton.getWidth();
					thumbMax = fIncrButton.getX() - thumbR.width;
				}
				else
				{
					thumbMin = fIncrButton.getX() + fIncrButton.getWidth();
					thumbMax = fDecrButton.getX() - thumbR.width;
				}
				thumbPos = Math.min(thumbMax, Math.max(thumbMin,
					(e.getX() - fOffset)));
				setThumbBounds(thumbPos, thumbR.y, thumbR.width, thumbR.height);
				trackLength = getTrackBounds().width;
				
				/* Set the scrollbars value.  If the thumb has reached the end of
				 * the scrollbar, then just set the value to its maximum.  Otherwise
				 * compute the value as accurately as possible.
				 */
				if (thumbPos == thumbMax)
				{
					if (fScrollbar.getComponentOrientation().isLeftToRight())
					{
						fScrollbar.setValue(model.getMaximum() - model.getViewExtent());
					}
					else
					{
						fScrollbar.setValue(model.getMinimum());
					}
				}
				else
				{
					double valueMax = model.getMaximum() - model.getViewExtent();
					double valueRange = valueMax - model.getMinimum();
					double thumbValue = thumbPos - thumbMin;
					double thumbRange = thumbMax - thumbMin;
					double value;
					
					if (fScrollbar.getComponentOrientation().isLeftToRight())
					{
						value = (thumbValue / thumbRange) * valueRange;
					}
					else
					{
						value = ((thumbMax - thumbPos) / thumbRange) * valueRange;
					}
					
					fScrollbar.setValue(value + model.getMinimum());
				}
			}
		}

		private void startScrollTimerIfNecessary()
		{
			if (fScrollTimer.isRunning())
			{
				return;
			}
			switch (fScrollbar.getOrientation())
			{
				case JScrollBar.VERTICAL:
					if (fDirection > 0)
					{
						if (getThumbBounds().y + getThumbBounds().height 
								< fTrackListener.fCurrentMouseY)
						{
							fScrollTimer.start();
						}
					}
					else if (getThumbBounds().y > fTrackListener.fCurrentMouseY)
					{
						fScrollTimer.start();
					}
					break;
				case JScrollBar.HORIZONTAL:
					if (fDirection > 0)
					{
						if (getThumbBounds().x + getThumbBounds().width 
								< fTrackListener.fCurrentMouseX)
						{
							fScrollTimer.start();
						}
					}
					else if (getThumbBounds().x > fTrackListener.fCurrentMouseX)
					{
						fScrollTimer.start();
					}
					break;
			}
		}

	    protected final Cursor sDefaultCursor = 
			new Cursor(Cursor.DEFAULT_CURSOR);
	    protected final Cursor sLeftCursor = 
			new Cursor(Cursor.W_RESIZE_CURSOR);
	    protected final Cursor sRightCursor = 
			new Cursor(Cursor.E_RESIZE_CURSOR);
	    protected final Cursor sTopCursor = 
			new Cursor(Cursor.N_RESIZE_CURSOR);

	    public void mouseMoved(MouseEvent e)
		{
			// TODO handle scalar area
			fCurrentMouseX = e.getX();
			fCurrentMouseY = e.getY();
			Rectangle thumbRect = getThumbBounds();

			if (inScalarRegion(fCurrentMouseX, fCurrentMouseY))
			{
				// Mouse is over the scalar region of the thumb
				if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
				{
					fScrollbar.setCursor(sTopCursor);
				}
				else
				{
					fScrollbar.setCursor(sLeftCursor);
				}
			}
			else
			{
				// Here the mouse is outside of the rectangle
				fScrollbar.setCursor(sDefaultCursor);
			}
		}
	}

	/**
	 * Listener for cursor keys.
	 */
	protected class ArrowButtonListener extends MouseAdapter
	{
		// Because we are handling both mousePressed and Actions
		// we need to make sure we don't fire under both conditions.
		// (keyfocus on scrollbars causes action without mousePress
		boolean handledEvent;

		public void mousePressed(MouseEvent e)
		{
			int direction;
			
			if (!fScrollbar.isEnabled())
			{
				return;
			}
			// not an unmodified left mouse button
			//if(e.getModifiers() != InputEvent.BUTTON1_MASK) {return; }
			if (!SwingUtilities.isLeftMouseButton(e))
			{
				return;
			}

			direction = (e.getSource() == fIncrButton) ? 1 : -1;

			scrollByUnit(direction);
			fScrollTimer.stop();
			fScrollListener.setDirection(direction);
			fScrollListener.setScrollByBlock(false);
			fScrollTimer.start();

			handledEvent = true;
			if (!fScrollbar.hasFocus() && fScrollbar.isRequestFocusEnabled())
			{
				fScrollbar.requestFocus();
			}
		}

		public void mouseReleased(MouseEvent e)
		{
			fScrollTimer.stop();
			handledEvent = false;
			fScrollbar.setValueIsAdjusting(false);
		}
	}

	/**
	 * Listener for scrolling events initiated in the
	 * <code>ScrollPane</code>.
	 */
	protected class ScrollListener implements ActionListener
	{
		int direction = +1;
		boolean useBlockIncrement;

		public ScrollListener()
		{
			direction = +1;
			useBlockIncrement = false;
		}

		public ScrollListener(int dir, boolean block)
		{
			direction = dir;
			useBlockIncrement = block;
		}

		public void setDirection(int direction)
		{
			this.direction = direction;
		}
		public void setScrollByBlock(boolean block)
		{
			this.useBlockIncrement = block;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (useBlockIncrement)
			{
				scrollByBlock(direction);
				// Stop scrolling if the thumb catches up with the mouse
				if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
				{
					if (direction > 0)
					{
						if (getThumbBounds().y + getThumbBounds().height 
								>= fTrackListener.fCurrentMouseY)
						{
							((Timer) e.getSource()).stop();
						}
					}
					else if (getThumbBounds().y <= fTrackListener.fCurrentMouseY)
					{
						((Timer) e.getSource()).stop();
					}
				}
				else
				{
					if (direction > 0)
					{
						if (getThumbBounds().x + getThumbBounds().width 
								>= fTrackListener.fCurrentMouseX)
						{
							((Timer) e.getSource()).stop();
						}
					}
					else if (getThumbBounds().x <= fTrackListener.fCurrentMouseX)
					{
						((Timer) e.getSource()).stop();
					}
				}
			}
			else
			{
				scrollByUnit(direction);
			}

			if (direction > 0
					&& fScrollbar.getValue() + fScrollbar.getVisibleAmount() 
						>= fScrollbar.getMaximum())
			{
				((Timer) e.getSource()).stop();
			}
			else if (direction < 0
					&& fScrollbar.getValue() <= fScrollbar.getMinimum())
			{
				((Timer) e.getSource()).stop();
			}
		}
	}

	public class PropertyChangeHandler implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String propertyName = e.getPropertyName();

			if ("model".equals(propertyName))
			{
				AxisModel oldModel = (AxisModel) e.getOldValue();
				AxisModel newModel = (AxisModel) e.getNewValue();
				oldModel.removeChangeListener(fModelListener);
				newModel.addChangeListener(fModelListener);
				fScrollbar.repaint();
				fScrollbar.revalidate();
			}
			else if ("orientation".equals(propertyName))
			{
				Integer orient = (Integer) e.getNewValue();

				if (fScrollbar.getComponentOrientation().isLeftToRight())
				{
					if (fIncrButton instanceof BasicArrowButton)
					{
						((BasicArrowButton) fIncrButton).setDirection(
							orient.intValue() == HORIZONTAL ? EAST : SOUTH);
					}
					if (fDecrButton instanceof BasicArrowButton)
					{
						((BasicArrowButton) fDecrButton).setDirection(
							orient.intValue() == HORIZONTAL ? WEST : NORTH);
					}
				}
				else
				{
					if (fIncrButton instanceof BasicArrowButton)
					{
						((BasicArrowButton) fIncrButton).setDirection(
							orient.intValue() == HORIZONTAL ? WEST : SOUTH);
					}
					if (fDecrButton instanceof BasicArrowButton)
					{
						((BasicArrowButton) fDecrButton).setDirection(
							orient.intValue() == HORIZONTAL ? EAST : NORTH);
					}
				}
			}
			else if ("componentOrientation".equals(propertyName))
			{
				ComponentOrientation co = fScrollbar.getComponentOrientation();
				fIncrButton.setComponentOrientation(co);
				fDecrButton.setComponentOrientation(co);

				if (fScrollbar.getOrientation() == JScrollBar.HORIZONTAL)
				{
					if (co.isLeftToRight())
					{
						if (fIncrButton instanceof BasicArrowButton)
						{
							((BasicArrowButton) fIncrButton).setDirection(EAST);
						}
						if (fDecrButton instanceof BasicArrowButton)
						{
							((BasicArrowButton) fDecrButton).setDirection(WEST);
						}
					}
					else
					{
						if (fIncrButton instanceof BasicArrowButton)
						{
							((BasicArrowButton) fIncrButton).setDirection(WEST);
						}
						if (fDecrButton instanceof BasicArrowButton)
						{
							((BasicArrowButton) fDecrButton).setDirection(EAST);
						}
					}
				}

				InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
				SwingUtilities.replaceUIInputMap(fScrollbar,
					JComponent.WHEN_FOCUSED, inputMap);
			}
		}
	}

	/**
	 * Used for scrolling the scrollbar.
	 */
	private static class SharedActionScroller extends AbstractAction
	{
		private int dir;
		private boolean block;

		SharedActionScroller(int dir, boolean block)
		{
			this.dir = dir;
			this.block = block;
		}

		public void actionPerformed(ActionEvent e)
		{
			AxisScrollBar scrollBar = (AxisScrollBar) e.getSource();
			if (dir == NEGATIVE_SCROLL || dir == POSITIVE_SCROLL)
			{
				double amount;
				// Don't use the BasicScrollBarUI.scrollByXXX methods as we
				// don't want to use an invokeLater to reset the trackHighlight
				// via an invokeLater
				if (block)
				{
					if (dir == NEGATIVE_SCROLL)
					{
						amount = -1.0 * scrollBar.getBlockIncrement(-1);
					}
					else
					{
						amount = scrollBar.getBlockIncrement(1);
					}
				}
				else
				{
					if (dir == NEGATIVE_SCROLL)
					{
						amount = -1.0 * scrollBar.getUnitIncrement(-1);
					}
					else
					{
						amount = scrollBar.getUnitIncrement(1);
					}
				}

				double newValue = scrollBar.getValue() + amount;
				
				// Check that new value is in range
				if (newValue < scrollBar.getMinimum())
				{
					newValue = scrollBar.getMinimum();
				}
				else if (newValue > scrollBar.getMaximum())
				{
					newValue = scrollBar.getMaximum();
				}

				scrollBar.setValue(newValue);
			}
			else if (dir == MIN_SCROLL)
			{
				scrollBar.setValue(scrollBar.getMinimum());
			}
			else if (dir == MAX_SCROLL)
			{
				scrollBar.setValue(scrollBar.getMaximum());
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: BasicAxisScrollBarUI.java,v $
//  Revision 1.1  2005/10/11 02:54:48  tames
//  Initial version.
//
//