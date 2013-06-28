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
name|network
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
name|command
operator|.
name|BrokerId
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
name|ConsumerInfo
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
name|NetworkBridgeFilter
import|;
end_import

begin_interface
specifier|public
interface|interface
name|NetworkBridgeFilterFactory
block|{
comment|// create a dispatch filter for network consumers, default impl will not send a message back to
comment|// its origin to prevent looping, the down side is that messages can get stuck
name|NetworkBridgeFilter
name|create
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|,
name|BrokerId
index|[]
name|remoteBrokerPath
parameter_list|,
name|int
name|messageTTL
parameter_list|,
name|int
name|consumerTTL
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

