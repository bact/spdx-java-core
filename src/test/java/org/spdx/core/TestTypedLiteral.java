/**
 * SPDX-FileCopyrightText: 2026 SPDX Contributors
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 */
package org.spdx.core;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test typed literal
 */
public class TestTypedLiteral {

	static final String XSD_DECIMAL = "http://www.w3.org/2001/XMLSchema#decimal";
	static final String XSD_DATE_TIME = "http://www.w3.org/2001/XMLSchema#dateTime";

	@Test
	public void testGetters() {
		TypedLiteral literal = new TypedLiteral("0.35", XSD_DECIMAL);
		assertEquals("0.35", literal.getValue());
		assertEquals(XSD_DECIMAL, literal.getDatatype());
	}

	@Test
	public void testEqualsAndHashCode() {
		TypedLiteral literal = new TypedLiteral("0.35", XSD_DECIMAL);
		TypedLiteral same = new TypedLiteral("0.35", XSD_DECIMAL);
		assertEquals(literal, same);
		assertEquals(literal.hashCode(), same.hashCode());
		// the lexical form is significant
		assertNotEquals(literal, new TypedLiteral("0.350", XSD_DECIMAL));
		assertNotEquals(literal, new TypedLiteral("0.35", XSD_DATE_TIME));
		assertNotEquals(literal, "0.35");
	}

	@Test
	public void testRequiredFields() {
		try {
			new TypedLiteral(null, XSD_DECIMAL);
			fail("Null value should not be allowed");
		} catch (NullPointerException e) {
			// expected
		}
		try {
			new TypedLiteral("0.35", null);
			fail("Null datatype should not be allowed");
		} catch (NullPointerException e) {
			// expected
		}
	}

	@Test
	public void testToString() {
		assertEquals("\"0.35\"^^<" + XSD_DECIMAL + ">", new TypedLiteral("0.35", XSD_DECIMAL).toString());
	}
}
