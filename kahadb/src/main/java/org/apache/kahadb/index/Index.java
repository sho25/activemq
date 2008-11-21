begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|kahadb
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|page
operator|.
name|Transaction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
operator|.
name|Marshaller
import|;
end_import

begin_comment
comment|/**  * Simpler than a Map  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|Index
parameter_list|<
name|Key
parameter_list|,
name|Value
parameter_list|>
block|{
comment|/**      * Set the marshaller for key objects      *       * @param marshaller      */
name|void
name|setKeyMarshaller
parameter_list|(
name|Marshaller
argument_list|<
name|Key
argument_list|>
name|marshaller
parameter_list|)
function_decl|;
comment|/**      * Set the marshaller for key objects      *       * @param marshaller      */
name|void
name|setValueMarshaller
parameter_list|(
name|Marshaller
argument_list|<
name|Value
argument_list|>
name|marshaller
parameter_list|)
function_decl|;
comment|/**      * load indexes      */
name|void
name|load
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * unload indexes      *       * @throws IOException      */
name|void
name|unload
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * clear the index      *       * @throws IOException      *       */
name|void
name|clear
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @param key      * @return true if it contains the key      * @throws IOException      */
name|boolean
name|containsKey
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * remove the index key      *       * @param key      * @return StoreEntry removed      * @throws IOException      */
name|Value
name|remove
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * store the key, item      *       * @param key      * @param entry      * @throws IOException      */
name|Value
name|put
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|,
name|Value
name|entry
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @param key      * @return the entry      * @throws IOException      */
name|Value
name|get
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @return true if the index is transient      */
name|boolean
name|isTransient
parameter_list|()
function_decl|;
comment|/**      * @param tx      * @return      * @throws IOException      * @trhows UnsupportedOperationException       *         if the index does not support fast iteration of the elements.      */
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iterator
parameter_list|(
specifier|final
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedOperationException
function_decl|;
block|}
end_interface

end_unit

