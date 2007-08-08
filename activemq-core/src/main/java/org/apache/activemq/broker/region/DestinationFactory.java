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
name|Set
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
name|broker
operator|.
name|ConnectionContext
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
name|ActiveMQDestination
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
name|ActiveMQTopic
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
name|SubscriptionInfo
import|;
end_import

begin_comment
comment|/**  * Used to create Destinations. One instance of DestinationFactory is used per BrokerService.   *   * @author fateev@amazon.com  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DestinationFactory
block|{
comment|/**      * Create destination implementation.      */
specifier|abstract
specifier|public
name|Destination
name|createDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|DestinationStatistics
name|destinationStatistics
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Returns a set of all the {@link org.apache.activemq.command.ActiveMQDestination}      * objects that the persistence store is aware exist.      */
specifier|abstract
specifier|public
name|Set
name|getDestinations
parameter_list|()
function_decl|;
comment|/**      * Lists all the durable subscirptions for a given destination.      */
specifier|abstract
specifier|public
name|SubscriptionInfo
index|[]
name|getAllDurableSubscriptions
parameter_list|(
name|ActiveMQTopic
name|topic
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|abstract
specifier|public
name|long
name|getLastMessageBrokerSequenceId
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|abstract
specifier|public
name|void
name|setRegionBroker
parameter_list|(
name|RegionBroker
name|regionBroker
parameter_list|)
function_decl|;
block|}
end_class

end_unit

