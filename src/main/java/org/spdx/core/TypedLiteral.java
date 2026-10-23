/**
 * SPDX-FileCopyrightText: 2026 SPDX Contributors
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 */
package org.spdx.core;

import java.util.Objects;

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
