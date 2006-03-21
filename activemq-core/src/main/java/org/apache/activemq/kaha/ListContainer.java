begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
comment|/**  *Represents a container of persistent objects in the store  *Acts as a map, but values can be retrieved in insertion order  *   * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|ListContainer
extends|extends
name|List
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
comment|/**      * For homogenous containers can set a custom marshaller for loading values      * The default uses Object serialization      * @param marshaller       */
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
comment|/**      * Inserts the given element at the beginning of this list.      *      * @param o the element to be inserted at the beginning of this list.      */
specifier|public
name|void
name|addFirst
parameter_list|(
name|Object
name|o
parameter_list|)
function_decl|;
comment|/**      * Appends the given element to the end of this list.  (Identical in      * function to the<tt>add</tt> method; included only for consistency.)      *      * @param o the element to be inserted at the end of this list.      */
specifier|public
name|void
name|addLast
parameter_list|(
name|Object
name|o
parameter_list|)
function_decl|;
comment|/**      * Removes and returns the first element from this list.      *      * @return the first element from this list.      * @throws    NoSuchElementException if this list is empty.      */
specifier|public
name|Object
name|removeFirst
parameter_list|()
function_decl|;
comment|/**      * Removes and returns the last element from this list.      *      * @return the last element from this list.      * @throws    NoSuchElementException if this list is empty.      */
specifier|public
name|Object
name|removeLast
parameter_list|()
function_decl|;
comment|/**      * remove an objecr from the list without retrieving the old value from the store      * @param position      * @return true if successful      */
specifier|public
name|boolean
name|doRemove
parameter_list|(
name|int
name|position
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

