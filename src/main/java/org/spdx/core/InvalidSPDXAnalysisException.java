/**
 * Copyright (c) 2019 Source Auditor Inc.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.spdx.core;

/**
 * Exception for invalid SPDX Documents
 * 
 * @author Gary O'Neall
 *
 */
public class InvalidSPDXAnalysisException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidSPDXAnalysisException() {
	}

	public InvalidSPDXAnalysisException(String arg0) {
		super(arg0);
	}

	public InvalidSPDXAnalysisException(Throwable arg0) {
		super(arg0);
	}
	
	public InvalidSPDXAnalysisException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidSPDXAnalysisException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
