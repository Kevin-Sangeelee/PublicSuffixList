package net.susa.cfs.psl;

/*
 * The PublicSuffixList helper class provides a way to identify an effective
 * Top Level Domain (TLD) from a fully qualified domain name according to the
 * Public Suffix List.
 * 
 * For example, 'uk' is a TLD, but many domains are registed under an effective
 * TLD (eTLD) of 'co.uk' or 'org.uk'. There are many others, and there's no
 * algorithmic way to determine what parts of a FQDN should be considered the
 * top level.
 * 
 *  The Public Suffix List (PSL) maintains a list of such eTLDs, and this class
 *  stores the PSL in a tree structure so that FQDNs can be checked quickly
 *  to identify any associated eTLD.
 *  
 *  See https://publicsuffix.org/learn/ for more details.
 *  
 *  Download the latest list from: -
 *  
 *    https://github.com/publicsuffix/list/raw/master/public_suffix_list.dat
 * 
 * (C)2020 Kevin Sangeelee
 * 
 * Released under the terms of the GNU GPL Version 2.
 * See http://www.gnu.org/licenses/gpl-2.0.html for full terms.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PublicSuffixList {

	private PslNode root = new PslNode("ROOT");

	/**
	 * Constructs a PublicSuffixList object from the file given.
	 * 
	 * @param filePath
	 *            - path to the file containing the Public Suffix List
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public PublicSuffixList(String filePath) throws FileNotFoundException, IOException {

		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);

		this.populateTree(br);

		br.close();
	}

	private void populateTree(BufferedReader br) throws IOException {

		root = new PslNode("ROOT");

		String domain;

		while ((domain = br.readLine()) != null) {

			// Ignore comments.
			if (domain.startsWith("//"))
				continue;
			// Ignore lines without '.' periods.
			if (domain.contains(".") == false) {
				continue;
			}

			String[] split = domain.split("\\.", 10);
			List<String> asList = Arrays.asList(split);
			LinkedList<String> domainSplit = new LinkedList<String>(asList);
			Collections.reverse(domainSplit);

			PslNode here = root;

			for (String domainBit : domainSplit) {
				// Find each domain bit, starting from the root.
				// If the name matches and there's something more to add
				// then add it to the node.

				PslNode target = here.findChild(domainBit);

				if (target == null) {
					PslNode newChild = new PslNode(domainBit);
					here.addChild(newChild);
					here = newChild;
					// optimisation: just add remaining domainBits.
				} else {
					here = target;
				}
			}
		}
	}

	/**
	 * Given a fully qualified domain name, return the effective TLD. If there is no
	 * entry in the Public Suffix List, then the usual TLD (e.g. com) is returned.
	 * Anything invalid will return the empty String to denote that no part of the
	 * FQDN can be considered a TLD.
	 */
	public String getETLD(String fqdn) {

		LinkedList<String> result = new LinkedList<String>();

		if (fqdn != null) {
			LinkedList<String> domainParts = new LinkedList<String>(Arrays.asList(fqdn.split("\\.", 10)));
			Collections.reverse(domainParts);

			// Iterate e.g. 'uk', 'co', 'mydomain', 'myhost'

			PslNode current = root;

			for (String part : domainParts) {

				PslNode child = current.findChild(part);

				if (child == null) {
					break;
				}

				current = child;
				result.push(child.getName());
			}
		}

		return String.join(".", result); // just for now
	}
}
