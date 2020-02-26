package net.susa.cfs.psl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublicSuffixListTest {
	
	/* 
	 * Location/name of the PSL file. It is requested that we don't download
	 * this more often than once per day, so it's not automatically fetched.
	 */
	private static String pslFile = "public_suffix_list.dat";
	
	private static PublicSuffixList psl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		psl = new PublicSuffixList(pslFile);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testNullFQDN() {
		
		String result = psl.getETLD(null);
		assertEquals("", result);
	}
	
	@Test
	void testEmptyFQDN() {
		
		String result = psl.getETLD("");
		assertEquals("", result);
	}
	
	@Test
	void testOnlyDot() {
		
		String result = psl.getETLD(".");
		assertEquals("", result);
	}
	
	@Test
	void testOnlyDotTLD() {
		
		String result = psl.getETLD(".com");
		assertEquals("com", result);
	}
	
	@Test
	void testDomainDotTLD() {
		
		String result = psl.getETLD("example.com");
		assertEquals("com", result);
	}
	
	@Test
	void testKnownWildcard() {
		
		// PSL defines '*.sch.uk', so test for a correct public suffix result.
		String result = psl.getETLD("x.y.sch.uk");
		assertEquals("y.sch.uk", result);
	}
	
	@Test
	void testBasicLookups() {
		
		/*
		 * An array of String arrays - two elements, the domain path to test, and
		 * the expected response.
		 */
		String[][] basicLookupTests = {
			{ "www.kevin.com", "com"},
			{ "www.kevin.uk", "uk"},
			{ "www.kevin.co.uk", "co.uk"},
			{ "host.domain.schools.nsw.edu.au", "schools.nsw.edu.au"}
		};
			
		/*
		 * Iterate our lookup tests and fail if any results differ from expected.
		 */
		for( String[] test : basicLookupTests ) {

			String fqdn = test[0];
			String expected = test[1];
			
			String etld = psl.getETLD(fqdn);
			assertEquals(expected, etld, "basicLookupTests");
		}
	}
}
