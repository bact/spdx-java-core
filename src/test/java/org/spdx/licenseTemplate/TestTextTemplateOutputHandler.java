/**
 * SPDX-FileCopyrightText: Copyright (c) 2013 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 */
package org.spdx.licenseTemplate;

import static org.junit.Assert.*;

import org.junit.Test;
import org.spdx.licenseTemplate.LicenseTemplateRule.RuleType;

/**
 * Test text template output handler
 *
 * @author Source Auditor
 */
public class TestTextTemplateOutputHandler {

	/**
	 * Test method for {@link org.spdx.licenseTemplate.TextTemplateOutputHandler#text(java.lang.String)}.
	 */
	@Test
	public void testNormalText() {
		String test = "test normal\n";
		TextTemplateOutputHandler oh = new TextTemplateOutputHandler();
		oh.text(test);
		assertEquals(test, oh.getText());
	}

	/**
	 * Test method for {@link org.spdx.licenseTemplate.TextTemplateOutputHandler#variableRule(org.spdx.licenseTemplate.LicenseTemplateRule)}.
	 * @throws LicenseTemplateRuleException
	 */
	@Test
	public void testVariableRule() throws LicenseTemplateRuleException {
		String ruleName = "testRule";
		String originalText = "Original \\ntext";
		String compareOriginalText = "Original \ntext";
		String matchText = "match text";
		String exampleText = "Example \\n text";
		LicenseTemplateRule normalRule = new LicenseTemplateRule(ruleName, RuleType.VARIABLE,
				originalText, matchText, exampleText);
		TextTemplateOutputHandler oh = new TextTemplateOutputHandler();
		oh.variableRule(normalRule);
		assertEquals(compareOriginalText, oh.getText());
	}

	/**
	 * Test method for {@link org.spdx.licenseTemplate.TextTemplateOutputHandler#beginOptional(org.spdx.licenseTemplate.LicenseTemplateRule)}.
	 * @throws LicenseTemplateRuleException
	 */
	@Test
	public void testBeginOptional() throws LicenseTemplateRuleException {
		String ruleName = "testRule";
		String originalText = "Original \\ntext";
		String matchText = "match text";
		String exampleText = "Example \\n text";
		LicenseTemplateRule normalRule = new LicenseTemplateRule(ruleName, RuleType.BEGIN_OPTIONAL,
				originalText, matchText, exampleText);
		TextTemplateOutputHandler oh = new TextTemplateOutputHandler();
		oh.beginOptional(normalRule);
		assertEquals("", oh.getText());
	}

	/**
	 * Test method for {@link org.spdx.licenseTemplate.TextTemplateOutputHandler#endOptional(org.spdx.licenseTemplate.LicenseTemplateRule)}.
	 * @throws LicenseTemplateRuleException
	 */
	@Test
	public void testEndOptional() throws LicenseTemplateRuleException {
		String ruleName = "testRule";
		String originalText = "Original \\ntext";
		String matchText = "match text";
		String exampleText = "Example \\n text";
		LicenseTemplateRule normalRule = new LicenseTemplateRule(ruleName, RuleType.END_OPTIONAL,
				originalText, matchText, exampleText);
		TextTemplateOutputHandler oh = new TextTemplateOutputHandler();
		oh.endOptional(normalRule);
		assertEquals("", oh.getText());
	}

	/**
	 * Test method for {@link org.spdx.licenseTemplate.TextTemplateOutputHandler#getText()}.
	 * @throws LicenseTemplateRuleException
	 */
	@Test
	public void testGetText() throws LicenseTemplateRuleException {
		String normalText = "Normal text";
		TextTemplateOutputHandler oh = new TextTemplateOutputHandler();
		assertEquals("", oh.getText());
		String ruleName = "testRule";
		String originalText = "Original \\ntext";
		String matchText = "match text";
		String exampleText = "Example \\n text";
		LicenseTemplateRule beginRule = new LicenseTemplateRule(ruleName, RuleType.BEGIN_OPTIONAL,
				originalText, matchText, exampleText);
		LicenseTemplateRule endRule = new LicenseTemplateRule(ruleName, RuleType.END_OPTIONAL,
				originalText, matchText, exampleText);
		oh.beginOptional(beginRule);
		oh.text(normalText);
		oh.endOptional(endRule);
		assertEquals(normalText, oh.getText());
	}

}
