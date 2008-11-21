begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Interface used to selectively visit the entries in a BTree.  *   * @param<Key>  * @param<Value>  */
end_comment

begin_interface
specifier|public
interface|interface
name|BTreeVisitor
parameter_list|<
name|Key
parameter_list|,
name|Value
parameter_list|>
block|{
comment|/**      * Do you want to visit the range of BTree entries between the first and and second key?      *       * @param first if null indicates the range of values before the second key.       * @param second if null indicates the range of values after the first key.      * @return true if you want to visit the values between the first and second key.      */
name|boolean
name|isInterestedInKeysBetween
parameter_list|(
name|Key
name|first
parameter_list|,
name|Key
name|second
parameter_list|)
function_decl|;
comment|/**      * The keys and values of a BTree leaf node.      *       * @param keys      * @param values      */
name|void
name|visit
parameter_list|(
name|List
argument_list|<
name|Key
argument_list|>
name|keys
parameter_list|,
name|List
argument_list|<
name|Value
argument_list|>
name|values
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

