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
extends|extends
name|Map
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
name|keyMarshaller
parameter_list|)
function_decl|;
comment|/**      * For homogenous containers can set a custom marshaller for loading values      * The default uses Object serialization      * @param valueMarshaller           */
specifier|public
name|void
name|setValueMarshaller
parameter_list|(
name|Marshaller
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
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**      * Get the value associated with the key      * @param key       * @return the value associated with the key from the store      */
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**      * @param o       * @return true if the MapContainer contains the value o      */
specifier|public
name|boolean
name|containsValue
parameter_list|(
name|Object
name|o
parameter_list|)
function_decl|;
comment|/**      * Add add entries in the supplied Map      * @param map      */
specifier|public
name|void
name|putAll
parameter_list|(
name|Map
name|map
parameter_list|)
function_decl|;
comment|/**      * @return a Set of all the keys      */
specifier|public
name|Set
name|keySet
parameter_list|()
function_decl|;
comment|/**      * @return a collection of all the values - the values will be lazily pulled out of the      * store if iterated etc.      */
specifier|public
name|Collection
name|values
parameter_list|()
function_decl|;
comment|/**      * @return a Set of all the Map.Entry instances - the values will be lazily pulled out of the      * store if iterated etc.      */
specifier|public
name|Set
name|entrySet
parameter_list|()
function_decl|;
comment|/**      * Add an entry      * @param key      * @param value      * @return the old value for the key      */
specifier|public
name|Object
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * remove an entry associated with the key      * @param key       * @return the old value assocaited with the key or null      */
specifier|public
name|Object
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**      * empty the container      */
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

