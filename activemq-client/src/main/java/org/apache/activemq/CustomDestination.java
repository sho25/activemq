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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueReceiver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSender
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicPublisher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSubscriber
import|;
end_import

begin_comment
comment|/**  * Represents a hook to allow the support of custom destinations  * such as to support<a href="http://activemq.apache.org/camel/">Apache Camel</a>  * to create and manage endpoints  *  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|CustomDestination
extends|extends
name|Destination
block|{
comment|// Consumers
comment|//-----------------------------------------------------------------------
name|MessageConsumer
name|createConsumer
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|String
name|messageSelector
parameter_list|)
function_decl|;
name|MessageConsumer
name|createConsumer
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
function_decl|;
name|TopicSubscriber
name|createSubscriber
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
function_decl|;
name|TopicSubscriber
name|createDurableSubscriber
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
function_decl|;
name|QueueReceiver
name|createReceiver
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|String
name|messageSelector
parameter_list|)
function_decl|;
comment|// Producers
comment|//-----------------------------------------------------------------------
name|MessageProducer
name|createProducer
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|)
throws|throws
name|JMSException
function_decl|;
name|TopicPublisher
name|createPublisher
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|)
throws|throws
name|JMSException
function_decl|;
name|QueueSender
name|createSender
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|)
throws|throws
name|JMSException
function_decl|;
block|}
end_interface

end_unit

