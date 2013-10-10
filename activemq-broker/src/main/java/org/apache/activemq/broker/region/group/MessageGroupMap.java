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
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|group
package|;
end_package

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
name|activemq
operator|.
name|command
operator|.
name|ConsumerId
import|;
end_import

begin_comment
comment|/**  * Represents a map of JMSXGroupID values to consumer IDs  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageGroupMap
block|{
name|void
name|put
parameter_list|(
name|String
name|groupId
parameter_list|,
name|ConsumerId
name|consumerId
parameter_list|)
function_decl|;
name|ConsumerId
name|get
parameter_list|(
name|String
name|groupId
parameter_list|)
function_decl|;
name|ConsumerId
name|removeGroup
parameter_list|(
name|String
name|groupId
parameter_list|)
function_decl|;
name|MessageGroupSet
name|removeConsumer
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
function_decl|;
name|void
name|removeAll
parameter_list|()
function_decl|;
comment|/**      * @return  a map of group names and associated consumer Id      */
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getGroups
parameter_list|()
function_decl|;
name|String
name|getType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

