/**
 * SPDX-FileCopyrightText: 2026 SPDX Contributors
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 */
package org.spdx.core;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable literal value with an explicit datatype
 * <p>
 * Used for property values whose datatype is not known from the SPDX model,
 * for example a property defined by an extension, written in JSON-LD as
 * <code>{"@value": "0.35", "@type": "http://www.w3.org/2001/XMLSchema#decimal"}</code>.
 * The lexical form and the datatype IRI are kept as given, so the value can
 * be serialized again without loss.
 * </p>
 */
public final class TypedLiteral {

	/**
	 * IRI of the XML Schema integer datatype, used for integers too large for an Integer
	 */
	public static final String XSD_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";

	/**
	 * IRIs of xsd:integer and the XML Schema datatypes derived from it
	 */
	public static final Set<String> XSD_INTEGER_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			XSD_INTEGER,
			"http://www.w3.org/2001/XMLSchema#nonNegativeInteger",
			"http://www.w3.org/2001/XMLSchema#positiveInteger",
			"http://www.w3.org/2001/XMLSchema#nonPositiveInteger",
			"http://www.w3.org/2001/XMLSchema#negativeInteger",
			"http://www.w3.org/2001/XMLSchema#long",
			"http://www.w3.org/2001/XMLSchema#int",
			"http://www.w3.org/2001/XMLSchema#short",
			"http://www.w3.org/2001/XMLSchema#byte",
			"http://www.w3.org/2001/XMLSchema#unsignedLong",
			"http://www.w3.org/2001/XMLSchema#unsignedInt",
			"http://www.w3.org/2001/XMLSchema#unsignedShort",
			"http://www.w3.org/2001/XMLSchema#unsignedByte")));

	/**
	 * @return the value as a BigInteger if the datatype is xsd:integer or derived from it
	 * @throws NumberFormatException if the datatype is an integer type but the lexical form is not an integer
	 * @throws IllegalStateException if the datatype is not an integer type
	 */
	public BigInteger integerValue() {
		if (!XSD_INTEGER_TYPES.contains(datatype)) {
			throw new IllegalStateException("Datatype " + datatype + " is not an integer type");
		}
		// XML Schema collapses whitespace in integer literals
		return new BigInteger(value.trim());
	}

	private final String value;
	private final String datatype;

	/**
	 * @param value lexical form of the literal
	 * @param datatype IRI of the datatype of the literal
	 */
	public TypedLiteral(String value, String datatype) {
		Objects.requireNonNull(value, "Value can not be null");
		Objects.requireNonNull(datatype, "Datatype can not be null");
		this.value = value;
		this.datatype = datatype;
	}

	/**
	 * @return the lexical form of the literal
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the IRI of the datatype of the literal
	 */
	public String getDatatype() {
		return datatype;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof TypedLiteral)) {
			return false;
		}
		TypedLiteral other = (TypedLiteral) o;
		return value.equals(other.value) && datatype.equals(other.datatype);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, datatype);
	}

	@Override
	public String toString() {
		return "\"" + value + "\"^^<" + datatype + ">";
	}
}
