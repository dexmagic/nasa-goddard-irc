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

package gov.nasa.gsfc.irc.gui.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollButton;

import gov.nasa.gsfc.irc.gui.swing.plaf.basic.BasicAxisScrollBarUI;

/**
 * Implementation of AxisScrollBarUI for the Metal Look and Feel. This class 
 * was adapted from the <code>javax.swing.plaf.metal.MetalScrollBarUI</code> 
 * class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 02:54:48 $
 * @author 	Troy Ames
 */
public class MetalAxisScrollBarUI extends BasicAxisScrollBarUI
{
	private static Color fShadowColor;
	private static Color fHighlightColor;
	private static Color fDarkShadowColor;
	private static Color fThumbColor;
	private static Color fThumbShadow;
	private static Color fThumbHighlightColor;

	protected MetalBumps fBumps;

	protected MetalScrollButton fIncreaseButton;
	protected MetalScrollButton fDecreaseButton;

	protected int fScrollBarWidth;

	public static final String FREE_STANDING_PROP = "JScrollBar.isFreeStanding";
	protected boolean fIsFreeStanding = true;

	public static ComponentUI createUI(JComponent c)
	{
		return new MetalAxisScrollBarUI();
	}

	protected void installDefaults()
	{
		fScrollBarWidth = ((Integer) (UIManager.get("ScrollBar.width")))
				.intValue();
		super.installDefaults();
		fBumps = new MetalBumps(10, 10, fThumbHighlightColor, fThumbShadow,
				fThumbColor);
	}

	protected void installListeners()
	{
		super.installListeners();
		((ScrollBarListener) fPropertyChangeListener)
				.handlePropertyChange(fScrollbar
						.getClientProperty(FREE_STANDING_PROP));
	}

	protected PropertyChangeListener createPropertyChangeListener()
	{
		return new ScrollBarListener();
	}

	protected void configureScrollBarColors()
	{
		super.configureScrollBarColors();
		fShadowColor = UIManager.getColor("ScrollBar.shadow");
		fHighlightColor = UIManager.getColor("ScrollBar.highlight");
		fDarkShadowColor = UIManager.getColor("ScrollBar.darkShadow");
		fThumbColor = UIManager.getColor("ScrollBar.thumb");
		fThumbShadow = UIManager.getColor("ScrollBar.thumbShadow");
		fThumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");

	}

	public Dimension getPreferredSize(JComponent c)
	{
		if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
		{
			return new Dimension(fScrollBarWidth, fScrollBarWidth * 3 + 10);
		}
		else
		// Horizontal
		{
			return new Dimension(fScrollBarWidth * 3 + 10, fScrollBarWidth);
		}

	}

	/** Returns the view that represents the decrease view. 
	 */
	protected JButton createDecreaseButton(int orientation)
	{
		fDecreaseButton = new MetalScrollButton(orientation, fScrollBarWidth,
				fIsFreeStanding);
		return fDecreaseButton;
	}

	/** Returns the view that represents the increase view. */
	protected JButton createIncreaseButton(int orientation)
	{
		fIncreaseButton = new MetalScrollButton(orientation, fScrollBarWidth,
				fIsFreeStanding);
		return fIncreaseButton;
	}

	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
	{
		g.translate(trackBounds.x, trackBounds.y);

		boolean leftToRight = MetalUtils.isLeftToRight(c);

		if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
		{
			if (!fIsFreeStanding)
			{
				if (!leftToRight)
				{
					trackBounds.width += 1;
					g.translate(-1, 0);
				}
				else
				{
					trackBounds.width += 2;
				}
			}

			if (c.isEnabled())
			{
				g.setColor(fDarkShadowColor);
				g.drawLine(0, 0, 0, trackBounds.height - 1);
				g.drawLine(trackBounds.width - 2, 0, trackBounds.width - 2,
					trackBounds.height - 1);
				g.drawLine(2, trackBounds.height - 1, trackBounds.width - 1,
					trackBounds.height - 1);
				g.drawLine(2, 0, trackBounds.width - 2, 0);

				g.setColor(fShadowColor);
				//	g.setColor( Color.red);
				g.drawLine(1, 1, 1, trackBounds.height - 2);
				g.drawLine(1, 1, trackBounds.width - 3, 1);
				if (fScrollbar.getValue() != fScrollbar.getMaximum())
				{ // thumb shadow
					int y = fThumbRect.y + fThumbRect.height - trackBounds.y;
					g.drawLine(1, y, trackBounds.width - 1, y);
				}
				g.setColor(fHighlightColor);
				g.drawLine(trackBounds.width - 1, 0, trackBounds.width - 1,
					trackBounds.height - 1);
			}
			else
			{
				MetalUtils.drawDisabledBorder(g, 0, 0, trackBounds.width,
					trackBounds.height);
			}

			if (!fIsFreeStanding)
			{
				if (!leftToRight)
				{
					trackBounds.width -= 1;
					g.translate(1, 0);
				}
				else
				{
					trackBounds.width -= 2;
				}
			}
		}
		else
		// HORIZONTAL
		{
			if (!fIsFreeStanding)
			{
				trackBounds.height += 2;
			}

			if (c.isEnabled())
			{
				g.setColor(fDarkShadowColor);
				g.drawLine(0, 0, trackBounds.width - 1, 0); // top
				g.drawLine(0, 2, 0, trackBounds.height - 2); // left
				g.drawLine(0, trackBounds.height - 2, trackBounds.width - 1,
					trackBounds.height - 2); // bottom
				g.drawLine(trackBounds.width - 1, 2, trackBounds.width - 1,
					trackBounds.height - 1); // right

				g.setColor(fShadowColor);
				//	g.setColor( Color.red);
				g.drawLine(1, 1, trackBounds.width - 2, 1); // top
				g.drawLine(1, 1, 1, trackBounds.height - 3); // left
				g.drawLine(0, trackBounds.height - 1, trackBounds.width - 1,
					trackBounds.height - 1); // bottom
				if (fScrollbar.getValue() != fScrollbar.getMaximum())
				{ // thumb shadow
					int x = fThumbRect.x + fThumbRect.width - trackBounds.x;
					g.drawLine(x, 1, x, trackBounds.height - 1);
				}
			}
			else
			{
				MetalUtils.drawDisabledBorder(g, 0, 0, trackBounds.width,
					trackBounds.height);
			}

			if (!fIsFreeStanding)
			{
				trackBounds.height -= 2;
			}
		}

		g.translate(-trackBounds.x, -trackBounds.y);
	}

	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
	{
		if (!c.isEnabled())
		{
			return;
		}

		boolean leftToRight = MetalUtils.isLeftToRight(c);

		g.translate(thumbBounds.x, thumbBounds.y);

		if (fScrollbar.getOrientation() == JScrollBar.VERTICAL)
		{
			if (!fIsFreeStanding)
			{
				if (!leftToRight)
				{
					thumbBounds.width += 1;
					g.translate(-1, 0);
				}
				else
				{
					thumbBounds.width += 2;
				}

			}

			g.setColor(fThumbColor);
			g.fillRect(0, 0, thumbBounds.width - 2, thumbBounds.height - 1);

			g.setColor(fThumbShadow);
			g.drawRect(0, 0, thumbBounds.width - 2, thumbBounds.height - 1);

			g.setColor(fThumbHighlightColor);
			g.drawLine(1, 1, thumbBounds.width - 3, 1);
			g.drawLine(1, 1, 1, thumbBounds.height - 2);

			fBumps.setBumpArea(thumbBounds.width - 6, thumbBounds.height - (2 * (fScalarSize + 2)));
			fBumps.paintIcon(c, g, 3, fScalarSize + 2);

			g.setColor(fThumbShadow);
			g.drawLine(0, fScalarSize, thumbBounds.width - 2, fScalarSize);
			g.drawLine(
				0, thumbBounds.height - fScalarSize - 1,
				thumbBounds.width - 2, thumbBounds.height - fScalarSize - 1);

			if (!fIsFreeStanding)
			{
				if (!leftToRight)
				{
					thumbBounds.width -= 1;
					g.translate(1, 0);
				}
				else
				{
					thumbBounds.width -= 2;
				}
			}
		}
		else
		// HORIZONTAL
		{
			if (!fIsFreeStanding)
			{
				thumbBounds.height += 2;
			}

			g.setColor(fThumbColor);
			g.fillRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 2);

			g.setColor(fThumbShadow);
			g.drawRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 2);
			g.drawLine(5, 0, 5, thumbBounds.height - 2);

			g.setColor(fThumbHighlightColor);
			g.drawLine(1, 1, thumbBounds.width - 3, 1);
			g.drawLine(1, 1, 1, thumbBounds.height - 3);

			fBumps.setBumpArea(thumbBounds.width - (2 * (fScalarSize + 2)), thumbBounds.height - 6);
			fBumps.paintIcon(c, g, fScalarSize + 2, 3);

			g.setColor(fThumbShadow);
			g.drawLine(fScalarSize, 0, fScalarSize, thumbBounds.height - 2);
			g.drawLine(
				thumbBounds.width - fScalarSize - 1, 0, 
				thumbBounds.width - fScalarSize - 1, thumbBounds.height - 2);

			if (!fIsFreeStanding)
			{
				thumbBounds.height -= 2;
			}
		}

		g.translate(-thumbBounds.x, -thumbBounds.y);
	}

	protected Dimension getMinimumThumbSize()
	{
		int thumbSize = fScrollBarWidth + fScalarSize;
		return new Dimension(thumbSize, thumbSize);
	}

	/**
	 * This is overridden only to increase the invalid area.  This
	 * ensures that the "Shadow" below the thumb is invalidated
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
		fScrollbar.repaint(minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
	}

	class ScrollBarListener extends BasicAxisScrollBarUI.PropertyChangeHandler
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String name = e.getPropertyName();
			if (name.equals(FREE_STANDING_PROP))
			{
				handlePropertyChange(e.getNewValue());
			}
			else
			{
				super.propertyChange(e);
			}
		}

		public void handlePropertyChange(Object newValue)
		{
			if (newValue != null)
			{
				boolean temp = ((Boolean) newValue).booleanValue();
				boolean becameFlush = temp == false && fIsFreeStanding == true;
				boolean becameNormal = temp == true && fIsFreeStanding == false;

				fIsFreeStanding = temp;

				if (becameFlush)
				{
					toFlush();
				}
				else if (becameNormal)
				{
					toFreeStanding();
				}
			}
			else
			{

				if (!fIsFreeStanding)
				{
					fIsFreeStanding = true;
					toFreeStanding();
				}

				// This commented-out block is used for testing flush scrollbars.
				/*
				 if ( isFreeStanding ) {
				 isFreeStanding = false;
				 toFlush();
				 }
				 */
			}

			if (fIncreaseButton != null)
			{
				fIncreaseButton.setFreeStanding(fIsFreeStanding);
			}
			if (fDecreaseButton != null)
			{
				fDecreaseButton.setFreeStanding(fIsFreeStanding);
			}
		}

		protected void toFlush()
		{
			fScrollBarWidth -= 2;
		}

		protected void toFreeStanding()
		{
			fScrollBarWidth += 2;
		}
	} // end class ScrollBarListener
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MetalAxisScrollBarUI.java,v $
//  Revision 1.1  2005/10/11 02:54:48  tames
//  Initial version.
//
//