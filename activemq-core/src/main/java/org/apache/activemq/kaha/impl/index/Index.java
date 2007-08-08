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
operator|.
name|impl
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|Marshaller
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|StoreEntry
import|;
end_import

begin_comment
comment|/**  * Simplier than a Map  *   * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Index
block|{
comment|/**      * clear the index      *       * @throws IOException      *       */
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * @param key      * @return true if it contains the key      * @throws IOException      */
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * remove the index key      *       * @param key      * @return StoreEntry removed      * @throws IOException      */
specifier|public
name|StoreEntry
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * store the key, item      *       * @param key      * @param entry      * @throws IOException      */
specifier|public
name|void
name|store
parameter_list|(
name|Object
name|key
parameter_list|,
name|StoreEntry
name|entry
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @param key      * @return the entry      * @throws IOException      */
specifier|public
name|StoreEntry
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @return true if the index is transient      */
specifier|public
name|boolean
name|isTransient
parameter_list|()
function_decl|;
comment|/**      * load indexes      */
specifier|public
name|void
name|load
parameter_list|()
function_decl|;
comment|/**      * unload indexes      *       * @throws IOException      */
specifier|public
name|void
name|unload
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Set the marshaller for key objects      *       * @param marshaller      */
specifier|public
name|void
name|setKeyMarshaller
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

