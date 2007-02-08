begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *Represents a container of persistent objects in the store  *Acts as a map, but values can be retrieved in insertion order  *   * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|MapContainer
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|/**      * The container is created or retrieved in       * an unloaded state.      * load populates the container will all the indexes used etc      * and should be called before any operations on the container      */
specifier|public
name|void
name|load
parameter_list|()
function_decl|;
comment|/**      * unload indexes from the container      *      */
specifier|public
name|void
name|unload
parameter_list|()
function_decl|;
comment|/**      * @return true if the indexes are loaded      */
specifier|public
name|boolean
name|isLoaded
parameter_list|()
function_decl|;
comment|/**      * For homogenous containers can set a custom marshaller for loading keys      * The default uses Object serialization      * @param keyMarshaller      */
specifier|public
name|void
name|setKeyMarshaller
parameter_list|(
name|Marshaller
argument_list|<
name|K
argument_list|>
name|keyMarshaller
parameter_list|)
function_decl|;
comment|/**      * For homogenous containers can set a custom marshaller for loading values      * The default uses Object serialization      * @param valueMarshaller           */
specifier|public
name|void
name|setValueMarshaller
parameter_list|(
name|Marshaller
argument_list|<
name|V
argument_list|>
name|valueMarshaller
parameter_list|)
function_decl|;
comment|/**      * @return the id the MapContainer was create with      */
specifier|public
name|Object
name|getId
parameter_list|()
function_decl|;
comment|/**      * @return the number of values in the container      */
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * @return true if there are no values stored in the container      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**      * @param key       * @return true if the container contains the key      */
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
comment|/**      * Get the value associated with the key      * @param key       * @return the value associated with the key from the store      */
specifier|public
name|V
name|get
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
comment|/**      * @param o       * @return true if the MapContainer contains the value o      */
specifier|public
name|boolean
name|containsValue
parameter_list|(
name|K
name|o
parameter_list|)
function_decl|;
comment|/**      * Add add entries in the supplied Map      * @param map      */
specifier|public
name|void
name|putAll
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|)
function_decl|;
comment|/**      * @return a Set of all the keys      */
specifier|public
name|Set
argument_list|<
name|K
argument_list|>
name|keySet
parameter_list|()
function_decl|;
comment|/**      * @return a collection of all the values - the values will be lazily pulled out of the      * store if iterated etc.      */
specifier|public
name|Collection
argument_list|<
name|V
argument_list|>
name|values
parameter_list|()
function_decl|;
comment|/**      * @return a Set of all the Map.Entry instances - the values will be lazily pulled out of the      * store if iterated etc.      */
specifier|public
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
function_decl|;
comment|/**      * Add an entry      * @param key      * @param value      * @return the old value for the key      */
specifier|public
name|V
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
function_decl|;
comment|/**      * remove an entry associated with the key      * @param key       * @return the old value assocaited with the key or null      */
specifier|public
name|V
name|remove
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
comment|/**      * empty the container      */
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**      * Add an entry to the Store Map      * @param key      * @param Value      * @return the StoreEntry associated with the entry      */
specifier|public
name|StoreEntry
name|place
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|Value
parameter_list|)
function_decl|;
comment|/**      * Remove an Entry from ther Map      * @param entry      */
specifier|public
name|void
name|remove
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
function_decl|;
comment|/**      * Get the Key object from it's location      * @param keyLocation      * @return the key for the entry      */
specifier|public
name|K
name|getKey
parameter_list|(
name|StoreEntry
name|keyLocation
parameter_list|)
function_decl|;
comment|/**      * Get the value from it's location      * @param Valuelocation      * @return the Object      */
specifier|public
name|V
name|getValue
parameter_list|(
name|StoreEntry
name|Valuelocation
parameter_list|)
function_decl|;
comment|/** Get the StoreEntry for the first value in the Map     *      * @return the first StoreEntry or null if the map is empty     */
specifier|public
name|StoreEntry
name|getFirst
parameter_list|()
function_decl|;
comment|/**     * Get the StoreEntry for the last value item of the Map     *      * @return the last StoreEntry or null if the list is empty     */
specifier|public
name|StoreEntry
name|getLast
parameter_list|()
function_decl|;
comment|/**     * Get the next StoreEntry value from the map     *      * @param entry     * @return the next StoreEntry or null     */
specifier|public
name|StoreEntry
name|getNext
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
function_decl|;
comment|/**     * Get the previous StoreEntry from the map     *      * @param entry     * @return the previous store entry or null     */
specifier|public
name|StoreEntry
name|getPrevious
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
function_decl|;
comment|/**     * It's possible that a StoreEntry could be come stale     * this will return an upto date entry for the StoreEntry position     * @param entry old entry     * @return a refreshed StoreEntry     */
specifier|public
name|StoreEntry
name|refresh
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
function_decl|;
comment|/**     * Get the StoreEntry associated with the key     * @param key     * @return the StoreEntry     */
specifier|public
name|StoreEntry
name|getEntry
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

