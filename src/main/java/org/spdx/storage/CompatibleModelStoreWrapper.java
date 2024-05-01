/**
 * Copyright (c) 2023 Source Auditor Inc.
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
package org.spdx.storage;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.spdx.core.IExternalElementInfo;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.core.ModelRegistryException;
import org.spdx.core.SpdxCoreConstants;
import org.spdx.core.SpdxInvalidIdException;
import org.spdx.core.SpdxInvalidTypeException;
import org.spdx.core.TypedValue;

/**
 * Wraps a model store providing a compatible interface to the 1.X version of the SPDX Java Library
 * 
 * @author Gary O'Neall
 *
 */
public class CompatibleModelStoreWrapper implements IModelStore {
	
	public static final String LATEST_SPDX_2X_VERSION = "SPDX-2.3";
	
	private IModelStore baseStore;
	
	public CompatibleModelStoreWrapper(IModelStore baseStore) {
		Objects.requireNonNull(baseStore, "A base store must be provided for the CompatibileModelStoreWrapper");
		this.baseStore = baseStore;
	}

	@Override
	public void close() throws Exception {
		baseStore.close();
	}

	/**
	 * @param documentUri a nameSpace for the ID
	 * @param objectUri unique ID within the SPDX document
	 * @return true if the objectUri already exists for the documentUri
	 */
	public boolean exists(String documentUri, String id) {
		return exists(documentUriIdToUri(documentUri, id, baseStore));
	}
	
	/**
	 * @param documentUri SPDX v2 Document URI
	 * @param id ID consistent with SPDX v2 spec
	 * @param store store used for the Document URI
	 * @return true if the objectUri already exists for the documentUri
	 */
	public static String documentUriIdToUri(String documentUri, String id, IModelStore store) {
		return documentUriIdToUri(documentUri, id, store.getIdType(id).equals(IdType.Anonymous));
	}
	
	public static String documentUriToNamespace(String documentUri, boolean anonymous) {
		if (anonymous) {
			return "";
		} else if (documentUri.contains("://spdx.org/licenses/"))  {
			return documentUri;
		} else {
			return documentUri + "#";
		}
	}
	
	/**
	 * @param documentUri SPDX v2 Document URI
	 * @param id ID consistent with SPDX v2 spec
	 * @param anonymous true of this is an anonymous ID
	 * @return a URI based on the document URI and ID - if anonymous is true, the ID is returned
	 */
	public static String documentUriIdToUri(String documentUri, String id, boolean anonymous) {
		return documentUriToNamespace(documentUri, anonymous) + id;
	}
	
	/**
	 * Convenience method to convert an SPDX 2.X style typed value to the current TypedValue
	 * @param documentUri SPDX v2 Document URI
	 * @param id ID consistent with SPDX v2 spec
	 * @param anonymous true of this is an anonymous ID
	 * @param type SPDX type
	 * @return TypedValue with the proper Object URI formed by the documentUri and ID
	 * @throws SpdxInvalidIdException
	 * @throws SpdxInvalidTypeException
	 * @throws ModelRegistryException 
	 */
	public static TypedValue typedValueFromDocUri(String documentUri, String id, boolean anonymous, String type) throws SpdxInvalidIdException, SpdxInvalidTypeException, ModelRegistryException {
		return new TypedValue(documentUriIdToUri(documentUri, id, anonymous), type, LATEST_SPDX_2X_VERSION);
	}
	
	/**
	 * @param store Store storing the objet URI
	 * @param objectUri Object URI
	 * @param documentUri SPDX 2 document URI for the ID
	 * @return the SPDX 2 compatible ID
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static String objectUriToId(IModelStore store, String objectUri, String documentUri) throws InvalidSPDXAnalysisException {
		return objectUriToId(store.getIdType(objectUri) == IdType.Anonymous, objectUri, documentUri);
	}
	
	/**
	 * @param anon true if the ID type is anonymous
	 * @param objectUri Object URI
	 * @param documentUri SPDX 2 document URI for the ID
	 * @return the SPDX 2 compatible ID
	 * @throws InvalidSPDXAnalysisException 
	 */
	public static String objectUriToId(boolean anon, String objectUri, String documentUri) throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(objectUri, "Object URI can not be null");
		if (anon) {
			return objectUri;
		}
		if (objectUri.startsWith(SpdxCoreConstants.LISTED_LICENSE_URL)) {
			return objectUri.substring(SpdxCoreConstants.LISTED_LICENSE_URL.length());
		}
		Objects.requireNonNull(documentUri, "Document URI can not be null");
		String nameSpace = documentUri + "#";
		if (!objectUri.startsWith(nameSpace)) {
			throw new InvalidSPDXAnalysisException("Object URI must start with document URI + #.  DocumentUri: " +
						documentUri + ", Object URI: "+objectUri);
		}
		return objectUri.substring(nameSpace.length());
	}

	@Override
	public boolean exists(String uri) {
		return baseStore.exists(uri);
	}
	
	/**
	 * @param documentUri SPDX v2 spec document URI
	 * @param objectUri SPDX ID
	 * @param type type
	 * @throws InvalidSPDXAnalysisException
	 */
	public void create(String documentUri, String id, String type)
			throws InvalidSPDXAnalysisException {
		baseStore.create(
				new TypedValue(documentUriIdToUri(documentUri, id, baseStore), type, LATEST_SPDX_2X_VERSION));
	}
	
	@Override
	public void create(TypedValue typedValue) throws InvalidSPDXAnalysisException {
		baseStore.create(typedValue);
	}

	@Override
	public List<PropertyDescriptor> getPropertyValueDescriptors(
			String objectUri) throws InvalidSPDXAnalysisException {
		return baseStore.getPropertyValueDescriptors(objectUri);
	}
	
	public List<PropertyDescriptor> getPropertyValueDescriptors(
			String documentUri, String id) throws InvalidSPDXAnalysisException {
		return getPropertyValueDescriptors(documentUriIdToUri(documentUri, id, baseStore));
	}
	
	@Override
	public void setValue(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		baseStore.setValue(objectUri, propertyDescriptor, value);
	}

	public void setValue(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		setValue(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, value);
	}

	@Override
	public Optional<Object> getValue(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.getValue(objectUri, propertyDescriptor);
	}
	
	public Optional<Object> getValue(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return getValue(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public String getNextId(IdType idType, String documentUri)
			throws InvalidSPDXAnalysisException {
		Objects.requireNonNull(documentUri, "SPDX V2 requires a namespace for generating next ID's");
		if (documentUri.contains("://spdx.org/licenses")) {
			return baseStore.getNextId(idType, documentUri);
		} else {
			return baseStore.getNextId(idType, documentUri + "#");
		}
	}

	@Override
	public void removeProperty(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		baseStore.removeProperty(objectUri, propertyDescriptor);
	}
	
	public void removeProperty(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		removeProperty(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public Stream<TypedValue> getAllItems(String nameSpace, String typeFilter)
			throws InvalidSPDXAnalysisException {
		return baseStore.getAllItems(nameSpace, typeFilter);
	}

	@Override
	public IModelStoreLock enterCriticalSection(boolean readLockRequested) throws InvalidSPDXAnalysisException {
		return baseStore.enterCriticalSection(readLockRequested);
	}

	public IModelStoreLock enterCriticalSection(String documentUri,
			boolean readLockRequested) throws InvalidSPDXAnalysisException {
		return enterCriticalSection(readLockRequested);
	}

	@Override
	public void leaveCriticalSection(IModelStoreLock lock) {
		baseStore.leaveCriticalSection(lock);
	}

	@Override
	public boolean removeValueFromCollection(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.removeValueFromCollection(objectUri, propertyDescriptor, value);
	}
	
	public boolean removeValueFromCollection(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return removeValueFromCollection(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, value);
	}

	@Override
	public int collectionSize(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.collectionSize(objectUri, propertyDescriptor);
	}
	
	public int collectionSize(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return collectionSize(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public boolean collectionContains(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.collectionContains(objectUri, propertyDescriptor, value);
	}
	
	public boolean collectionContains(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return collectionContains(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, value);
	}

	@Override
	public void clearValueCollection(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		baseStore.clearValueCollection(objectUri, propertyDescriptor);
	}
	
	public void clearValueCollection(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		clearValueCollection(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public boolean addValueToCollection(String objectUri,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return baseStore.addValueToCollection(objectUri, propertyDescriptor, value);
	}
	
	public boolean addValueToCollection(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Object value)
			throws InvalidSPDXAnalysisException {
		return addValueToCollection(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, value);
	}

	@Override
	public Iterator<Object> listValues(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.listValues(objectUri, propertyDescriptor);
	}
	
	public Iterator<Object> listValues(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return listValues(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public boolean isCollectionMembersAssignableTo(String objectUri,
			PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return baseStore.isCollectionMembersAssignableTo(objectUri, propertyDescriptor, clazz);
	}
	
	public boolean isCollectionMembersAssignableTo(String documentUri,
			String id, PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return isCollectionMembersAssignableTo(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, clazz);
	}

	@Override
	public boolean isPropertyValueAssignableTo(String objectUri,
			PropertyDescriptor propertyDescriptor, Class<?> clazz, String specVersion)
			throws InvalidSPDXAnalysisException {
		return baseStore.isPropertyValueAssignableTo(objectUri, propertyDescriptor, clazz, specVersion);
	}
	
	public boolean isPropertyValueAssignableTo(String documentUri, String id,
			PropertyDescriptor propertyDescriptor, Class<?> clazz)
			throws InvalidSPDXAnalysisException {
		return isPropertyValueAssignableTo(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor, 
				clazz, LATEST_SPDX_2X_VERSION);
	}

	@Override
	public boolean isCollectionProperty(String objectUri,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return baseStore.isCollectionProperty(objectUri, propertyDescriptor);
	}
	
	public boolean isCollectionProperty(String documentUri, String id,
			PropertyDescriptor propertyDescriptor)
			throws InvalidSPDXAnalysisException {
		return isCollectionProperty(documentUriIdToUri(documentUri, id, baseStore), propertyDescriptor);
	}

	@Override
	public IdType getIdType(String objectUri) {
		return baseStore.getIdType(objectUri);
	}

	@Override
	public Optional<String> getCaseSensisitiveId(String documentUri,
			String caseInsensisitiveId) {
		return baseStore.getCaseSensisitiveId(documentUri, caseInsensisitiveId);
	}

	@Override
	public Optional<TypedValue> getTypedValue(String objectUri)
			throws InvalidSPDXAnalysisException {
		return baseStore.getTypedValue(objectUri);
	}
	
	public Optional<TypedValue> getTypedValue(String documentUri, String id)
			throws InvalidSPDXAnalysisException {
		return getTypedValue(documentUriIdToUri(documentUri, id, baseStore));
	}

	@Override
	public void delete(String documentUri)
			throws InvalidSPDXAnalysisException {
		baseStore.delete(documentUri);
	}

	public void delete(String documentUri, String id)
			throws InvalidSPDXAnalysisException {
		delete(documentUriIdToUri(documentUri, id, baseStore));
	}

	/**
	 * @return the store this store wraps
	 */
	public IModelStore getBaseModelStore() {
		return this.baseStore;
	}
	
	@Override
	public boolean equals(Object comp) {
		return comp instanceof CompatibleModelStoreWrapper && super.equals(((CompatibleModelStoreWrapper)comp).getBaseModelStore());
		// TODO: Return true if the base is equal since this contains no properties
	}
	
	@Override
	public int hashCode() {
		return 11 ^ super.hashCode();
	}

	@Override
	public IExternalElementInfo addExternalReference(String externalObjectUri,
			String collectionUri, IExternalElementInfo externalElementInfo) {
		return this.baseStore.addExternalReference(externalObjectUri, collectionUri, externalElementInfo);
	}

	@Override
	public Map<String, IExternalElementInfo> getExternalReferenceMap(
			String externalObjectUri) {
		return this.baseStore.getExternalReferenceMap(externalObjectUri);
	}

	@Override
	public IExternalElementInfo getExternalElementInfo(String externalObjectUri,
			String collectionUri) {
		return this.baseStore.getExternalElementInfo(externalObjectUri, collectionUri);
	}

}
