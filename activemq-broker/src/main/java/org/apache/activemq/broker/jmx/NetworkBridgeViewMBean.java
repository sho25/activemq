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
name|jmx
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|Service
import|;
end_import

begin_interface
specifier|public
interface|interface
name|NetworkBridgeViewMBean
extends|extends
name|Service
block|{
name|String
name|getLocalAddress
parameter_list|()
function_decl|;
name|String
name|getRemoteAddress
parameter_list|()
function_decl|;
name|String
name|getRemoteBrokerName
parameter_list|()
function_decl|;
name|String
name|getLocalBrokerName
parameter_list|()
function_decl|;
name|long
name|getEnqueueCounter
parameter_list|()
function_decl|;
name|long
name|getDequeueCounter
parameter_list|()
function_decl|;
name|boolean
name|isCreatedByDuplex
parameter_list|()
function_decl|;
name|void
name|resetStats
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

