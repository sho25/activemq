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
name|policy
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
name|broker
operator|.
name|Broker
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
name|broker
operator|.
name|region
operator|.
name|Destination
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
name|region
operator|.
name|Subscription
import|;
end_import

begin_comment
comment|/**  * Interface for a strategy for dealing with slow consumers  */
end_comment

begin_interface
specifier|public
interface|interface
name|SlowConsumerStrategy
block|{
comment|/**      * Slow consumer event.      *      * @param context      *      Connection context of the subscription.      * @param subs      *      The subscription object for the slow consumer.      */
name|void
name|slowConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|subs
parameter_list|)
function_decl|;
comment|/**      * Sets the Broker instance which can provide a Scheduler among other things.      *      * @param broker      *      The running Broker.      */
name|void
name|setBrokerService
parameter_list|(
name|Broker
name|broker
parameter_list|)
function_decl|;
comment|/**      * For Strategies that need to examine assigned destination for slow consumers      * periodically the destination is assigned here.      *      * If the strategy doesn't is event driven it can just ignore assigned destination.      *      * @param destination      *      A destination to add to a watch list.      */
name|void
name|addDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

