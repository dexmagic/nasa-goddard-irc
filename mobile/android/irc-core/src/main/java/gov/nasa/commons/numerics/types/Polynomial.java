//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/types/Polynomial.java,v 1.2 2004/07/12 14:26:24 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: Polynomial.java,v $
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
//	
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

package gov.nasa.gsfc.commons.numerics.types;


/**
 *  A Polynomial represents properties of and operations on polynomial
 *  equations; that is, equations of the form:
 *  <P> a[0] + a[1]x + a[2]x^[2] ... a[n-1]x^[n-1].
 *
 *  <P>This code is based on that found in Scott Robert Ladd's
 *  <i>Java Algoriths</i> (McGraw Hill, 1997), pp. 43-47, with modifications
 *  and adaptations (esp. in terminology) after Thomas H. Cormen, et al.,
 *  <i>Introduction to Algorithms</i> (McGraw Hill, 1991), pp. 776-82.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version $Date: 2004/07/12 14:26:24 $
 *	@author Carl Hostetter
**/

abstract public class Polynomial
{
	/**
	 *  A coefficient representation of a polynomial of degree-bound n:
	 *  <P>
	 *  A(x) = SUM[j=0->n-1] a[j]x^[j]
	 *
	 * <P> is a set of coefficients a = (a[o], a[1], ..., a[n-1])
	**/

	protected Object[] fCoefficients = null;	// The set of coefficients of
												// this Polynomial.
	protected int fDegreeBound = 0; // The number of coefficients of this
									// Polynomial.


	/**
	 *  Constructs a Polynomial having the given degree-bound (i.e., number of
	 *  coefficients).
	 *
	 *  @param degreeBound The number of coefficients of the new Polynomial
	**/

	public Polynomial(int degreeBound)
	{
		fDegreeBound = degreeBound;

		initializeCoefficients(fDegreeBound);

		if (fDegreeBound <= 0)
		{
			String detail = "Polynomial constructor Polynomial(int) was " +
				"passed an illegal argument: the number of coefficients must " +
				"be greater than or equal to 1.";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Constructs a Polynomial having the given set of coefficients.
	 *
	 *  @param coefficients The Set of coefficients of the new Polynomial
	**/

	public Polynomial(Object[] coefficients)
	{
		fDegreeBound = coefficients.length;

		initializeCoefficients(fDegreeBound, coefficients);

		if (fDegreeBound > 0)
		{
			fCoefficients = (Object[]) coefficients.clone();
		}
		else
		{
			String detail = "Polynomial constructor Polynomial(Set) was " +
				"passed an illegal argument: the number of coefficients must " +
				"be greater than or equal to 1.";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Constructs a Polynomial having the same set of coefficients as the
	 *  given Polynomial.
	 *
	 *  @param polynomial A Polynomial
	**/

	public Polynomial(Polynomial polynomial)
	{
		this(polynomial.getDegreeBound());

		initializeCoefficients(fDegreeBound, polynomial.fCoefficients);
	}


	/**
	 *  Causes this Polynomial to initialize its coefficients to a set of
	 *  appropriately-typed coefficients having the given degree-bound (i.e.,
	 *  number of coefficients).
	 *
	 *  @param degreeBound The size of the new set of coefficients
	**/

	abstract protected void initializeCoefficients(int degreeBound);


	/**
	 *  Causes this Polynomial to initialize its coefficients to a set of
	 *  appropriately-typed coefficients having the given degree-bound (i.e.,
	 *  number of coefficients) and the same values as the given set of
	 *  coefficients.
	 *
	 *  @param coefficients A set of coefficients
	**/

	protected void initializeCoefficients(int degreeBound,
		Object[] coefficients)
	{
		fCoefficients = new Object[degreeBound];
		System.arraycopy(coefficients, 0, fCoefficients, 0,
			coefficients.length);
	}


	/**
	 *  Returns the number of coefficients of this Polynomial.
	 *
	 *  @return The number of coefficients of this Polynomial
	**/

	public int getDegreeBound()
	{
		return (fDegreeBound);
	}


	/**
	 *  Returns the number of coefficients of this Polynomial.
	 *
	 *  @return The number of coefficients of this Polynomial
	**/

	public int getNumCoefficients()
	{
		return (fDegreeBound);
	}


	/**
	 *  Causes this Polynomial to "grow" to accomodate the given number of
	 *  coefficients.
	 *
	 *  <P>Note that coefficient indices start at 0, and that the highest
	 *  order coefficient of a Polynomial having n coefficients is at index
	 *  n - 1.
	 *
	 *  @param newDegreeBound The new number of coefficients for this
	 *	  Polynomial
	**/

	public void grow(int newDegreeBound)
	{
		if (newDegreeBound > fDegreeBound)
		{
			fDegreeBound = newDegreeBound;
			initializeCoefficients(newDegreeBound, fCoefficients);
		}
	}


	/**
	 *  Causes this Polynomial to increase its degree-bound to the next-highest
	 *  power of two.
	 *
	**/

	public void increaseDegreeBoundToNextPowerOfTwo()
	{
		int currentDegreeBound = getDegreeBound();
		int newDegreeBound = 1;

		while (newDegreeBound < currentDegreeBound)
		{
			newDegreeBound <<= 1;

			if (newDegreeBound == 0)
			{
				String detail = "Polynomial expansion failed";

				throw (new IllegalArgumentException(detail));
			}
		}

		newDegreeBound <<= 1;

		this.grow(newDegreeBound);
	}


	/**
	 *  Negates this Polynomial by negating each of its coefficients.
	 *
	**/

	abstract public void negate();


	/**
	 *  Adds the given target Polynomial to this Polynomial.
	 *
	 *  @param target A Polynomial
	**/

	abstract public void add(Polynomial target);


	/**
	 *  Subtracts the given target Polynomial from this Polynomial.
	 *
	 *  @param target A Polynomial
	**/

	abstract public void subtract(Polynomial target);


	/**
	 *  Multiplies this Polynomial by the given target Polynomial.
	 *
	 *  @param target A Polynomial
	**/

	abstract public void multiply(Polynomial target);
}
