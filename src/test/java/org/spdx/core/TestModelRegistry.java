/*
 * SPDX-FileCopyrightText: Copyright (c) 2024 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 */
package org.spdx.core;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.spdx.storage.IModelStore;
import org.spdx.storage.MockModelStore;

/**
 * Test model registry
 *
 * @author Gary O'Neall
 */
public class TestModelRegistry {
	
	static final String OBJECT_URI = "https://myspdx.docs/objecturi#part1";
	
	MockModelStore modelStore;
	MockCopyManager copyManager;


	@Before
	public void setUp() {
		modelStore = new MockModelStore();
		copyManager = new MockCopyManager();
	}

	/**
	 * Test method for {@link org.spdx.core.ModelRegistry#containsSpecVersion(java.lang.String)}.
	 * @throws InvalidSPDXAnalysisException on error
	 */
	@Test
	public void testAll() throws InvalidSPDXAnalysisException {
		ModelRegistry.getModelRegistry().clearAll();
		assertFalse(ModelRegistry.getModelRegistry().containsSpecVersion("3.0.0"));
		Map<String, Enum<?>> uriToEnum = new HashMap<>();
		uriToEnum.put(MockEnum.ENUM1.getIndividualURI(), MockEnum.ENUM1);
		Map<String, Object> uriToIndividual = new HashMap<>();
		MockIndividual individual = new MockIndividual();
		uriToIndividual.put(individual.getIndividualURI(), individual);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put(MockModelType.TYPE, MockModelType.class);
		ModelRegistry.getModelRegistry().registerModel(new ISpdxModelInfo() {

			@Override
			public Map<String, Enum<?>> getUriToEnumMap() {
				return uriToEnum;
			}

			@Override
			public List<String> getSpecVersions() {
				return Arrays.asList(new String[] {"3.0.0"});
			}

			@Override
			public CoreModelObject createExternalElement(IModelStore store,
					String uri, IModelCopyManager copyManager, Class<?> type,
					String specVersion) throws InvalidSPDXAnalysisException {
				return new MockModelType(store, uri, copyManager, true, specVersion);
			}

			@Override
			public CoreModelObject createModelObject(IModelStore modelStore,
					String objectUri, String type,
					IModelCopyManager copyManager, String specVersion,
					boolean create, String idPrefix) throws InvalidSPDXAnalysisException {
				return new MockModelType(modelStore, objectUri, copyManager, create, specVersion);
			}

			@Override
			public Map<String, Class<?>> getTypeToClassMap() {
				return classMap;
			}

			@Override
			public Object uriToIndividual(String uri, @Nullable Class<?> type) {
				return uriToIndividual.get(uri);
			}

			@Override
			public boolean canBeExternal(Class<?> clazz) {
				return false;
			}
			
		});
		assertTrue(ModelRegistry.getModelRegistry().containsSpecVersion("3.0.0"));
		CoreModelObject result = ModelRegistry.getModelRegistry().inflateModelObject(modelStore, OBJECT_URI, MockModelType.TYPE,
				copyManager, "3.0.0", true, null);
		assertEquals(OBJECT_URI, result.getObjectUri());
		Object oResult = ModelRegistry.getModelRegistry().getExternalElement(modelStore, OBJECT_URI, copyManager, null, "3.0.0");
		assertTrue(oResult instanceof MockModelType);
		assertEquals(OBJECT_URI, ((MockModelType)oResult).getObjectUri());
		Class<?> cResult = ModelRegistry.getModelRegistry().typeToClass(MockModelType.TYPE, "3.0.0");
		assertEquals(MockModelType.class, cResult);
		Enum<?> eResult = ModelRegistry.getModelRegistry().uriToEnum(MockEnum.ENUM1.getIndividualURI(), "3.0.0");
		assertEquals(MockEnum.ENUM1, eResult);
		Object iResult = ModelRegistry.getModelRegistry().uriToIndividual(individual.getIndividualURI(), "3.0.0", null);
		assertEquals(individual.getIndividualURI(), ((MockIndividual)iResult).getIndividualURI());
	}

	@Test
	public void testExtensions() throws InvalidSPDXAnalysisException {
		ModelRegistry.getModelRegistry().clearAll();
		assertFalse(ModelRegistry.getModelRegistry().containsSpecVersion("3.0.0"));
		Map<String, Enum<?>> uriToEnum = new HashMap<>();
		uriToEnum.put(MockEnum.ENUM1.getIndividualURI(), MockEnum.ENUM1);
		Map<String, Object> uriToIndividual = new HashMap<>();
		MockIndividual individual = new MockIndividual();
		uriToIndividual.put(individual.getIndividualURI(), individual);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put(MockModelType.TYPE, MockModelType.class);
		ModelRegistry.getModelRegistry().registerModel(new ISpdxModelInfo() {

			@Override
			public Map<String, Enum<?>> getUriToEnumMap() {
				return uriToEnum;
			}

			@Override
			public List<String> getSpecVersions() {
				return Arrays.asList(new String[] {"3.0.0"});
			}

			@Override
			public CoreModelObject createExternalElement(IModelStore store,
														 String uri, IModelCopyManager copyManager, Class<?> type,
														 String specVersion) throws InvalidSPDXAnalysisException {
				return new MockModelType(store, uri, copyManager, true, specVersion);
			}

			@Override
			public CoreModelObject createModelObject(IModelStore modelStore,
													 String objectUri, String type,
													 IModelCopyManager copyManager, String specVersion,
													 boolean create, String idPrefix) throws InvalidSPDXAnalysisException {
				return new MockModelType(modelStore, objectUri, copyManager, create, specVersion);
			}

			@Override
			public Map<String, Class<?>> getTypeToClassMap() {
				return classMap;
			}

			@Override
			public Object uriToIndividual(String uri, @Nullable Class<?> type) {
				return uriToIndividual.get(uri);
			}

			@Override
			public boolean canBeExternal(Class<?> clazz) {
				return false;
			}

		});

		ModelRegistry.getModelRegistry().registerExtensionType(MockExtension.MOCK_EXTENSION_TYPE, MockExtension.class);
		assertEquals(MockExtension.class,
                ModelRegistry.getModelRegistry().typeToClass(MockExtension.MOCK_EXTENSION_TYPE, "3.0.0"));
		CoreModelObject result = ModelRegistry.getModelRegistry().inflateModelObject(modelStore, "https://my.uri",
				MockExtension.MOCK_EXTENSION_TYPE, copyManager, "3.0.0", true, "prefix");
		assertTrue(result instanceof MockExtension);
		assertEquals(MockExtension.MOCK_EXTENSION_TYPE, result.getType());


	}

	@Test
	public void testDefaultExtensionType() throws InvalidSPDXAnalysisException {
		ModelRegistry registry = ModelRegistry.getModelRegistry();
		registry.clearAll();
		registry.registerModel(new MockModelInfo());
		String customType = "https://example.org/ext/CustomExtension";
		assertNull(registry.typeToClass(customType, "3.0.0"));
		assertNull(registry.getDefaultExtensionType());
		assertEquals(MockExtension.class, registry.registerDefaultExtensionTypeIfAbsent(MockExtension.class));
		// a second registration does not replace the first
		assertEquals(MockExtension.class, registry.registerDefaultExtensionTypeIfAbsent(MockModelType.class));
		assertEquals(MockExtension.class, registry.getDefaultExtensionType());
		// any absolute IRI which is not otherwise known uses the default extension type
		assertEquals(MockExtension.class, registry.typeToClass(customType, "3.0.0"));
		assertEquals(MockExtension.class, registry.typeToClass("urn:example:Custom", "3.0.0"));
		// model types, explicitly registered extensions, and non-IRI types are not affected
		assertEquals(MockModelType.class, registry.typeToClass(MockModelType.TYPE, "3.0.0"));
		registry.registerExtensionType("https://example.org/ext/Registered", MockModelType.class);
		assertEquals(MockModelType.class, registry.typeToClass("https://example.org/ext/Registered", "3.0.0"));
		assertNull(registry.typeToClass("UnknownType", "3.0.0"));
		assertNull(registry.typeToClass("_:b0", "3.0.0"));
		// IRIs in the SPDX namespace are never extension types
		assertNull(registry.typeToClass("https://spdx.org/rdf/3.0.1/terms/Core/Element", "3.0.0"));
		assertNull(registry.typeToClass("https://spdx.org/rdf/3.0.1/terms/Extension/Foo", "3.0.0"));
		assertNull(registry.typeToClass("http://spdx.org/rdf/terms#Package", "3.0.0"));
		try {
			new TypedValue("https://my.uri", "https://spdx.org/rdf/3.0.1/terms/Core/Element", "3.0.0");
			fail("A type in the SPDX namespace must not be accepted");
		} catch (SpdxInvalidTypeException e) {
			// expected
		}
		// SPDX 2 has no extensions
		assertNull(registry.typeToClass(customType, "SPDX-2.3"));
		assertFalse(registry.inflateModelObject(modelStore, "https://my.uri2", customType, copyManager,
				"SPDX-2.3", true, "prefix") instanceof MockExtension);
		// the mock extension reports a fixed type which must be known to create it
		registry.registerExtensionType(MockExtension.MOCK_EXTENSION_TYPE, MockExtension.class);
		CoreModelObject result = registry.inflateModelObject(modelStore, "https://my.uri", customType,
				copyManager, "3.0.0", true, "prefix");
		assertTrue(result instanceof MockExtension);
		assertFalse(registry.canBeExternal(MockExtension.class, "3.0.0"));
		registry.clearAll();
		assertNull(registry.getDefaultExtensionType());
		registry.registerModel(new MockModelInfo());
	}

}
