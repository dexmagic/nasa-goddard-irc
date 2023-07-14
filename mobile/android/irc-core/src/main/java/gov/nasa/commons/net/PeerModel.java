//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/net/PeerModel.java,v 1.2 2004/07/12 14:26:23 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log:
//	 1	IRC	   1.0		 1/22/2002 11:18:04 AMTroy Ames
//	$
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

package gov.nasa.gsfc.commons.net;

import java.util.Set;

import net.jxta.peergroup.PeerGroup;

import gov.nasa.gsfc.commons.net.services.DescriptionPublisher;

/**
 * PeerModel provides the interface for manipulating the current state of a
 * peer in the peer-to-peer network. A PeerModel state includes PeerName,
 * joined groups, DescriptionPublisher services available, and root peer group.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2004/07/12 14:26:23 $
 * @author	Troy Ames
**/
public interface PeerModel
{
	/**
	 * The group name delimiter when the parent group hierarchy of a
	 * group is represented by a fully qualified string.
	 * An example of a fully qualified name is <code>A.B.C</code> if
	 * "." is the group delimiter <code>GROUP_DELIM</code>. In this example
	 * group A is the parent of B. B is a subgroup of A and the parent of
	 * group C. The default root peer group is not specified so A in the example
	 * is a subgroup of the root.
	**/
	public static final String GROUP_DELIM = ".";

	/**
	 * Get the peer name of this peer. This name is contained in advertisements
	 * to other peers.
	 *
	 * @return a String peer name of this peer
	**/
	public String getPeerName();

	/**
	 * Set the peer name of this peer. This name is contained in advertisements
	 * to other peers.
	 *
	 * @param name  name of peer
	**/
	public void setPeerName(String name);

	/**
	 * Get the peer discovery limit for this peer. This specifies the upper limit
	 * of discovery responses per remote peer for advertisement discovery.
	 *
	 * @return a upper discovery limit
	**/
	public int getPeerDiscoveryLimit();

	/**
	 * Set the peer discovery limit for this peer. This specifies the upper limit
	 * of discovery responses per remote peer for advertisement discovery.
	 *
	 * @param  limit discovery limit for this peer
	**/
	public void setPeerDiscoveryLimit(int limit);

	/**
	 * Get the root peer group for this peer. Every peer is by default a member
	 * of a root peer group that does not have a parent group.
	 *
	 * @return a PeerGroup that is the root
	**/
	public PeerGroup getRootPeerGroup();

	/**
	 * Set the root peer group for this peer. Every peer is by default a member
	 * of a root peer group. The root peer group that does not have a parent
	 * group.
	 *
	 * @param  peerGroup PeerGroup representing the root peer group
	**/
	public void setRootPeerGroup(PeerGroup peerGroup);

	/**
	 * Add a new group that this peer is a member of. The group name parameter
	 * must be a fully qualified name (see {@link #GROUP_DELIM}).
	 *
	 * @param  groupName name of the group to add
	 * @param  peerGroup PeerGroup representing the group
	 *
	 * @see #GROUP_DELIM
	**/
	public void putJoinedGroup(String groupName, PeerGroup peerGroup);

	/**
	 * Removes a group that this peer was a member of. The group name parameter
	 * must be a fully qualified name (see {@link #GROUP_DELIM}).
	 *
	 * @param  groupName name of the group to remove.
	 * @return the PeerGroup removed, null if it does not exist.
	 *
	 * @see #GROUP_DELIM
	**/
	public PeerGroup removeJoinedGroup(String groupName);

	/**
	 * Get a group with the specified name that this peer is a member of.
	 * The group name parameter
	 * must be a fully qualified name (see {@link #GROUP_DELIM}).
	 *
	 * @param  groupName name of the group
	 * @return a PeerGroup with the given name, null if it does not exist.
	 *
	 * @see #GROUP_DELIM
	**/
	public PeerGroup getJoinedGroup(String groupName);

	/**
	 * Returns true if this peer is a member of the specified group.
	 * The group name parameter
	 * must be a fully qualified name (see {@link #GROUP_DELIM}).
	 *
	 * @param  groupName name of the group
	 * @return true if a member of the group, false otherwise.
	 *
	 * @see #GROUP_DELIM
	**/
	public boolean isJoinedGroup(String groupName);

	/**
	 * Get a set of group names that this peer is a member of.
	 * Each group name in the set will be a fully qualified name
	 * (see {@link #GROUP_DELIM}).
	 *
	 * @return a Set of joined group keys.
	 *
	 * @see #GROUP_DELIM
	**/
	public Set getJoinedGroups();

	/**
	 * Add a link to a DescriptionPublisher service that is associated with
	 * this peer in the specified group. The group name parameter
	 * must be a fully qualified name (see {@link #GROUP_DELIM}).
	 *
	 * @param  groupName   name of the group
	 * @param  descService a DescriptionPublisher service
	 *
	 * @see #GROUP_DELIM
	 * @see gov.nasa.gsfc.commons.net.services.DescriptionPublisher
	**/
	public void putDescriptionService(
		String groupName, DescriptionPublisher descService);

	/**
	 * Removes a DescriptionPublisher service that is associated with
	 * this peer in the specified group. The group name parameter
	 * must be a fully qualified name (see {@link #GROUP_DELIM}).
	 *
	 * @param  groupName   name of the group
	 * @return the removed DescriptionPublisher service associated with the group
	 *
	 * @see #GROUP_DELIM
	 * @see gov.nasa.gsfc.commons.net.services.DescriptionPublisher
	**/
	public DescriptionPublisher removeDescriptionService(String groupName);

	/**
	 * Returns a link to a DescriptionPublisher service that is associated with
	 * this peer in the specified group. The group name parameter
	 * must be a fully qualified name (see {@link #GROUP_DELIM}).
	 *
	 * @param  groupName   name of the group
	 * @return a DescriptionPublisher service associated with the group
	 *
	 * @see #GROUP_DELIM
	 * @see gov.nasa.gsfc.commons.net.services.DescriptionPublisher
	**/
	public DescriptionPublisher getDescriptionService(String groupName);

	/**
	 * Returns true of a DescriptionPublisher service exists that is associated
	 * with this peer in the specified group. The group name parameter
	 * must be a fully qualified name (see {@link #GROUP_DELIM}).
	 *
	 * @param  groupName   name of the group
	 * @return true if the service exists, false otherwise.
	 *
	 * @see #GROUP_DELIM
	 * @see gov.nasa.gsfc.commons.net.services.DescriptionPublisher
	**/
	public boolean isDescriptionServiceStarted(String groupName);
}