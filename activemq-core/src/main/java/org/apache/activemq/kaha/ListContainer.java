begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * Represents a container of persistent objects in the store Acts as a map, but  * values can be retrieved in insertion order  *   * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|ListContainer
parameter_list|<
name|V
parameter_list|>
extends|extends
name|List
argument_list|<
name|V
argument_list|>
block|{
comment|/**      * The container is created or retrieved in an unloaded state. load      * populates the container will all the indexes used etc and should be      * called before any operations on the container      */
specifier|public
name|void
name|load
parameter_list|()
function_decl|;
comment|/**      * unload indexes from the container      *       */
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
comment|/**      * For homogenous containers can set a custom marshaller for loading values      * The default uses Object serialization      *       * @param marshaller      */
specifier|public
name|void
name|setMarshaller
parameter_list|(
name|Marshaller
name|marshaller
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
comment|/**      * Inserts the given element at the beginning of this list.      *       * @param o the element to be inserted at the beginning of this list.      */
specifier|public
name|void
name|addFirst
parameter_list|(
name|V
name|o
parameter_list|)
function_decl|;
comment|/**      * Appends the given element to the end of this list. (Identical in function      * to the<tt>add</tt> method; included only for consistency.)      *       * @param o the element to be inserted at the end of this list.      */
specifier|public
name|void
name|addLast
parameter_list|(
name|V
name|o
parameter_list|)
function_decl|;
comment|/**      * Removes and returns the first element from this list.      *       * @return the first element from this list.      * @throws NoSuchElementException if this list is empty.      */
specifier|public
name|V
name|removeFirst
parameter_list|()
function_decl|;
comment|/**      * Removes and returns the last element from this list.      *       * @return the last element from this list.      * @throws NoSuchElementException if this list is empty.      */
specifier|public
name|V
name|removeLast
parameter_list|()
function_decl|;
comment|/**      * remove an objecr from the list without retrieving the old value from the      * store      *       * @param position      * @return true if successful      */
specifier|public
name|boolean
name|doRemove
parameter_list|(
name|int
name|position
parameter_list|)
function_decl|;
comment|/**      * add an Object to the list but get a StoreEntry of its position      *       * @param object      * @return the entry in the Store      */
specifier|public
name|StoreEntry
name|placeLast
parameter_list|(
name|V
name|object
parameter_list|)
function_decl|;
comment|/**      * insert an Object in first position int the list but get a StoreEntry of      * its position      *       * @param object      * @return the location in the Store      */
specifier|public
name|StoreEntry
name|placeFirst
parameter_list|(
name|V
name|object
parameter_list|)
function_decl|;
comment|/**      * Advanced feature = must ensure the object written doesn't overwrite other      * objects in the container      *       * @param entry      * @param object      */
specifier|public
name|void
name|update
parameter_list|(
name|StoreEntry
name|entry
parameter_list|,
name|V
name|object
parameter_list|)
function_decl|;
comment|/**      * Retrieve an Object from the Store by its location      *       * @param entry      * @return the Object at that entry      */
specifier|public
name|V
name|get
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
function_decl|;
comment|/**      * Get the StoreEntry for the first item of the list      *       * @return the first StoreEntry or null if the list is empty      */
specifier|public
name|StoreEntry
name|getFirst
parameter_list|()
function_decl|;
comment|/**      * Get the StoreEntry for the last item of the list      *       * @return the last StoreEntry or null if the list is empty      */
specifier|public
name|StoreEntry
name|getLast
parameter_list|()
function_decl|;
comment|/**      * Get the next StoreEntry from the list      *       * @param entry      * @return the next StoreEntry or null      */
specifier|public
name|StoreEntry
name|getNext
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
function_decl|;
comment|/**      * Get the previous StoreEntry from the list      *       * @param entry      * @return the previous store entry or null      */
specifier|public
name|StoreEntry
name|getPrevious
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
function_decl|;
comment|/**      * remove the Object at the StoreEntry      *       * @param entry      * @return true if successful      */
specifier|public
name|boolean
name|remove
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
function_decl|;
comment|/**      * It's possible that a StoreEntry could be come stale this will return an      * upto date entry for the StoreEntry position      *       * @param entry old entry      * @return a refreshed StoreEntry      */
specifier|public
name|StoreEntry
name|refresh
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

